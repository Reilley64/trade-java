package com.trade.trade.api.controllers;

import com.trade.trade.api.repositories.OrderRepository;
import com.trade.trade.api.repositories.TransactionRepository;
import com.trade.trade.api.repositories.UserRepository;
import com.trade.trade.clients.iexcloud.IEXCloudClient;
import com.trade.trade.domain.datatransferobjects.AssetHoldingResponse;
import com.trade.trade.domain.exceptions.ResourceNotFoundException;
import com.trade.trade.domain.models.Order;
import com.trade.trade.domain.models.Transaction;
import com.trade.trade.domain.models.User;
import com.trade.trade.domain.models.assets.Asset;
import com.trade.trade.api.repositories.assets.AssetRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/users/{userUuid}")
public class OrderController {
    private final OrderRepository repository;
    private final AssetRepository assetRepository;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;

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
    public List<Order> getAllOrders(@PathVariable UUID userUuid, @RequestParam(name = "asset", required = false) String assetSymbol) {
        userRepository.findByUuid(userUuid)
                .orElseThrow(() -> new ResourceNotFoundException(User.class, userUuid));

        if (assetSymbol != null) {
            assetRepository.findBySymbol(assetSymbol)
                    .orElseThrow(() -> new ResourceNotFoundException(Asset.class, assetSymbol));
            return repository.findByUserUuidAndAssetSymbol(userUuid, assetSymbol);
        }

        return repository.findByUserUuid(userUuid);
    }

    @GetMapping("/asset-holdings")
    @PreAuthorize("#userUuid == authentication.principal.user.uuid")
    public List<AssetHoldingResponse> getAssetHoldings(@PathVariable UUID userUuid, @RequestParam(name = "asset", required = false) String assetSymbol) {
        userRepository.findByUuid(userUuid)
                .orElseThrow(() -> new ResourceNotFoundException(User.class, userUuid));

        if (assetSymbol != null) {
            assetRepository.findBySymbol(assetSymbol)
                    .orElseThrow(() -> new ResourceNotFoundException(Asset.class, assetSymbol));
            return repository.findAssetHoldingsByUserUuidAndAssetSymbol(userUuid, assetSymbol);
        }

        return repository.findAssetHoldingsByUserUuid(userUuid);
    }

    @GetMapping("/orders/{uuid}")
    @PreAuthorize("#userUuid == authentication.principal.user.uuid")
    public Order getOrderByUuid(@PathVariable UUID userUuid, @PathVariable UUID uuid) {
        userRepository.findByUuid(userUuid)
                .orElseThrow(() -> new ResourceNotFoundException(User.class, userUuid));
        return repository.findByUserUuidAndUuid(uuid, userUuid)
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
        repository.save(order);

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
        transactionRepository.save(transaction);

        return order;
    }
}
