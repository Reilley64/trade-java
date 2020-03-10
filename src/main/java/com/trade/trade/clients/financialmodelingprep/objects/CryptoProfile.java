package com.trade.trade.clients.financialmodelingprep.objects;

import lombok.Data;

@Data
public class CryptoProfile {
    private String ticker;
    private String name;
    private double price;
    private double changes;
    private double marketCapitalization;
}
