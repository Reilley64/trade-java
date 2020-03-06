package com.trade.trade.clients.financialmodelingprep.objects;

import lombok.Data;

import java.util.Date;

@Data
public class History {
    private Date date;
    private double open;
    private double high;
    private double low;
    private double close;
    private double adjClose;
    private long volume;
    private long unadjustedVolume;
    private double change;
    private double changePercentage;
    private double vwap;
    private String label;
    private double changeOverTime;
}
