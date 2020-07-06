package com.trade.trade.api.services;

import com.trade.trade.api.repositories.AssetValuationRepository;
import com.trade.trade.api.repositories.OrderRepository;
import com.trade.trade.api.repositories.TransactionRepository;
import com.trade.trade.domain.datatransferobjects.AssetHolding;
import com.trade.trade.domain.models.AssetValuation;
import com.trade.trade.domain.models.User;
import com.trade.trade.domain.models.UserSnapshot;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserSnapshotService {
    private final AssetValuationRepository assetValuationRepository;
    private final AssetValuationService assetValuationService;
    private final OrderRepository orderRepository;
    private final TransactionRepository transactionRepository;

    public UserSnapshotService(AssetValuationRepository assetValuationRepository, AssetValuationService assetValuationService, OrderRepository orderRepository, TransactionRepository transactionRepository) {
        this.assetValuationRepository = assetValuationRepository;
        this.assetValuationService = assetValuationService;
        this.orderRepository = orderRepository;
        this.transactionRepository = transactionRepository;
    }

    public UserSnapshot createSnapshotForUser(User user) {
        UserSnapshot userSnapshot = new UserSnapshot();
        userSnapshot.setBalance(transactionRepository.findBalanceByUserUuid(user.getUuid()));
        userSnapshot.setAssetHoldings(orderRepository.findAssetHoldingsByUserOrderByAssetSymbol(user, PageRequest.of(0, 100000)).getContent());
        long valuation = userSnapshot.getBalance();
        for (AssetHolding assetHolding : userSnapshot.getAssetHoldings()) {
            assetValuationService.updateAssetValuations(assetHolding.getAsset());
            Optional<AssetValuation> optionalAssetValuation = assetValuationRepository.findFirstByAssetOrderByDateDesc(assetHolding.getAsset());
            if (optionalAssetValuation.isPresent()) {
                assetHolding.setAssetValuation(optionalAssetValuation.get());
                valuation += assetHolding.getAssetValuation().getClose() * assetHolding.getQuantity();
            }
        }
        userSnapshot.setValuation(valuation);
        userSnapshot.setUser(user);
        return userSnapshot;
    }
}
