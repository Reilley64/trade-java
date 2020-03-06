package com.trade.trade.controllers;

import com.trade.trade.clients.financialmodelingprep.FinancialModelingPrepClient;
import com.trade.trade.exceptions.ResourceNotFoundException;
import com.trade.trade.models.*;
import com.trade.trade.repositories.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
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
    public List<Order> getAllOrders() {
        return repository.findAll();
    }

    @GetMapping("/orders/{uuid}")
    public Order getOrderByUuid(@PathVariable UUID uuid) {
        return repository.findByUuid(uuid)
                .orElseThrow(() -> new ResourceNotFoundException(Order.class, uuid));
    }

    @PostMapping("/orders")
    public Order createOrder(@RequestBody Order order) {
        order.setUser(userRepository.findByUuid(order.getUser().getUuid())
                .orElseThrow(() -> new ResourceNotFoundException(User.class, order.getUser().getUuid())));
        order.setAsset(assetRepository.findByUuid(order.getAsset().getUuid())
                .orElseThrow(() -> new ResourceNotFoundException(Asset.class, order.getAsset().getUuid())));
        order.setPrice((long) (financialModelingPrepClient.getRealTimePrice(order.getAsset()).getPrice() * 100));
        repository.save(order);
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
        transactionRepository.save(transaction);

        return order;
    }
}
