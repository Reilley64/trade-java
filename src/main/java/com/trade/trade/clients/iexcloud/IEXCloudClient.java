package com.trade.trade.clients.iexcloud;

import com.trade.trade.api.repositories.ReferenceDataRepository;
import com.trade.trade.clients.iexcloud.objects.*;
import com.trade.trade.domain.models.Asset;
import com.trade.trade.domain.models.Exchange;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
public class IEXCloudClient {
    private final ReferenceDataRepository referenceDataRepository;

    public IEXCloudClient(ReferenceDataRepository referenceDataRepository) {
        this.referenceDataRepository = referenceDataRepository;
    }

    private final String base = "https://cloud.iexapis.com/stable";

    private String getToken() {
        return "?token=" + referenceDataRepository.findByKey("iex-cloud-api-token");
    }

    public IEXAssetHistoricalPrice[] getAssetHistoricalPrices(Asset asset) {
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(base + "/stock/" + asset.getSymbol() + "/chart/1y" + getToken(), IEXAssetHistoricalPrice[].class);
    }

    public IEXAssetHistoricalPrice[] getAssetHistoricalPrices(Asset asset, LocalDate localDate) {
        String dateFormat = "yyyyMMdd";
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(dateFormat);
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(base + "/stock/" + asset.getSymbol() + "/chart/date/" + localDate.format(dateTimeFormatter) + getToken() + "&chartByDay=true", IEXAssetHistoricalPrice[].class);
    }

    public IEXAssetProfile getAssetProfile(Asset asset) {
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(base + "/stock/" + asset.getSymbol() + "/company" + getToken(), IEXAssetProfile.class);
    }

    public IEXAssetQuote getAssetQuote(Asset asset) {
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(base + "/stock/" + asset.getSymbol() + "/quote" + getToken(), IEXAssetQuote.class);
    }

    public IEXAssetSymbol[] getAssetSymbols(Exchange exchange) {
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(base + "/ref-data/exchange/" + exchange.getReference() + "/symbols" + getToken(), IEXAssetSymbol[].class);
    }

    public IEXExchange[] getExchanges() {
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(base + "/ref-data/exchanges" + getToken(), IEXExchange[].class);
    }
}
