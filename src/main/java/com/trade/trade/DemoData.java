package com.trade.trade;

import com.trade.trade.api.controllers.AssetController;
import com.trade.trade.api.repositories.ExchangeRepository;
import com.trade.trade.api.services.AssetService;
import com.trade.trade.clients.iexcloud.IEXCloudClient;
import com.trade.trade.clients.iexcloud.objects.IEXAssetSymbol;
import com.trade.trade.clients.iexcloud.objects.IEXExchange;
import com.trade.trade.domain.models.Asset;
import com.trade.trade.domain.models.Exchange;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

//@Component
public class DemoData {
    private final AssetService assetService;
    private final ExchangeRepository exchangeRepository;

    public DemoData(AssetService assetService, ExchangeRepository exchangeRepository) {
        this.assetService = assetService;
        this.exchangeRepository = exchangeRepository;
    }

    IEXCloudClient iexCloudClient = new IEXCloudClient();

    @EventListener
    public void appReady(ApplicationReadyEvent event) {
        List<Exchange> exchangeList = new ArrayList<>();
        for (IEXExchange iexExchange : iexCloudClient.getExchanges()) {
            Exchange exchange = new Exchange();
            exchange.setReference(iexExchange.getExchange());
            exchange.setName(iexExchange.getDescription());
            exchange.setRegion(iexExchange.getRegion());
            exchangeList.add(exchangeRepository.save(exchange));
        }

        for (Exchange exchange : exchangeList) {
            if (exchange.getRegion().equals("US")) {
                IEXAssetSymbol[] assetSymbols = iexCloudClient.getAssetSymbols(exchange);
                Stream.of(assetSymbols).parallel().forEach(assetSymbol -> {
                    if (assetSymbol.getIsEnabled()) {
                        try {
                            Asset asset = new Asset();
                            asset.setSymbol(assetSymbol.getSymbol());
                            asset.setExchange(exchange);
                            assetService.createAsset(asset);
                        } catch (Exception e) {
                            System.out.print(assetSymbol.getSymbol());
                            System.out.print(e.toString());
                            System.out.print('\n');
                        }
                    }
                });
            }
        }

        System.out.println("Demo data done");
    }
}
