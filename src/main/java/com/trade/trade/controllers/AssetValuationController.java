package com.trade.trade.controllers;

import com.trade.trade.exceptions.ResourceNotFoundException;
import com.trade.trade.models.Asset;
import com.trade.trade.models.AssetValuation;
import com.trade.trade.models.Order;
import com.trade.trade.models.User;
import com.trade.trade.repositories.AssetRepository;
import com.trade.trade.repositories.AssetValuationRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/assets/{assetUuid}")
public class AssetValuationController {
    private final AssetValuationRepository repository;
    private final AssetRepository assetRepository;

    public AssetValuationController(AssetValuationRepository repository, AssetRepository assetRepository) {
        this.repository = repository;
        this.assetRepository = assetRepository;
    }

    @GetMapping("/valuations")
    public List<AssetValuation> getAllAssetValuations(@PathVariable UUID assetUuid) {
        assetRepository.findByUuid(assetUuid)
                .orElseThrow(() -> new ResourceNotFoundException(Asset.class, assetUuid));
        return repository.findByAssetUuid(assetUuid);
    }
}
