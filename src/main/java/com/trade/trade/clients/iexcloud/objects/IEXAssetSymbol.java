package com.trade.trade.clients.iexcloud.objects;

import lombok.Data;

@Data
public class IEXAssetSymbol {
    String symbol;
    String exchange;
    String name;
    String date;
    String type;
    String iexId;
    String region;
    String currency;
    Boolean isEnabled;
    String figi;
    String cik;
}
