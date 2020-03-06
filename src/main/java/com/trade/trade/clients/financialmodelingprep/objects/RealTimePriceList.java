package com.trade.trade.clients.financialmodelingprep.objects;

import lombok.Data;

import java.util.List;

@Data
public class RealTimePriceList {
    private List<RealTimePrice> companiesPriceList;
}
