package com.trade.trade.controllers;

import com.trade.trade.clients.financialmodelingprep.FinancialModelingPrepClient;
import com.trade.trade.clients.financialmodelingprep.objects.History;
import com.trade.trade.clients.financialmodelingprep.objects.HistoricalDailyPrice;
import com.trade.trade.clients.financialmodelingprep.objects.Profile;
import com.trade.trade.exceptions.ResourceNotFoundException;
import com.trade.trade.models.Asset;
import com.trade.trade.models.AssetValuation;
import com.trade.trade.repositories.AssetRepository;
import com.trade.trade.repositories.AssetValuationRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
public class AssetController {
    private final AssetRepository repository;

    private final AssetValuationRepository assetValuationRepository;

    public AssetController(AssetRepository repository, AssetValuationRepository assetValuationRepository) {
        this.repository = repository;
        this.assetValuationRepository = assetValuationRepository;
    }

    public FinancialModelingPrepClient financialModelingPrepClient = new FinancialModelingPrepClient();

    @GetMapping("/assets")
    public List<Asset> getAllAssets() {
        return repository.findAll();
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
        return (long) (financialModelingPrepClient.getRealTimePrice(asset).getPrice() * 100);
    }

    @PostMapping("/assets")
    @PreAuthorize("hasAuthority('ADMIN')")
    public Asset createAsset(@RequestBody Asset asset) {
        Profile profile = financialModelingPrepClient.getProfile(asset);
        asset.setExchange(profile.getProfile().getExchange());
        asset.setCompanyName(profile.getProfile().getCompanyName());
        asset.setDescription(profile.getProfile().getDescription());
        asset.setIndustry(profile.getProfile().getIndustry());
        asset.setSector(profile.getProfile().getSector());
        asset.setWebsite(profile.getProfile().getWebsite());
        asset.setImage(profile.getProfile().getImage());
        asset = repository.save(asset);

        HistoricalDailyPrice historicalDailyPrice = financialModelingPrepClient.getHistoricalDailyPrice(asset);
        List<AssetValuation> assetValuations = new ArrayList<>();
        for (History history : historicalDailyPrice.getHistorical()) {
            AssetValuation assetValuation = new AssetValuation();
            assetValuation.setAsset(asset);
            assetValuation.setDate(history.getDate());
            assetValuation.setOpen((long) (history.getOpen() * 100));
            assetValuation.setClose((long) (history.getClose() * 100));
            assetValuation.setHigh((long) (history.getHigh() * 100));
            assetValuation.setLow((long) (history.getLow() * 100));
            assetValuation.setVolume(history.getVolume());
            assetValuations.add(assetValuation);
        }
        assetValuationRepository.saveAll(assetValuations);

        return asset;
    }
}
