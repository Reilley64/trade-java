package com.trade.trade.api.controllers;

import com.trade.trade.api.repositories.AssetRepository;
import com.trade.trade.api.repositories.ExchangeRepository;
import com.trade.trade.clients.iexcloud.IEXCloudClient;
import com.trade.trade.domain.exceptions.ResourceNotFoundException;
import com.trade.trade.domain.models.Asset;
import com.trade.trade.domain.models.Exchange;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
public class AssetController {
    private final AssetRepository repository;
    private final ExchangeRepository exchangeRepository;

    public AssetController(AssetRepository repository, ExchangeRepository exchangeRepository) {
        this.repository = repository;
        this.exchangeRepository = exchangeRepository;
    }

    IEXCloudClient iexCloudClient = new IEXCloudClient();

    @GetMapping("/assets")
    public Page<Asset> getAllAssets(@RequestParam(name = "page") Integer page, @RequestParam(name = "size") Integer size,
                                    @RequestParam(name = "query", required = false) String query) {
        if (query != null && !query.isEmpty()) return repository.findByFilter(query, PageRequest.of(0, 16));
        return repository.findAll(PageRequest.of(page, size, Sort.by("symbol")));
    }

    @GetMapping("/assets/{assetUuid}")
    public Asset getAssetByUuid(@PathVariable UUID assetUuid) {
        return repository.findByUuid(assetUuid)
                .orElseThrow(() -> new ResourceNotFoundException(Asset.class, assetUuid));
    }

    @GetMapping("/exchanges/{exchangeUuid}/assets/{assetSymbol}")
    public Asset getAssetBySymbol(@PathVariable UUID exchangeUuid, @PathVariable String assetSymbol) {
        Exchange exchange = exchangeRepository.findByUuid(exchangeUuid)
                .orElseThrow(() -> new ResourceNotFoundException(Exchange.class, exchangeUuid));
        return repository.findBySymbolAndExchange(assetSymbol, exchange)
                .orElseThrow(() -> new ResourceNotFoundException(Asset.class, assetSymbol));
    }

    @GetMapping("/assets/{assetUuid}/price")
    public long getAssetPriceBySymbol(@PathVariable UUID assetUuid) {
        Asset asset = repository.findByUuid(assetUuid)
                .orElseThrow(() -> new ResourceNotFoundException(Asset.class, assetUuid));
        return (long) (iexCloudClient.getAssetQuote(asset).getLatestPrice() * 100);
    }
}
