package com.server.exceptions;

import com.server.ENUM.Origin;

public class MappingExceptions extends RuntimeException {
    private final Origin origin;

    public MappingExceptions(String message, Origin origin) {
        super(message);
        this.origin = origin;
    }

    public MappingExceptions(String message, Origin origin, Throwable cause) {
        super(message, cause);
        this.origin = origin;
    }

    public Origin getOrigin() {
        return origin;
    }
}