package com.trade.trade.api.repositories;

import com.trade.trade.domain.models.AssetValuation;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AssetValuationRepository extends Repository<AssetValuation, Long> {
    List<AssetValuation> findByAssetSymbolOrderByDateDesc(String assetSymbol);

    List<AssetValuation> findByAssetSymbolAndDateBetweenOrderByDateDesc(String assetSymbol, Date startDate, Date endDate);

    Optional<AssetValuation> findByAssetSymbolAndUuid(String assetSymbol, UUID uuid);
}
