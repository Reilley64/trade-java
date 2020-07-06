package com.trade.trade.api.batches.processors;

import com.trade.trade.api.services.UserSnapshotService;
import com.trade.trade.domain.models.User;
import com.trade.trade.domain.models.UserSnapshot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

public class UserSnapshotItemProcessor implements ItemProcessor<User, UserSnapshot> {
    private static final Logger log = LoggerFactory.getLogger(UserSnapshotItemProcessor.class);

    private final UserSnapshotService service;

    public UserSnapshotItemProcessor(UserSnapshotService service) {
        this.service = service;
    }

    @Override
    public UserSnapshot process(final User user) throws Exception {
        return service.createSnapshotForUser(user);
    }
}
