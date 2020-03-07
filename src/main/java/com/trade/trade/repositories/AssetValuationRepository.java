package com.trade.trade.repositories;

import com.trade.trade.models.AssetValuation;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AssetValuationRepository extends Repository<AssetValuation, Long> {
    List<AssetValuation> findByAssetSymbol(String assetSymbol);

    Optional<AssetValuation> findByAssetSymbolAndUuid(String assetSymbol, UUID uuid);
}
