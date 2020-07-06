package com.trade.trade.api.repositories;

import com.trade.trade.domain.models.Exchange;

import java.util.List;

public interface ExchangeRepository extends Repository<Exchange, Long> {
    List<Exchange> findAllByActiveIsTrue();
}
