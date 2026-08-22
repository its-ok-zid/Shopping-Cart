package com.cts.common.error;

import java.time.Instant;

public record ErrorResponse(boolean success, String code, String message, String traceId, Instant timestamp) {
}
