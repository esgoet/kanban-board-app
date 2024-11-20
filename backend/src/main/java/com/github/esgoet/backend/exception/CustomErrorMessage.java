package com.github.esgoet.backend.exception;

import java.time.LocalDateTime;

public record CustomErrorMessage(
        String message,
        LocalDateTime timestamp,
        int statusCode
) {
}
