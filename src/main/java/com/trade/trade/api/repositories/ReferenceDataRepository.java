package com.trade.trade.api.repositories;

import com.trade.trade.domain.models.ReferenceData;

import java.util.Optional;

public interface ReferenceDataRepository extends Repository<ReferenceData, Long> {
    Optional<ReferenceData> findByKey(String key);
}
