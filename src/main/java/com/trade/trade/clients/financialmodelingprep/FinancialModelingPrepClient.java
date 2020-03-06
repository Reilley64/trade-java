package com.trade.trade.clients.financialmodelingprep;

import com.trade.trade.clients.financialmodelingprep.objects.HistoricalDailyPrice;
import com.trade.trade.clients.financialmodelingprep.objects.RealTimePrice;
import com.trade.trade.clients.financialmodelingprep.objects.RealTimePriceList;
import com.trade.trade.models.Asset;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

public class FinancialModelingPrepClient {
    private String base = "https://financialmodelingprep.com/api/v3";

    public RealTimePrice getRealTimePrice(Asset asset) {
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(base + "/stock/real-time-price/" + asset.getSymbol(), RealTimePrice.class);
    }

    public RealTimePriceList getRealTimePrice(List<Asset> assets) {
        String[] strings = assets.stream().map(asset -> asset.getSymbol() + ",").toArray(size -> new String[assets.size()]);
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(base + "/stock/real-time-price/" + Arrays.toString(strings), RealTimePriceList.class);
    }

    public HistoricalDailyPrice getHistoricalDailyPrice(Asset asset) {
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(base + "/historical-price-full/" + asset.getSymbol(), HistoricalDailyPrice.class);
    }
}
