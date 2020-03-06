package com.trade.trade.repositories;

import com.trade.trade.models.UserValuation;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserValuationRepository extends Repository<UserValuation, Long> {
    List<UserValuation> findByUserUuid(UUID userUuid);
    Optional<UserValuation> findByUserUuidAndUuid(UUID uuid, UUID userUuid);
}
