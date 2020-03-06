package com.trade.trade.clients.financialmodelingprep.objects;

import lombok.Data;

import java.util.List;

@Data
public class HistoricalDailyPrice {
    private String symbol;
    private List<History> historical;
}
