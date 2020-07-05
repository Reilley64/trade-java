package com.trade.trade;

import com.trade.trade.api.controllers.AssetController;
import com.trade.trade.clients.iexcloud.IEXCloudClient;
import com.trade.trade.clients.iexcloud.objects.IEXAssetSymbol;
import com.trade.trade.domain.models.Asset;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

//@Component
public class DemoData {
    private final AssetController assetController;

    public DemoData(AssetController assetController) {
        this.assetController = assetController;
    }

    IEXCloudClient iexCloudClient = new IEXCloudClient();

    @EventListener
    public void appReady() {
        IEXAssetSymbol[] assetSymbols = iexCloudClient.getAssetSymbols();
        Stream.of(assetSymbols).parallel().forEach(assetSymbol -> {
            try {
                Asset asset = new Asset();
                asset.setSymbol(assetSymbol.getSymbol());
                assetController.createStock(asset);
            } catch (Exception e) {
                System.out.print(assetSymbol.getSymbol());
                System.out.print(e.toString());
                System.out.print('\n');
            }
        });
        System.out.println("Demo data done");
    }
}
