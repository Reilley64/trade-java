package com.trade.trade.clients.iexcloud.objects;

import lombok.Data;

import java.util.Date;

@Data
public class IEXAssetHistoricalPrice {
    private Date date;
    private Double close;
    private Double open;
    private Double high;
    private Double low;
    private Long volume;
}
