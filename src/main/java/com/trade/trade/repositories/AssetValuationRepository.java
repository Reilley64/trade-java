package com.trade.trade.repositories;

import com.trade.trade.models.Asset;
import com.trade.trade.models.AssetValuation;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AssetValuationRepository extends Repository<AssetValuation, Long> {
    List<AssetValuation> findByAssetUuid(UUID assetUuid);

    Optional<AssetValuation> findByAssetUuidAndUuid(UUID uuid, UUID assetUuid);
}
