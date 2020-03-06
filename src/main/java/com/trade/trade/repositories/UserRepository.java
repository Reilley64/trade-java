package com.trade.trade.repositories;

import com.trade.trade.models.User;

import java.util.Optional;

public interface UserRepository extends Repository<User, Long> {
    Optional<User> findByUsername(String username);
}
