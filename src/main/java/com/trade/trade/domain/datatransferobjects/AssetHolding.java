package com.trade.trade.domain.datatransferobjects;

import com.trade.trade.domain.models.Asset;
import com.trade.trade.domain.models.AssetValuation;
import lombok.Data;

@Data
public class AssetHolding {
    private Asset asset;
    private AssetValuation assetValuation;
    private long quantity;

    public AssetHolding() {
    }

    public AssetHolding(Asset asset, long quantity) {
        this.asset = asset;
        this.quantity = quantity;
    }
}
