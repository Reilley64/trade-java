package com.trade.trade.api.repositories;

import com.trade.trade.domain.datatransferobjects.AssetHolding;
import com.trade.trade.domain.models.Order;
import com.trade.trade.domain.models.User;
import com.trade.trade.domain.models.Asset;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface OrderRepository extends Repository<Order, Long> {
    Page<Order> findByUser(User user, Pageable pageable);

    Page<Order> findByUserAndAsset(User user, Asset asset, Pageable pageable);

    Optional<Order> findByUuidAndUser(UUID uuid, User user);

    @Query("SELECT new com.trade.trade.domain.datatransferobjects.AssetHolding(a, SUM(CASE WHEN o.direction = 0 THEN o.quantity ELSE -o.quantity END))" +
            " FROM Order o JOIN Asset a ON o.asset = a" +
            " WHERE o.user = ?1" +
            " GROUP BY a" +
            " ORDER BY a.symbol")
    Page<AssetHolding> findAssetHoldingsByUserOrderByAssetSymbol(User user, Pageable pageable);

    @Query("SELECT new com.trade.trade.domain.datatransferobjects.AssetHolding(a, SUM(CASE WHEN o.direction = 0 THEN o.quantity ELSE -o.quantity END))" +
            " FROM Order o JOIN Asset a ON o.asset = a" +
            " WHERE o.user = ?1 AND a = ?2" +
            " GROUP BY a")
    Page<AssetHolding> findAssetHoldingsByUserAndAsset(User user, Asset asset, Pageable pageable);
}
