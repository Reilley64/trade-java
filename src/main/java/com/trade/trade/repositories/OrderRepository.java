package com.trade.trade.repositories;

import com.trade.trade.models.Order;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository extends Repository<Order, Long> {
    List<Order> findByUserUuid(UUID userUuid);

    Optional<Order> findByUserUuidAndUuid(UUID uuid, UUID userUuid);
}
