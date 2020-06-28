package com.trade.trade.domain.exceptions;

import java.util.UUID;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(Class klass, String string) {
        super("Could not find " + klass.getSimpleName() + " " + string);
    }

    public ResourceNotFoundException(Class klass, UUID uuid) {
        super("Could not find " + klass.getSimpleName() + " " + uuid);
    }
}
