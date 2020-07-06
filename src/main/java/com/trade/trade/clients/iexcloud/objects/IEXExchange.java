package com.trade.trade.clients.iexcloud.objects;

import lombok.Data;

@Data
public class IEXExchange {
    String exchange;
    String region;
    String description;
    String mic;
    String exchangeSuffix;
}
