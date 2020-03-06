package com.trade.trade.controllers;

import com.trade.trade.exceptions.ResourceNotFoundException;
import com.trade.trade.models.User;
import com.trade.trade.models.UserValuation;
import com.trade.trade.repositories.UserRepository;
import com.trade.trade.repositories.UserValuationRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/users/{userUuid}")
public class UserValuationController {
    public UserValuationRepository repository;
    public UserRepository userRepository;

    public UserValuationController(UserValuationRepository repository, UserRepository userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
    }

    @GetMapping("/valuations")
    public List<UserValuation> getAllUsersBalances(@PathVariable UUID userUuid) {
        userRepository.findByUuid(userUuid)
                .orElseThrow(() -> new ResourceNotFoundException(User.class, userUuid));
        return repository.findByUserUuid(userUuid);
    }

    @GetMapping("/valuations/{uuid}")
    public UserValuation getUserByUuid(@PathVariable UUID userUuid, @PathVariable UUID uuid) {
        return repository.findByUserUuidAndUuid(uuid, userUuid)
                .orElseThrow(() -> new ResourceNotFoundException(UserValuation.class, uuid));
    }
}
