package com.trade.trade.api.controllers;

import com.trade.trade.api.repositories.AssetRepository;
import com.trade.trade.clients.iexcloud.IEXCloudClient;
import com.trade.trade.clients.iexcloud.objects.IEXStockProfile;
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
    public Page<Asset> getAllAssets() {
        return repository.findAll(PageRequest.of(0, 16, Sort.by("symbol")));
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
        return (long) (iexCloudClient.getStockQuote(asset).getLatestPrice() * 100);
    }

    @PostMapping("/assets")
    @PreAuthorize("hasAuthority('ADMIN')")
    public Asset createStock(@RequestBody Asset asset) {
        IEXStockProfile IEXStockProfile = iexCloudClient.getStockProfile(asset);
        asset.setName(IEXStockProfile.getCompanyName());
        asset.setImage("https://storage.googleapis.com/iex/api/logos/" + asset.getSymbol() + ".png");
        asset.setExchange(IEXStockProfile.getExchange());
        asset.setDescription(IEXStockProfile.getDescription());
        asset.setIndustry(IEXStockProfile.getIndustry());
        asset.setSector(IEXStockProfile.getSector());
        asset.setWebsite(IEXStockProfile.getWebsite());
        return repository.save(asset);
    }

}
