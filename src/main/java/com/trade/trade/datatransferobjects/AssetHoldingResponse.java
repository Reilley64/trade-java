package com.trade.trade.datatransferobjects;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AssetHoldingResponse {
    private String symbol;
    private long sum;
}
