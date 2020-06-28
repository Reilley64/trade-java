package com.trade.trade.api.repositories.assets;

import com.trade.trade.domain.models.assets.Stock;
import com.trade.trade.api.repositories.Repository;

import java.util.Optional;

public interface StockRepository extends Repository<Stock, Long> {
    Optional<Stock> findBySymbol(String symbol);

    boolean existsBySymbol(String symbol);
}
