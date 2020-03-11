package com.trade.trade.repositories;

import com.trade.trade.datatransferobjects.AssetHoldingResponse;
import com.trade.trade.models.Order;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository extends Repository<Order, Long> {
    List<Order> findByUserUuid(UUID userUuid);

    List<Order> findByUserUuidAndAssetSymbol(UUID userUuid, String assetSymbol);

    Optional<Order> findByUserUuidAndUuid(UUID uuid, UUID userUuid);

    @Query("SELECT new com.trade.trade.datatransferobjects.AssetHoldingResponse(a.symbol, SUM(CASE WHEN o.direction = 0 THEN o.quantity ELSE -o.quantity END))" +
            " FROM Order o INNER JOIN Asset a ON o.asset = a" +
            " WHERE o.user.uuid = ?1" +
            " GROUP BY a.symbol")
    List<AssetHoldingResponse> findAssetHoldingsByUserUuid(UUID userUuid);

    @Query("SELECT new com.trade.trade.datatransferobjects.AssetHoldingResponse(a.symbol, SUM(CASE WHEN o.direction = 0 THEN o.quantity ELSE -o.quantity END))" +
            " FROM Order o INNER JOIN Asset a ON o.asset = a" +
            " WHERE o.user.uuid = ?1 AND a.symbol = ?2" +
            " GROUP BY a.symbol")
    List<AssetHoldingResponse> findAssetHoldingsByUserUuidAndAssetSymbol(UUID userUuid, String assetSymbol);
}
