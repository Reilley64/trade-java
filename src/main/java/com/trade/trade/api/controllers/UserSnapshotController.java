package com.trade.trade.api.controllers;

import com.trade.trade.api.repositories.UserRepository;
import com.trade.trade.api.repositories.UserSnapshotRepository;
import com.trade.trade.domain.exceptions.ResourceNotFoundException;
import com.trade.trade.domain.models.User;
import com.trade.trade.domain.models.UserSnapshot;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/users/{userUuid}")
public class UserSnapshotController {
    private final UserSnapshotRepository repository;
    private final UserRepository userRepository;

    public UserSnapshotController(UserSnapshotRepository repository, UserRepository userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
    }

    @GetMapping("/snapshots")
    public List<UserSnapshot> getAllUserSnapshots(@PathVariable UUID userUuid, @RequestParam(name = "range") String range) {
        User user = userRepository.findByUuid(userUuid)
                .orElseThrow(() -> new ResourceNotFoundException(User.class, userUuid));
        switch (range) {
            case "1m":
                return repository.findByUserAndCreatedAtBetweenOrderByCreatedAt(
                        user,
                        java.util.Date.from(LocalDate.now().minusMonths(1).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()),
                        new Date(System.currentTimeMillis())
                );

            case "3m":
                return repository.findByUserAndCreatedAtBetweenOrderByCreatedAt(
                        user,
                        java.util.Date.from(LocalDate.now().minusMonths(3).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()),
                        new Date(System.currentTimeMillis())
                );

            case "6m":
                return repository.findByUserAndCreatedAtBetweenOrderByCreatedAt(
                        user,
                        java.util.Date.from(LocalDate.now().minusMonths(6).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()),
                        new Date(System.currentTimeMillis())
                );

            case "1y":
                return repository.findByUserAndCreatedAtBetweenOrderByCreatedAt(
                        user,
                        java.util.Date.from(LocalDate.now().minusYears(1).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()),
                        new Date(System.currentTimeMillis())
                );

            default:
                return repository.findByUserAndCreatedAtBetweenOrderByCreatedAt(
                        user,
                        java.util.Date.from(LocalDate.now().minusDays(7).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()),
                        new Date(System.currentTimeMillis())
                );
        }
    }
}
