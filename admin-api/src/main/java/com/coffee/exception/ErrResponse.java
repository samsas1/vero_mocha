package com.coffee.exception;

import java.time.LocalDateTime;

public record ErrResponse(
        int status,
        String message,
        String path,
        LocalDateTime timestamp
) {
}
