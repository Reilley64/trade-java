package com.trade.trade.domain.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(Class klass, String string) {
        super("Could not find " + klass.getSimpleName() + " " + string);
    }

    public ResourceNotFoundException(Class klass, UUID uuid) {
        super("Could not find " + klass.getSimpleName() + " " + uuid);
    }
}
