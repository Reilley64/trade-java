package com.trade.trade.api.repositories;

import com.trade.trade.domain.models.User;
import com.trade.trade.domain.models.UserSnapshot;

import java.util.List;

public interface UserSnapshotRepository extends Repository<UserSnapshot, Long> {
    List<UserSnapshot> findByUserOrderByCreatedAt(User user);
}
