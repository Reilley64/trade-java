package com.trade.trade.repositories.assets;

import com.trade.trade.models.assets.Stock;
import com.trade.trade.repositories.Repository;

import java.util.Optional;

public interface StockRepository extends Repository<Stock, Long> {
    Optional<Stock> findBySymbol(String symbol);

    boolean existsBySymbol(String symbol);
}
