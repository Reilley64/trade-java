package com.trade.trade.clients.financialmodelingprep.objects;

import lombok.Data;

import java.util.List;

@Data
public class StockRealTimePriceList {
    private List<StockRealTimePrice> companiesPriceList;
}
