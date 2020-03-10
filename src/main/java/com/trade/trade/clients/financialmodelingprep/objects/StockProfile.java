package com.trade.trade.clients.financialmodelingprep.objects;

import lombok.Data;

@Data
public class StockProfile {
    private String symbol;
    private StockProfileDetails profile;
}
