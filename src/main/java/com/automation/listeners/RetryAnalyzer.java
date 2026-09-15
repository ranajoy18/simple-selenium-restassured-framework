package com.automation.listeners;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

/**
 * Retries a failed test once before letting it fail for real. Exists for
 * genuine environmental flakiness (a live third-party site being briefly
 * slow, a transient network hiccup) — it does not mask deterministic bugs,
 * since a real assertion failure reproduces identically on the retry too.
 */
public class RetryAnalyzer implements IRetryAnalyzer {

    private static final Logger logger = LogManager.getLogger(RetryAnalyzer.class);
    private static final int MAX_RETRY_COUNT = 1;

    private int retryCount = 0;

    @Override
    public boolean retry(ITestResult result) {
        if (retryCount < MAX_RETRY_COUNT) {
            retryCount++;
            logger.warn("Retrying {} (attempt {}/{})", result.getName(), retryCount + 1, MAX_RETRY_COUNT + 1);
            return true;
        }
        return false;
    }

}
