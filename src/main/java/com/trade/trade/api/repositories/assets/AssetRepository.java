package com.trade.trade.api.repositories.assets;

import com.trade.trade.domain.models.assets.Asset;
import com.trade.trade.api.repositories.Repository;

import java.util.Optional;

public interface AssetRepository extends Repository<Asset, Long> {
    Optional<Asset> findBySymbol(String symbol);

    boolean existsBySymbol(String symbol);
}
