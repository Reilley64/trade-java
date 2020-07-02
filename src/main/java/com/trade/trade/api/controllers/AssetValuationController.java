package com.trade.trade.api.controllers;

import com.trade.trade.api.repositories.AssetValuationRepository;
import com.trade.trade.api.repositories.AssetRepository;
import com.trade.trade.api.services.AssetValuationService;
import com.trade.trade.domain.exceptions.ResourceNotFoundException;
import com.trade.trade.domain.models.AssetValuation;
import com.trade.trade.domain.models.Asset;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/assets/{assetSymbol}")
public class AssetValuationController {
    private final AssetValuationRepository repository;
    private final AssetValuationService service;
    private final AssetRepository assetRepository;

    public AssetValuationController(AssetValuationRepository repository, AssetValuationService service, AssetRepository assetRepository) {
        this.repository = repository;
        this.service = service;
        this.assetRepository = assetRepository;
    }

    @GetMapping("/valuations")
    public List<AssetValuation> getAllAssetValuations(@PathVariable String assetSymbol, @RequestParam(name = "range", required = false) String range) {
        Asset asset = assetRepository.findBySymbol(assetSymbol)
                .orElseThrow(() -> new ResourceNotFoundException(Asset.class, assetSymbol));

        service.updateAssetValuations(asset);

        if (range != null) {
            switch (range) {
                case "1w":
                    return repository.findByAssetAndDateBetweenOrderByDate(
                            asset,
                            java.util.Date.from(LocalDate.now().minusDays(7).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()),
                            new Date(System.currentTimeMillis())
                    );

                case "1m":
                    return repository.findByAssetAndDateBetweenOrderByDate(
                            asset,
                            java.util.Date.from(LocalDate.now().minusMonths(1).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()),
                            new Date(System.currentTimeMillis())
                    );

                case "3m":
                    return repository.findByAssetAndDateBetweenOrderByDate(
                            asset,
                            java.util.Date.from(LocalDate.now().minusMonths(3).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()),
                            new Date(System.currentTimeMillis())
                    );

                case "6m":
                    return repository.findByAssetAndDateBetweenOrderByDate(
                            asset,
                            java.util.Date.from(LocalDate.now().minusMonths(6).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()),
                            new Date(System.currentTimeMillis())
                    );

                case "1y":
                    return repository.findByAssetAndDateBetweenOrderByDate(
                            asset,
                            java.util.Date.from(LocalDate.now().minusYears(1).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()),
                            new Date(System.currentTimeMillis())
                    );
            }
        }

        return repository.findByAssetAndDateBetweenOrderByDate(
                asset,
                java.util.Date.from(LocalDate.now().minusDays(7).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()),
                new Date(System.currentTimeMillis())
        );
    }
}
