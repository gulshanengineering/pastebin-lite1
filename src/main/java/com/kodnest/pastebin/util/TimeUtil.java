package com.kodnest.pastebin.util;

import jakarta.servlet.http.HttpServletRequest;

public final class TimeUtil {

    private TimeUtil() {
        // Utility class
    }

    /**
     * Returns current time in milliseconds.
     * If TEST_MODE=1 and x-test-now-ms header is present,
     * the header value is used as the current time
     * ONLY for expiry logic.
     */
    public static long now(HttpServletRequest request) {

        if ("1".equals(System.getenv("TEST_MODE"))) {
            if (request != null) {
                String header = request.getHeader("x-test-now-ms");
                if (header != null && !header.isBlank()) {
                    try {
                        return Long.parseLong(header);
                    } catch (NumberFormatException ignored) {
                        // Fallback to system time
                    }
                }
            }
        }

        return System.currentTimeMillis();
    }
}
