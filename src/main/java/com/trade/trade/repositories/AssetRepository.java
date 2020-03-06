package com.trade.trade.repositories;

import com.trade.trade.models.Asset;

import java.util.Optional;

public interface AssetRepository extends Repository<Asset, Long> {
    Optional<Asset> findBySymbol(String symbol);

    boolean existsBySymbol(String symbol);
}
