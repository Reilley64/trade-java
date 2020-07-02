package com.trade.trade.api.repositories;

import com.trade.trade.domain.models.AssetValuation;
import com.trade.trade.domain.models.Asset;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface AssetValuationRepository extends Repository<AssetValuation, Long> {
    List<AssetValuation> findByAssetAndDateBetweenOrderByDate(Asset asset, Date startDate, Date endDate);

    Optional<AssetValuation> findFirstByAssetOrderByDateDesc(Asset asset);
}
