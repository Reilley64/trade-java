package com.trade.trade.api.repositories;

import com.trade.trade.domain.models.User;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends Repository<User, Long> {
    Optional<User> findByUsername(String username);
}
