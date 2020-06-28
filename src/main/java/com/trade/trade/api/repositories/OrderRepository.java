package com.trade.trade.api.repositories;

import com.trade.trade.domain.datatransferobjects.AssetHoldingResponse;
import com.trade.trade.domain.models.Order;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository extends Repository<Order, Long> {
    List<Order> findByUserUuid(UUID userUuid);

    List<Order> findByUserUuidAndAssetSymbol(UUID userUuid, String assetSymbol);

    Optional<Order> findByUserUuidAndUuid(UUID uuid, UUID userUuid);

    @Query("SELECT new com.trade.trade.domain.datatransferobjects.AssetHoldingResponse(a, SUM(CASE WHEN o.direction = 0 THEN o.quantity ELSE -o.quantity END))" +
            " FROM Order o INNER JOIN Asset a ON o.asset = a" +
            " WHERE o.user.uuid = ?1" +
            " GROUP BY a")
    List<AssetHoldingResponse> findAssetHoldingsByUserUuid(UUID userUuid);

    @Query("SELECT new com.trade.trade.domain.datatransferobjects.AssetHoldingResponse(a, SUM(CASE WHEN o.direction = 0 THEN o.quantity ELSE -o.quantity END))" +
            " FROM Order o INNER JOIN Asset a ON o.asset = a" +
            " WHERE o.user.uuid = ?1 AND a.symbol = ?2" +
            " GROUP BY a")
    List<AssetHoldingResponse> findAssetHoldingsByUserUuidAndAssetSymbol(UUID userUuid, String assetSymbol);
}
