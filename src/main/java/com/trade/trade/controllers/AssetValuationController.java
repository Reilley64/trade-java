package com.trade.trade.controllers;

import com.trade.trade.exceptions.ResourceNotFoundException;
import com.trade.trade.models.Asset;
import com.trade.trade.models.AssetValuation;
import com.trade.trade.models.Order;
import com.trade.trade.models.User;
import com.trade.trade.repositories.AssetRepository;
import com.trade.trade.repositories.AssetValuationRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.UUID;

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
