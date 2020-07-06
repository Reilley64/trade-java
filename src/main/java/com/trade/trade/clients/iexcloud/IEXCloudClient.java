package com.trade.trade.clients.iexcloud;

import com.trade.trade.clients.iexcloud.objects.*;
import com.trade.trade.domain.models.Asset;
import com.trade.trade.domain.models.Exchange;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class IEXCloudClient {
    private final String base = "https://cloud.iexapis.com/stable";
    private final String token = "?token=pk_d5306380300849baa2ba01cef379338c";

    public IEXAssetProfile getAssetProfile(Asset asset) {
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(base + "/stock/" + asset.getSymbol() + "/company" + token, IEXAssetProfile.class);
    }

    public IEXAssetQuote getAssetQuote(Asset asset) {
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(base + "/stock/" + asset.getSymbol() + "/quote" + token, IEXAssetQuote.class);
    }

    public IEXAssetHistoricalPrice[] getAssetHistoricalPrices(Asset asset) {
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(base + "/stock/" + asset.getSymbol() + "/chart/1y" + token, IEXAssetHistoricalPrice[].class);
    }

    public IEXAssetHistoricalPrice[] getAssetHistoricalPrices(Asset asset, LocalDate localDate) {
        String dateFormat = "yyyyMMdd";
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(dateFormat);
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(base + "/stock/" + asset.getSymbol() + "/chart/date/" + localDate.format(dateTimeFormatter) + token + "&chartByDay=true", IEXAssetHistoricalPrice[].class);
    }

    public IEXExchange[] getExchanges() {
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(base + "/ref-data/exchanges" + token, IEXExchange[].class);
    }

    public IEXAssetSymbol[] getAssetSymbols(Exchange exchange) {
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(base + "/ref-data/exchange/" + exchange.getReference() + "/symbols" + token, IEXAssetSymbol[].class);
    }
}
