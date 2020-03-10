package com.trade.trade.controllers;

import com.trade.trade.clients.financialmodelingprep.FinancialModelingPrepClient;
import com.trade.trade.clients.financialmodelingprep.objects.HistoricalDailyPrice;
import com.trade.trade.clients.financialmodelingprep.objects.History;
import com.trade.trade.clients.financialmodelingprep.objects.StockProfile;
import com.trade.trade.exceptions.ResourceNotFoundException;
import com.trade.trade.models.AssetValuation;
import com.trade.trade.models.assets.Stock;
import com.trade.trade.repositories.AssetValuationRepository;
import com.trade.trade.repositories.assets.StockRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class StockController {
    private final StockRepository repository;
    private final AssetValuationRepository assetValuationRepository;

    public StockController(StockRepository repository, AssetValuationRepository assetValuationRepository) {
        this.repository = repository;
        this.assetValuationRepository = assetValuationRepository;
    }

    FinancialModelingPrepClient financialModelingPrepClient = new FinancialModelingPrepClient();

    @GetMapping("/stocks")
    public List<Stock> getAllStocks() {
        return repository.findAll();
    }

    @GetMapping("/stocks/{symbol}")
    public Stock getStockBySymbol(@PathVariable String symbol) {
        return repository.findBySymbol(symbol)
                .orElseThrow(() -> new ResourceNotFoundException(Stock.class, symbol));
    }

    @GetMapping("/stocks/{symbol}/price")
    public long getStockPriceBySymbol(@PathVariable String symbol) {
        Stock stock = repository.findBySymbol(symbol)
                .orElseThrow(() -> new ResourceNotFoundException(Stock.class, symbol));
        return (long) (financialModelingPrepClient.getStockRealTimePrice(stock).getPrice() * 100);
    }

    @PostMapping("/stocks")
    @PreAuthorize("hasAuthority('ADMIN')")
    public Stock createStock(@RequestBody Stock stock) {
        StockProfile profile = financialModelingPrepClient.getStockProfile(stock);
        stock.setExchange(profile.getProfile().getExchange());
        stock.setName(profile.getProfile().getCompanyName());
        stock.setDescription(profile.getProfile().getDescription());
        stock.setIndustry(profile.getProfile().getIndustry());
        stock.setSector(profile.getProfile().getSector());
        stock.setWebsite(profile.getProfile().getWebsite());
        stock.setImage(profile.getProfile().getImage());
        stock = repository.save(stock);

        HistoricalDailyPrice historicalDailyPrice = financialModelingPrepClient.getStockHistoricalDailyPrice(stock);
        List<AssetValuation> assetValuations = new ArrayList<>();
        for (History history : historicalDailyPrice.getHistorical()) {
            AssetValuation assetValuation = new AssetValuation();
            assetValuation.setAsset(stock);
            assetValuation.setDate(history.getDate());
            assetValuation.setOpen((long) (history.getOpen() * 100));
            assetValuation.setClose((long) (history.getClose() * 100));
            assetValuation.setHigh((long) (history.getHigh() * 100));
            assetValuation.setLow((long) (history.getLow() * 100));
            assetValuation.setVolume(history.getVolume());
            assetValuations.add(assetValuation);
        }
        assetValuationRepository.saveAll(assetValuations);

        return stock;
    }

}
