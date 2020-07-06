package com.trade.trade.api.controllers;

import com.trade.trade.api.repositories.OrderRepository;
import com.trade.trade.api.repositories.TransactionRepository;
import com.trade.trade.api.repositories.UserRepository;
import com.trade.trade.clients.iexcloud.IEXCloudClient;
import com.trade.trade.domain.datatransferobjects.AssetHolding;
import com.trade.trade.domain.exceptions.ResourceNotFoundException;
import com.trade.trade.domain.models.Order;
import com.trade.trade.domain.models.Transaction;
import com.trade.trade.domain.models.User;
import com.trade.trade.domain.models.Asset;
import com.trade.trade.api.repositories.AssetRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/users/{userUuid}")
public class OrderController {
    private final OrderRepository repository;
    private final AssetRepository assetRepository;
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    public OrderController(OrderRepository repository, AssetRepository assetRepository,
                           TransactionRepository transactionRepository, UserRepository userRepository) {
        this.repository = repository;
        this.assetRepository = assetRepository;
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
    }

    IEXCloudClient iexCloudClient = new IEXCloudClient();

    @GetMapping("/orders")
    @PreAuthorize("#userUuid == authentication.principal.user.uuid")
    public Page<Order> getAllOrders(@RequestParam(name = "page") Integer page, @RequestParam(name = "size") Integer size,
                                    @PathVariable UUID userUuid, @RequestParam(name = "asset", required = false) String assetSymbol) {
        User user = userRepository.findByUuid(userUuid)
                .orElseThrow(() -> new ResourceNotFoundException(User.class, userUuid));
        return repository.findByUser(user, PageRequest.of(page, size, Sort.by("createdAt").descending()));
    }

    @GetMapping("/orders/{assetUuid}")
    @PreAuthorize("#userUuid == authentication.principal.user.uuid")
    public Page<Order> getOrdersByAssetUuid(@PathVariable UUID userUuid, @PathVariable UUID assetUuid,
                                                   @RequestParam(name = "page") Integer page, @RequestParam(name = "limit") Integer limit) {
        User user = userRepository.findByUuid(userUuid)
                .orElseThrow(() -> new ResourceNotFoundException(User.class, userUuid));
        Asset asset = assetRepository.findByUuid(assetUuid)
                .orElseThrow(() -> new ResourceNotFoundException(Asset.class, assetUuid));
        return repository.findByUserAndAsset(user, asset, PageRequest.of(page, limit));
    }

    @GetMapping("/asset-holdings")
    @PreAuthorize("#userUuid == authentication.principal.user.uuid")
    public Page<AssetHolding> getAllAssetHoldings(@PathVariable UUID userUuid,
                                               @RequestParam(name = "page") Integer page, @RequestParam(name = "limit") Integer limit) {
        User user = userRepository.findByUuid(userUuid)
                .orElseThrow(() -> new ResourceNotFoundException(User.class, userUuid));
        return repository.findAssetHoldingsByUserOrderByAssetSymbol(user, PageRequest.of(page, limit));
    }

    @GetMapping("/asset-holdings/{assetUuid}")
    @PreAuthorize("#userUuid == authentication.principal.user.uuid")
    public Page<AssetHolding> getAssetHoldingsByAssetUuid(@PathVariable UUID userUuid, @PathVariable UUID assetUuid,
                                                          @RequestParam(name = "page") Integer page, @RequestParam(name = "limit") Integer limit) {
        User user = userRepository.findByUuid(userUuid)
                .orElseThrow(() -> new ResourceNotFoundException(User.class, userUuid));
        Asset asset = assetRepository.findByUuid(assetUuid)
                .orElseThrow(() -> new ResourceNotFoundException(Asset.class, assetUuid));
        return repository.findAssetHoldingsByUserAndAsset(user, asset, PageRequest.of(page, limit));
    }

    @GetMapping("/orders/{uuid}")
    @PreAuthorize("#userUuid == authentication.principal.user.uuid")
    public Order getOrderByUuid(@PathVariable UUID userUuid, @PathVariable UUID uuid) {
        User user = userRepository.findByUuid(userUuid)
                .orElseThrow(() -> new ResourceNotFoundException(User.class, userUuid));
        return repository.findByUuidAndUser(uuid, user)
                .orElseThrow(() -> new ResourceNotFoundException(Order.class, uuid));
    }

    @PostMapping("/orders")
    @PreAuthorize("#userUuid == authentication.principal.user.uuid")
    public Order createOrder(@PathVariable UUID userUuid, @RequestBody Order order) {
        order.setUser(userRepository.findByUuid(userUuid)
                .orElseThrow(() -> new ResourceNotFoundException(User.class, order.getUser().getUuid())));
        order.setAsset(assetRepository.findByUuid(order.getAsset().getUuid())
                .orElseThrow(() -> new ResourceNotFoundException(Asset.class, order.getAsset().getUuid())));
        order.setPrice((long) (iexCloudClient.getAssetQuote(order.getAsset()).getLatestPrice() * 100));
        order.setBrokerage(2000);

        if (transactionRepository.findBalanceByUserUuid(order.getUser().getUuid()) < order.getTotal()) throw new RuntimeException();
        Transaction transaction = new Transaction();
        if (order.getDirection() == Order.Direction.BUY) {
            transaction.setUser(order.getUser());
            transaction.setValue(order.getTotal());
            transaction.setDescription("Buy " + order.getAsset().getSymbol());
            transaction.setDirection(Transaction.Direction.DEBIT);
        } else {
            transaction.setUser(order.getUser());
            transaction.setValue(order.getTotal());
            transaction.setDescription("Buy " + order.getAsset().getSymbol());
            transaction.setDirection(Transaction.Direction.CREDIT);
        }
        order.setTransaction(transaction);

        return repository.save(order);
    }
}
