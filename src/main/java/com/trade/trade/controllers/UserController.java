package com.trade.trade.controllers;

import com.trade.trade.exceptions.ResourceNotFoundException;
import com.trade.trade.models.Transaction;
import com.trade.trade.models.User;
import com.trade.trade.models.UserValuation;
import com.trade.trade.repositories.TransactionRepository;
import com.trade.trade.repositories.UserRepository;
import com.trade.trade.repositories.UserValuationRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
public class UserController {
    private final UserRepository repository;
    private final TransactionRepository transactionRepository;
    private final UserValuationRepository userValuationRepository;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserRepository repository, TransactionRepository transactionRepository,
                          UserValuationRepository userValuationRepository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.transactionRepository = transactionRepository;
        this.userValuationRepository = userValuationRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/users")
    public List<User> getAllUsers() {
        return repository.findAll();
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
        transaction.setValue(100000000);
        transactionRepository.save(transaction);

        UserValuation userValuation = new UserValuation();
        userValuation.setUser(user);
        userValuation.setValue(transactionRepository.findBalanceByUserUuid(user.getUuid()));
        userValuationRepository.save(userValuation);

        return user;
    }

    @PutMapping("/users/{uuid}")
    public User updateUser(@PathVariable UUID uuid, @RequestBody User newUser) {
        return repository.findByUuid(uuid)
                .map(user -> {
                    user.setUsername(newUser.getUsername());
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
    public void deleteUser(@PathVariable UUID uuid) {
        repository.deleteByUuid(uuid);
    }
}
