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

import java.util.List;
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
    public Page<Order> getAllOrders(@PathVariable UUID userUuid, @RequestParam(name = "asset", required = false) String assetSymbol) {
        User user = userRepository.findByUuid(userUuid)
                .orElseThrow(() -> new ResourceNotFoundException(User.class, userUuid));

        if (assetSymbol != null) {
            Asset asset = assetRepository.findBySymbol(assetSymbol)
                    .orElseThrow(() -> new ResourceNotFoundException(Asset.class, assetSymbol));
            return repository.findByUserAndAsset(user, asset, PageRequest.of(0, 15, Sort.by("createdAt").descending()));
        }

        return repository.findByUser(user, PageRequest.of(0, 15, Sort.by("createdAt").descending()));
    }

    @GetMapping("/asset-holdings")
    @PreAuthorize("#userUuid == authentication.principal.user.uuid")
    public Page<AssetHolding> getAssetHoldings(@PathVariable UUID userUuid, @RequestParam(name = "asset", required = false) String assetSymbol,
                                               @RequestParam(name = "page") Integer page, @RequestParam(name = "limit") Integer limit) {
        User user = userRepository.findByUuid(userUuid)
                .orElseThrow(() -> new ResourceNotFoundException(User.class, userUuid));

        if (assetSymbol != null) {
            Asset asset = assetRepository.findBySymbol(assetSymbol)
                    .orElseThrow(() -> new ResourceNotFoundException(Asset.class, assetSymbol));
            return repository.findAssetHoldingsByUserAndAsset(user, asset, PageRequest.of(page, limit));
        }

        return repository.findAssetHoldingsByUserOrderByAssetSymbol(user, PageRequest.of(page, limit));
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
                .orElse(assetRepository.findBySymbol(order.getAsset().getSymbol())
                        .orElseThrow(() -> new ResourceNotFoundException(Asset.class, order.getAsset().getUuid()))));
        order.setPrice((long) (iexCloudClient.getStockQuote(order.getAsset()).getLatestPrice() * 100));
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
