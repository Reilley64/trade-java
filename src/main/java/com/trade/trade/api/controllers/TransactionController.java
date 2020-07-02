package com.trade.trade.api.controllers;

import com.trade.trade.api.repositories.TransactionRepository;
import com.trade.trade.api.repositories.UserRepository;
import com.trade.trade.domain.exceptions.ResourceNotFoundException;
import com.trade.trade.domain.models.Transaction;
import com.trade.trade.domain.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/users/{userUuid}")
public class TransactionController {
    private final TransactionRepository repository;
    private final UserRepository userRepository;

    public TransactionController(TransactionRepository repository, UserRepository userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
    }

    @GetMapping("/transactions")
    public Page<Transaction> getAllTransactions(@PathVariable UUID userUuid) {
        User user = userRepository.findByUuid(userUuid)
                .orElseThrow(() -> new ResourceNotFoundException(User.class, userUuid));
        return repository.findByUser(user, PageRequest.of(0, 10, Sort.by("createdAt").descending()));
    }
}
