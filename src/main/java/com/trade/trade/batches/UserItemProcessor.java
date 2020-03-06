package com.trade.trade.batches;

import com.trade.trade.models.User;
import com.trade.trade.models.UserValuation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

public class UserItemProcessor implements ItemProcessor<User, User> {
    private static final Logger log = LoggerFactory.getLogger(UserItemProcessor.class);

    @Override
    public User process(final User user) throws Exception {
        UserValuation userValuation = new UserValuation();
        return user;
    }
}
