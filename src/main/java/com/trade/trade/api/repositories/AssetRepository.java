package com.trade.trade.api.repositories;

import com.trade.trade.domain.models.Asset;
import com.trade.trade.api.repositories.Repository;

import java.util.Optional;

public interface AssetRepository extends Repository<Asset, Long> {
    Optional<Asset> findBySymbol(String symbol);

    boolean existsBySymbol(String symbol);
}
