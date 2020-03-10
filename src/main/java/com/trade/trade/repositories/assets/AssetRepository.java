package com.trade.trade.repositories.assets;

import com.trade.trade.models.assets.Asset;
import com.trade.trade.repositories.Repository;

import java.util.Optional;

public interface AssetRepository extends Repository<Asset, Long> {
    Optional<Asset> findBySymbol(String symbol);

    boolean existsBySymbol(String symbol);
}
