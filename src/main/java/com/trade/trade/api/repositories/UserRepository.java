package com.trade.trade.api.repositories;

import com.trade.trade.domain.models.User;

import java.util.Optional;

public interface UserRepository extends Repository<User, Long> {
    Optional<User> findByUsername(String username);
}
