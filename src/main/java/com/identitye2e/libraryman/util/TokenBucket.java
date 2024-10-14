package com.identitye2e.libraryman.util;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/*
    Simple API rate limiter implementation of Token Bucket algorithm.
    100 requests per 10 second windows.
    Number of available tokens are reset if the last check has expired.
    In order to support thread safety, class methods are defined as static and
    availableTokens defined as Thread safe AtomicInteger.
    TODO: Replace implementation with Bucket4j.
 */

public class TokenBucket {
    private static final int MAX_TOKENS = 100;
    private static final int MAX_WINDOW_DURATION = 10 * 1000; // 10 secs
    private static AtomicLong nextResetTime = new AtomicLong(0L);
    private static AtomicInteger availableTokens = new AtomicInteger(MAX_TOKENS);

    public static boolean getNextToken() {
        checkTimer();
    return availableTokens.decrementAndGet() > 0;
  }

    private static void checkTimer() {
        if (nextResetTime.longValue() <= System.currentTimeMillis()) {
            synchronized(nextResetTime) {
                // Double Check synchronized nextResetTime to avoid multiple reset per thread waiting in the queue.
                if (nextResetTime.longValue() <= System.currentTimeMillis()) {
                    availableTokens.set(MAX_TOKENS);
                    nextResetTime.set(System.currentTimeMillis() + MAX_WINDOW_DURATION);
                }
            }
        }
    }
}
