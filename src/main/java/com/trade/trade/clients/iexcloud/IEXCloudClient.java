package com.trade.trade.clients.iexcloud;

import com.trade.trade.clients.iexcloud.objects.IEXAssetHistoricalPrice;
import com.trade.trade.clients.iexcloud.objects.IEXAssetProfile;
import com.trade.trade.clients.iexcloud.objects.IEXAssetQuote;
import com.trade.trade.clients.iexcloud.objects.IEXAssetSymbol;
import com.trade.trade.domain.models.Asset;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class IEXCloudClient {
    private final String base = "https://cloud.iexapis.com/stable";
    private final String key = "?token=pk_2ae9a124aac941718c3ac6f47ef379aa";

    public IEXAssetProfile getAssetProfile(Asset asset) {
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(base + "/stock/" + asset.getSymbol() + "/company" + key, IEXAssetProfile.class);
    }

    public IEXAssetQuote getAssetQuote(Asset asset) {
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(base + "/stock/" + asset.getSymbol() + "/quote" + key, IEXAssetQuote.class);
    }

    public IEXAssetHistoricalPrice[] getAssetHistoricalPrices(Asset asset) {
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(base + "/stock/" + asset.getSymbol() + "/chart/1y" + key, IEXAssetHistoricalPrice[].class);
    }

    public IEXAssetHistoricalPrice[] getAssetHistoricalPrices(Asset asset, LocalDate localDate) {
        String dateFormat = "yyyyMMdd";
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(dateFormat);
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(base + "/stock/" + asset.getSymbol() + "/chart/date/" + localDate.format(dateTimeFormatter) + key + "&chartByDay=true", IEXAssetHistoricalPrice[].class);
    }

    public IEXAssetSymbol[] getAssetSymbols() {
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(base + "/ref-data/symbols" + key, IEXAssetSymbol[].class);
    }
}
