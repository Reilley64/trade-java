package com.trade.trade.api.controllers;

import com.trade.trade.clients.iexcloud.IEXCloudClient;
import com.trade.trade.clients.iexcloud.objects.IEXStockProfile;
import com.trade.trade.domain.exceptions.ResourceNotFoundException;
import com.trade.trade.domain.models.assets.Stock;
import com.trade.trade.api.repositories.assets.StockRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class StockController {
    private final StockRepository repository;

    public StockController(StockRepository repository) {
        this.repository = repository;
    }

    IEXCloudClient iexCloudClient = new IEXCloudClient();

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
        return (long) (iexCloudClient.getStockQuote(stock).getLatestPrice() * 100);
    }

    @PostMapping("/stocks")
    @PreAuthorize("hasAuthority('ADMIN')")
    public Stock createStock(@RequestBody Stock stock) {
        IEXStockProfile IEXStockProfile = iexCloudClient.getStockProfile(stock);
        stock.setName(IEXStockProfile.getCompanyName());
        stock.setImage("https://storage.googleapis.com/iex/api/logos/" + stock.getSymbol() + ".png");
        stock.setExchange(IEXStockProfile.getExchange());
        stock.setDescription(IEXStockProfile.getDescription());
        stock.setIndustry(IEXStockProfile.getIndustry());
        stock.setSector(IEXStockProfile.getSector());
        stock.setWebsite(IEXStockProfile.getWebsite());
        stock = repository.save(stock);
        return stock;
    }

}
