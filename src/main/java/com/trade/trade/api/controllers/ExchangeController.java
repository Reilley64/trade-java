package com.trade.trade.api.controllers;

import com.trade.trade.api.repositories.ExchangeRepository;
import com.trade.trade.domain.models.Exchange;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ExchangeController {
    private final ExchangeRepository repository;

    public ExchangeController(ExchangeRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/exchanges")
    public List<Exchange> getAllExchanges() {
        return repository.findAllByActiveIsTrue();
    }
}
