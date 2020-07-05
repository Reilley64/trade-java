package com.trade.trade.api.controllers;

import com.trade.trade.api.repositories.AssetRepository;
import com.trade.trade.clients.iexcloud.IEXCloudClient;
import com.trade.trade.clients.iexcloud.objects.IEXAssetProfile;
import com.trade.trade.domain.exceptions.ResourceNotFoundException;
import com.trade.trade.domain.models.Asset;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
public class AssetController {
    private final AssetRepository repository;

    public AssetController(AssetRepository repository) {
        this.repository = repository;
    }

    IEXCloudClient iexCloudClient = new IEXCloudClient();

    @GetMapping("/assets")
    public Page<Asset> getAllAssets(@RequestParam(name = "page") Integer page, @RequestParam(name = "size") Integer size,
                                    @RequestParam(name = "query", required = false) String query) {
        if (query != null && !query.isEmpty()) return repository.findByFilter(query, PageRequest.of(0, 16));
        return repository.findAll(PageRequest.of(page, size, Sort.by("symbol")));
    }

    @GetMapping("/assets/{symbol}")
    public Asset getAssetBySymbol(@PathVariable String symbol) {
        return repository.findBySymbol(symbol)
                .orElseThrow(() -> new ResourceNotFoundException(Asset.class, symbol));
    }

    @GetMapping("/assets/{symbol}/price")
    public long getAssetPriceBySymbol(@PathVariable String symbol) {
        Asset asset = repository.findBySymbol(symbol)
                .orElseThrow(() -> new ResourceNotFoundException(Asset.class, symbol));
        return (long) (iexCloudClient.getAssetQuote(asset).getLatestPrice() * 100);
    }

    @PostMapping("/assets")
    @PreAuthorize("hasAuthority('ADMIN')")
    public Asset createStock(@RequestBody Asset asset) {
        IEXAssetProfile IEXAssetProfile = iexCloudClient.getAssetProfile(asset);
        asset.setName(IEXAssetProfile.getCompanyName());
        asset.setImage("https://storage.googleapis.com/iex/api/logos/" + asset.getSymbol() + ".png");
        asset.setExchange(IEXAssetProfile.getExchange());
        asset.setDescription(IEXAssetProfile.getDescription());
        asset.setIndustry(IEXAssetProfile.getIndustry());
        asset.setSector(IEXAssetProfile.getSector());
        asset.setWebsite(IEXAssetProfile.getWebsite());
        return repository.save(asset);
    }

}
