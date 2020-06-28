package com.trade.trade.api.controllers;

import com.trade.trade.clients.iexcloud.IEXCloudClient;
import com.trade.trade.clients.iexcloud.objects.IEXStockHistoricalPrice;
import com.trade.trade.domain.exceptions.ResourceNotFoundException;
import com.trade.trade.domain.models.assets.Asset;
import com.trade.trade.domain.models.AssetValuation;
import com.trade.trade.api.repositories.assets.AssetRepository;
import com.trade.trade.api.repositories.AssetValuationRepository;
import org.springframework.web.bind.annotation.*;

import javax.xml.crypto.Data;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/assets/{assetSymbol}")
public class AssetValuationController {
    private final AssetValuationRepository repository;
    private final AssetRepository assetRepository;

    public AssetValuationController(AssetValuationRepository repository, AssetRepository assetRepository) {
        this.repository = repository;
        this.assetRepository = assetRepository;
    }

    IEXCloudClient iexCloudClient = new IEXCloudClient();

    @GetMapping("/valuations")
    public List<AssetValuation> getAllAssetValuations(@PathVariable String assetSymbol, @RequestParam(name = "range", required = false) String range) {
        Asset asset = assetRepository.findBySymbol(assetSymbol)
                .orElseThrow(() -> new ResourceNotFoundException(Asset.class, assetSymbol));

        List<AssetValuation> assetValuations = new ArrayList<>();
        if (range != null) {
            LocalDate startDate;

            switch (range) {
                case "1w":
                    startDate = LocalDate.now().minusDays(7);
                    assetValuations = repository.findByAssetSymbolAndDateBetweenOrderByDateDesc(
                            asset.getSymbol(),
                            java.util.Date.from(startDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()),
                            new Date(System.currentTimeMillis())
                    );
                    break;

                case "1m":
                    startDate = LocalDate.now().minusMonths(1);
                    assetValuations = repository.findByAssetSymbolAndDateBetweenOrderByDateDesc(
                            asset.getSymbol(),
                            java.util.Date.from(startDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()),
                            new Date(System.currentTimeMillis())
                    );
                    break;

                case "3m":
                    startDate = LocalDate.now().minusMonths(3);
                    assetValuations = repository.findByAssetSymbolAndDateBetweenOrderByDateDesc(
                            asset.getSymbol(),
                            java.util.Date.from(startDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()),
                            new Date(System.currentTimeMillis())
                    );
                    break;

                case "6m":
                    startDate = LocalDate.now().minusMonths(6);
                    assetValuations = repository.findByAssetSymbolAndDateBetweenOrderByDateDesc(
                            asset.getSymbol(),
                            java.util.Date.from(startDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()),
                            new Date(System.currentTimeMillis())
                    );
                    break;

                case "1y":
                    startDate = LocalDate.now().minusYears(1);
                    assetValuations = repository.findByAssetSymbolAndDateBetweenOrderByDateDesc(
                            asset.getSymbol(),
                            java.util.Date.from(startDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()),
                            new Date(System.currentTimeMillis())
                    );
                    break;
            }
        } else {
            assetValuations = repository.findByAssetSymbolOrderByDateDesc(asset.getSymbol());
        }

        if (assetValuations.isEmpty()) {
            IEXStockHistoricalPrice[] historicalPrices = iexCloudClient.getStockHistoricalPrices(asset);
            for (IEXStockHistoricalPrice historicalPrice : historicalPrices) {
                AssetValuation assetValuation = new AssetValuation();
                assetValuation.setDate(historicalPrice.getDate());
                assetValuation.setOpen((long) (historicalPrice.getOpen() * 100));
                assetValuation.setClose((long) (historicalPrice.getClose() * 100));
                assetValuation.setHigh((long) (historicalPrice.getHigh() * 100));
                assetValuation.setLow((long) (historicalPrice.getLow() * 100));
                assetValuation.setVolume(historicalPrice.getVolume());
                assetValuation.setAsset(asset);
                asset.getAssetValuation().add(assetValuation);
            }
            assetRepository.save(asset);
            return asset.getAssetValuation();
        }

        LocalDate lastTradingDate = LocalDate.now().getDayOfWeek() == DayOfWeek.SATURDAY || LocalDate.now().getDayOfWeek() == DayOfWeek.SUNDAY
                ? LocalDate.now().with(TemporalAdjusters.previous(DayOfWeek.FRIDAY))
                : LocalDate.now();
        if (assetValuations.iterator().next().getDate().before(Date.from(lastTradingDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()))) {
            Date startDate = assetValuations.iterator().next().getDate();
            List<LocalDate> datesUntilPresent = startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().plusDays(1).datesUntil(LocalDate.now()).collect(Collectors.toList());
            for (LocalDate localDate : datesUntilPresent) {
                if (!(localDate.getDayOfWeek() == DayOfWeek.SATURDAY || localDate.getDayOfWeek() == DayOfWeek.SUNDAY)) {
                    IEXStockHistoricalPrice[] historicalPrices = iexCloudClient.getStockHistoricalPrices(asset, localDate);
                    if (historicalPrices.length != 0) {
                        IEXStockHistoricalPrice historicalPrice = historicalPrices[0];
                        AssetValuation assetValuation = new AssetValuation();
                        assetValuation.setDate(historicalPrice.getDate());
                        assetValuation.setOpen((long) (historicalPrice.getOpen() * 100));
                        assetValuation.setClose((long) (historicalPrice.getClose() * 100));
                        assetValuation.setHigh((long) (historicalPrice.getHigh() * 100));
                        assetValuation.setLow((long) (historicalPrice.getLow() * 100));
                        assetValuation.setVolume(historicalPrice.getVolume());
                        assetValuation.setAsset(asset);
                        asset.getAssetValuation().add(assetValuation);
                    }
                }
            }
            assetRepository.save(asset);
            return asset.getAssetValuation();
        }

        return assetValuations;
    }
}
