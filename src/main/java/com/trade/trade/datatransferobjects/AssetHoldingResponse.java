package com.trade.trade.datatransferobjects;

import com.trade.trade.models.assets.Asset;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AssetHoldingResponse {
    private Asset asset;
    private long quantity;
}
