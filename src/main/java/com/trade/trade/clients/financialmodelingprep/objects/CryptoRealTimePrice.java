package com.trade.trade.clients.financialmodelingprep.objects;

import lombok.Data;

import java.util.Date;

@Data
public class CryptoRealTimePrice {
    private String symbol;
    private String name;
    private double price;
    private double changesPercentage;
    private double change;
    private double dayLow;
    private double dayHigh;
    private double yearHigh;
    private double yearLow;
    private long marketCap;
    private double priceAvg50;
    private double priceAvg200;
    private long volume;
    private long avgVolume;
    private String crypto;
    private double open;
    private double previousClose;
    private Date timestamp;
}
