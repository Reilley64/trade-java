package com.trade.trade.domain.datatransferobjects;

import com.trade.trade.domain.models.Asset;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AssetHolding {
    private String assetSymbol;
    private String assetSector;
    private long closePrice;
    private long quantity;

    public AssetHolding() {
    }

    public AssetHolding(Asset asset, long quantity) {
        this.assetSymbol = asset.getSymbol();
        this.assetSector = asset.getSector();
        this.quantity = quantity;
    }
}
