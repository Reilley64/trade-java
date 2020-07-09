package com.trade.trade.api.services;

import com.trade.trade.api.repositories.AssetValuationRepository;
import com.trade.trade.api.repositories.AssetRepository;
import com.trade.trade.clients.iexcloud.IEXCloudClient;
import com.trade.trade.clients.iexcloud.objects.IEXAssetHistoricalPrice;
import com.trade.trade.domain.models.AssetValuation;
import com.trade.trade.domain.models.Asset;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AssetValuationService {
    private final AssetValuationRepository repository;
    private final AssetRepository assetRepository;
    private final IEXCloudClient iexCloudClient;

    public AssetValuationService(AssetValuationRepository repository, AssetRepository assetRepository, IEXCloudClient iexCloudClient) {
        this.repository = repository;
        this.assetRepository = assetRepository;
        this.iexCloudClient = iexCloudClient;
    }

    public void updateAssetValuations(Asset asset) {
        Optional<AssetValuation> lastAssetValuation = repository.findFirstByAssetOrderByDateDesc(asset);

        if (lastAssetValuation.isEmpty()) {
            IEXAssetHistoricalPrice[] historicalPrices = iexCloudClient.getAssetHistoricalPrices(asset);
            for (IEXAssetHistoricalPrice historicalPrice : historicalPrices) {
                AssetValuation assetValuation = new AssetValuation();
                assetValuation.setDate(historicalPrice.getDate());
                assetValuation.setOpen((long) (historicalPrice.getOpen() * 100));
                assetValuation.setClose((long) (historicalPrice.getClose() * 100));
                assetValuation.setHigh((long) (historicalPrice.getHigh() * 100));
                assetValuation.setLow((long) (historicalPrice.getLow() * 100));
                assetValuation.setVolume(historicalPrice.getVolume());
                assetValuation.setAsset(asset);
                asset.getAssetValuations().add(assetValuation);
            }
            assetRepository.save(asset);
            return;
        }

        LocalDate lastRecordedDate = lastAssetValuation.get().getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDateTime nextTradingDate = LocalDateTime.of(lastRecordedDate.plusDays(1), LocalTime.of(4, 0));
        ZonedDateTime nextZonedTradingDate = ZonedDateTime.of(nextTradingDate, ZoneId.of("UTC-5"));
        if (nextZonedTradingDate.getDayOfWeek() == DayOfWeek.SUNDAY) nextZonedTradingDate = nextZonedTradingDate.plusDays(2);
        if (nextZonedTradingDate.getDayOfWeek() == DayOfWeek.MONDAY) nextZonedTradingDate = nextZonedTradingDate.plusDays(1);
        ZonedDateTime currentZonedDate = ZonedDateTime.now(ZoneId.of("UTC-5"));
        if (currentZonedDate.isAfter(nextZonedTradingDate)) {
            ZonedDateTime lastZonedRecordedDate = ZonedDateTime.of(LocalDateTime.of(lastRecordedDate, LocalTime.of(4, 0)), ZoneId.of("UTC-5"));
            List<LocalDate> datesUntilPresent = lastZonedRecordedDate.plusDays(1).toLocalDate().datesUntil(currentZonedDate.toLocalDate()).collect(Collectors.toList());
            for (LocalDate localDate : datesUntilPresent) {
                if (!(localDate.getDayOfWeek() == DayOfWeek.SATURDAY || localDate.getDayOfWeek() == DayOfWeek.SUNDAY)) {
                    IEXAssetHistoricalPrice[] historicalPrices = iexCloudClient.getAssetHistoricalPrices(asset, localDate);
                    if (historicalPrices.length != 0) {
                        IEXAssetHistoricalPrice historicalPrice = historicalPrices[0];
                        AssetValuation assetValuation = new AssetValuation();
                        assetValuation.setDate(historicalPrice.getDate());
                        assetValuation.setOpen((long) (historicalPrice.getOpen() * 100));
                        assetValuation.setClose((long) (historicalPrice.getClose() * 100));
                        assetValuation.setHigh((long) (historicalPrice.getHigh() * 100));
                        assetValuation.setLow((long) (historicalPrice.getLow() * 100));
                        assetValuation.setVolume(historicalPrice.getVolume());
                        assetValuation.setAsset(asset);
                        asset.getAssetValuations().add(assetValuation);
                    }
                }
            }
            assetRepository.save(asset);
        }
    }
}
