package com.trade.trade.clients.financialmodelingprep.objects;

import lombok.Data;

@Data
public class StockRealTimePrice {
    private String symbol;
    private double price;
}
