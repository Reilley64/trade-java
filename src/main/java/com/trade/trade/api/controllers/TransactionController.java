package com.trade.trade.api.controllers;

import com.trade.trade.api.repositories.TransactionRepository;
import com.trade.trade.api.repositories.UserRepository;
import com.trade.trade.domain.exceptions.ResourceNotFoundException;
import com.trade.trade.domain.models.Transaction;
import com.trade.trade.domain.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

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
    public Page<Transaction> getAllTransactions(@RequestParam(name = "page") Integer page, @RequestParam(name = "size") Integer size,
                                                @PathVariable UUID userUuid) {
        User user = userRepository.findByUuid(userUuid)
                .orElseThrow(() -> new ResourceNotFoundException(User.class, userUuid));
        return repository.findByUser(user, PageRequest.of(page, size, Sort.by("createdAt").descending()));
    }
}
