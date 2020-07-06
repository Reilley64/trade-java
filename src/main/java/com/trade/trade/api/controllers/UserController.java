package com.trade.trade.api.controllers;

import com.trade.trade.api.repositories.UserSnapshotRepository;
import com.trade.trade.api.services.UserSnapshotService;
import com.trade.trade.domain.exceptions.ResourceNotFoundException;
import com.trade.trade.domain.models.Transaction;
import com.trade.trade.domain.models.User;
import com.trade.trade.api.repositories.TransactionRepository;
import com.trade.trade.api.repositories.UserRepository;
import com.trade.trade.api.security.UserPrincipal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
public class UserController {
    private final UserRepository repository;
    private final TransactionRepository transactionRepository;
    private final UserSnapshotRepository userSnapshotRepository;
    private final UserSnapshotService userSnapshotService;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserRepository repository, TransactionRepository transactionRepository,
                          UserSnapshotRepository userSnapshotRepository, UserSnapshotService userSnapshotService,
                          PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.transactionRepository = transactionRepository;
        this.userSnapshotRepository = userSnapshotRepository;
        this.userSnapshotService = userSnapshotService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/user")
    public User getCurrentUser() {
        return ((UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
    }

    @GetMapping("/user/balance")
    public long getCurrentUsersBalance() {
        return transactionRepository.findBalanceByUserUuid(((UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser().getUuid());
    }

    @GetMapping("/users")
    public Page<User> getAllUsers(@RequestParam(name = "page") Integer page, @RequestParam(name = "size") Integer size) {
        return repository.findAll(PageRequest.of(page, size, Sort.by("latestSnapshot.valuation").descending()));
    }

    @GetMapping("/users/{uuid}")
    public User getUserByUuid(@PathVariable UUID uuid) {
        return repository.findByUuid(uuid)
                .orElseThrow(() -> new ResourceNotFoundException(User.class, uuid));
    }

    @PostMapping("/users")
    public User createUser(@RequestBody User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user = repository.save(user);

        Transaction transaction = new Transaction();
        transaction.setUser(user);
        transaction.setDirection(Transaction.Direction.CREDIT);
        transaction.setDescription("Initial account credit");
        transaction.setValue(100000000);
        transactionRepository.save(transaction);

        userSnapshotRepository.save(userSnapshotService.createSnapshotForUser(user));

        return user;
    }

    @PutMapping("/users/{uuid}")
    @PreAuthorize("#uuid == authentication.principal.user.uuid")
    public User updateUser(@PathVariable UUID uuid, @RequestBody User newUser) {
        return repository.findByUuid(uuid)
                .map(user -> {
                    user.setPassword(passwordEncoder.encode(newUser.getPassword()));
                    user.setFirstName(newUser.getFirstName());
                    user.setLastName(newUser.getLastName());
                    return repository.save(user);
                })
                .orElseGet(() -> {
                    newUser.setUuid(uuid);
                    return repository.save(newUser);
                });
    }

    @DeleteMapping("/users/{uuid}")
    @PreAuthorize("#uuid == authentication.principal.user.uuid")
    public void deleteUser(@PathVariable UUID uuid) {
        repository.deleteByUuid(uuid);
    }
}
