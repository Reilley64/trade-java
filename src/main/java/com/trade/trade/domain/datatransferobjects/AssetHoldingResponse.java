package com.trade.trade.domain.datatransferobjects;

import com.trade.trade.domain.models.assets.Asset;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AssetHoldingResponse {
    private Asset asset;
    private long quantity;
}
