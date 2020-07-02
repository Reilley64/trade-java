package com.trade.trade.api.controllers;

import com.trade.trade.api.repositories.UserRepository;
import com.trade.trade.api.repositories.UserSnapshotRepository;
import com.trade.trade.domain.exceptions.ResourceNotFoundException;
import com.trade.trade.domain.models.User;
import com.trade.trade.domain.models.UserSnapshot;
import org.springframework.web.bind.annotation.*;

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
    public List<UserSnapshot> getAllUserSnapshots(@PathVariable UUID userUuid) {
        User user = userRepository.findByUuid(userUuid)
                .orElseThrow(() -> new ResourceNotFoundException(User.class, userUuid));
        return repository.findByUserOrderByCreatedAt(user);
    }
}
