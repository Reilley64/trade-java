package com.trade.trade.clients.financialmodelingprep;

import com.trade.trade.clients.financialmodelingprep.objects.*;
import com.trade.trade.models.assets.Asset;
import com.trade.trade.models.assets.Crypto;
import com.trade.trade.models.assets.Stock;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

public class FinancialModelingPrepClient {
    private String base = "https://financialmodelingprep.com/api/v3";

    public StockProfile getStockProfile(Asset asset) {
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(base + "/company/profile/" + asset.getSymbol(), StockProfile.class);
    }

    public StockRealTimePrice getStockRealTimePrice(Stock stock) {
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(base + "/stock/real-time-price/" + stock.getSymbol(), StockRealTimePrice.class);
    }

    public StockRealTimePriceList getStockRealTimePrice(List<Stock> stocks) {
        String[] strings = stocks.stream().map(asset -> asset.getSymbol() + ",").toArray(size -> new String[stocks.size()]);
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(base + "/stock/real-time-price/" + Arrays.toString(strings), StockRealTimePriceList.class);
    }

    public HistoricalDailyPrice getStockHistoricalDailyPrice(Asset asset) {
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(base + "/historical-price-full/" + asset.getSymbol(), HistoricalDailyPrice.class);
    }

    public CryptoProfile getCryptoProfile(Asset asset) {
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(base + "/cryptocurrency/" + asset.getSymbol(), CryptoProfile.class);
    }

    public CryptoRealTimePrice getCryptoRealTimePrice(Crypto crypto) {
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(base + "/quote/" + crypto.getSymbol() + "USD", CryptoRealTimePrice.class);
    }

    public CryptoRealTimePrice[] getCryptoRealTimePrice(List<Crypto> cryptos) {
        String[] strings = cryptos.stream().map(crypto -> crypto.getSymbol() + "USD,").toArray(size -> new String[cryptos.size()]);
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(base + "/stock/real-time-price/" + Arrays.toString(strings), CryptoRealTimePrice[].class);
    }

    public HistoricalDailyPrice getCryptoHistoricalDailyPrice(Asset asset) {
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(base + "/historical-price-full/crypto/" + asset.getSymbol() + "USD", HistoricalDailyPrice.class);
    }
}
