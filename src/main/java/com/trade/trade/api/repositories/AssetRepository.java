package com.trade.trade.api.repositories;

import com.trade.trade.domain.models.Asset;
import com.trade.trade.api.repositories.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AssetRepository extends Repository<Asset, Long> {
    Optional<Asset> findBySymbol(String symbol);

    boolean existsBySymbol(String symbol);

    @Query(
            value = "SELECT a.* "
                    + " FROM assets a "
                    + " WHERE similarity((symbol || ' ' || name), ?1) > 0 "
                    + " ORDER BY similarity((symbol || ' ' || name), ?1) DESC",
            countQuery = "SELECT count(*)"
                    + " FROM assets a "
                    + " WHERE similarity((symbol || ' ' || name), ?1) > 0 ",
            nativeQuery = true
    )
    Page<Asset> findByFilter(String filter, Pageable pageable);
}
