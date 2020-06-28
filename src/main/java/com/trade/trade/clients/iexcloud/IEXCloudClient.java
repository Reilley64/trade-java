package com.trade.trade.clients.iexcloud;

import com.trade.trade.clients.iexcloud.objects.IEXStockHistoricalPrice;
import com.trade.trade.clients.iexcloud.objects.IEXStockProfile;
import com.trade.trade.clients.iexcloud.objects.IEXStockQuote;
import com.trade.trade.domain.models.assets.Asset;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class IEXCloudClient {
    private final String base = "https://cloud.iexapis.com/stable";
    private final String key = "?token=pk_2ae9a124aac941718c3ac6f47ef379aa";

    public IEXStockProfile getStockProfile(Asset asset) {
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(base + "/stock/" + asset.getSymbol() + "/company" + key, IEXStockProfile.class);
    }

    public IEXStockQuote getStockQuote(Asset asset) {
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(base + "/stock/" + asset.getSymbol() + "/quote" + key, IEXStockQuote.class);
    }

    public IEXStockHistoricalPrice[] getStockHistoricalPrices(Asset asset) {
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(base + "/stock/" + asset.getSymbol() + "/chart/1y" + key, IEXStockHistoricalPrice[].class);
    }

    public IEXStockHistoricalPrice[] getStockHistoricalPrices(Asset asset, LocalDate localDate) {
        String dateFormat = "yyyyMMdd";
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(dateFormat);
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(base + "/stock/" + asset.getSymbol() + "/chart/date/" + localDate.format(dateTimeFormatter) + key + "&chartByDay=true", IEXStockHistoricalPrice[].class);
    }
}
