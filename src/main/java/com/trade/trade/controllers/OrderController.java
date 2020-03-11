package com.trade.trade.controllers;

import com.trade.trade.clients.financialmodelingprep.FinancialModelingPrepClient;
import com.trade.trade.datatransferobjects.AssetHoldingResponse;
import com.trade.trade.exceptions.ResourceNotFoundException;
import com.trade.trade.models.*;
import com.trade.trade.models.assets.Asset;
import com.trade.trade.models.assets.Crypto;
import com.trade.trade.models.assets.Stock;
import com.trade.trade.repositories.*;
import com.trade.trade.repositories.assets.AssetRepository;
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

    public FinancialModelingPrepClient financialModelingPrepClient = new FinancialModelingPrepClient();

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

        if (order.getAsset() instanceof Stock) {
            order.setPrice((long) (financialModelingPrepClient.getStockRealTimePrice((Stock) order.getAsset()).getPrice() * 100));
        } else if (order.getAsset() instanceof Crypto) {
            order.setPrice((long) (financialModelingPrepClient.getCryptoRealTimePrice((Crypto) order.getAsset()).getPrice() * 100));
        }

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
