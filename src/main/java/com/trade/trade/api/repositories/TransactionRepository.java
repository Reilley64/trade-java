package com.trade.trade.api.repositories;

import com.trade.trade.domain.models.Transaction;
import com.trade.trade.domain.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TransactionRepository extends Repository<Transaction, Long> {
    Page<Transaction> findByUser(User user, Pageable pageable);

    @Query("SELECT SUM(CASE WHEN t.direction = 0 THEN t.value ELSE -t.value END)" +
                    " FROM Transaction t INNER JOIN User u ON t.user = u" +
                    " WHERE u.uuid = ?1")
    long findBalanceByUserUuid(UUID userUuid);
}
