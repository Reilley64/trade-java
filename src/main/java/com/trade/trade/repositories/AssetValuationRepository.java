package com.trade.trade.repositories;

import com.trade.trade.models.AssetValuation;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AssetValuationRepository extends Repository<AssetValuation, Long> {
    List<AssetValuation> findByAssetSymbol(String assetSymbol);

    List<AssetValuation> findByAssetSymbolAndDateBetween(String assetSymbol, Date startDate, Date endDate);

    Optional<AssetValuation> findByAssetSymbolAndUuid(String assetSymbol, UUID uuid);
}
