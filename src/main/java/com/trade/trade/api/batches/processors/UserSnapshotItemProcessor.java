package com.trade.trade.api.batches.processors;

import com.trade.trade.api.repositories.AssetValuationRepository;
import com.trade.trade.api.repositories.OrderRepository;
import com.trade.trade.api.repositories.TransactionRepository;
import com.trade.trade.api.repositories.AssetRepository;
import com.trade.trade.api.services.AssetValuationService;
import com.trade.trade.domain.datatransferobjects.AssetHolding;
import com.trade.trade.domain.models.User;
import com.trade.trade.domain.models.UserSnapshot;
import com.trade.trade.domain.models.Asset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.data.domain.PageRequest;

public class UserSnapshotItemProcessor implements ItemProcessor<User, UserSnapshot> {
    private static final Logger log = LoggerFactory.getLogger(UserSnapshotItemProcessor.class);

    private final AssetRepository assetRepository;
    private final AssetValuationRepository assetValuationRepository;
    private final AssetValuationService assetValuationService;
    private final OrderRepository orderRepository;
    private final TransactionRepository transactionRepository;

    public UserSnapshotItemProcessor(AssetRepository assetRepository, AssetValuationRepository assetValuationRepository,
                                     AssetValuationService assetValuationService, OrderRepository orderRepository,
                                     TransactionRepository transactionRepository) {
        this.assetRepository = assetRepository;
        this.assetValuationRepository = assetValuationRepository;
        this.assetValuationService = assetValuationService;
        this.orderRepository = orderRepository;
        this.transactionRepository = transactionRepository;
    }

    @Override
    public UserSnapshot process(final User user) throws Exception {
        UserSnapshot userSnapshot = new UserSnapshot();
        userSnapshot.setAccountBalance(transactionRepository.findBalanceByUserUuid(user.getUuid()));
        userSnapshot.setAssetHoldings(orderRepository.findAssetHoldingsByUserOrderByAssetSymbol(user, PageRequest.of(0, 1000)).getContent());
        long accountValue = userSnapshot.getAccountBalance();
        for (AssetHolding assetHolding : userSnapshot.getAssetHoldings()) {
            Asset asset = assetRepository.findBySymbol(assetHolding.getAssetSymbol()).orElseThrow(Exception::new);
            assetValuationService.updateAssetValuations(asset);
            assetHolding.setClosePrice(assetValuationRepository.findFirstByAssetOrderByDateDesc(asset)
                    .orElseThrow(Exception::new).getClose());
            accountValue += assetHolding.getClosePrice() * assetHolding.getQuantity();
        }
        userSnapshot.setAccountValue(accountValue);
        userSnapshot.setUser(user);
        return userSnapshot;
    }
}
