package com.trade.trade.controllers;

import com.trade.trade.exceptions.ResourceNotFoundException;
import com.trade.trade.models.assets.Asset;
import com.trade.trade.models.AssetValuation;
import com.trade.trade.repositories.assets.AssetRepository;
import com.trade.trade.repositories.AssetValuationRepository;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/assets/{assetSymbol}")
public class AssetValuationController {
    private final AssetValuationRepository repository;
    private final AssetRepository assetRepository;

    public AssetValuationController(AssetValuationRepository repository, AssetRepository assetRepository) {
        this.repository = repository;
        this.assetRepository = assetRepository;
    }

    @GetMapping("/valuations")
    public List<AssetValuation> getAllAssetValuations(@PathVariable String assetSymbol,
                                                      @RequestParam(name = "start-date") Date startDate,
                                                      @RequestParam(name = "end-date") Date endDate) {
        assetRepository.findBySymbol(assetSymbol)
                .orElseThrow(() -> new ResourceNotFoundException(Asset.class, assetSymbol));
        return repository.findByAssetSymbolAndDateBetween(assetSymbol, startDate, endDate);
    }
}
