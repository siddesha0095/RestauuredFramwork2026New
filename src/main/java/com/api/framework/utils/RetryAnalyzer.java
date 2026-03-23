package com.api.framework.utils;

import com.api.framework.config.ConfigManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

/**
 * RetryAnalyzer - Implements TestNG IRetryAnalyzer for automatic retry of failed tests.
 * Retry count is driven by framework configuration (default: 3).
 */
public class RetryAnalyzer implements IRetryAnalyzer {

    private static final Logger log = LogManager.getLogger(RetryAnalyzer.class);
    private int retryCount = 0;
    private final int maxRetryCount = ConfigManager.getConfig().retryCount();

    @Override
    public boolean retry(ITestResult result) {
        if (retryCount < maxRetryCount) {
            retryCount++;
            log.warn("Retrying test '{}' — attempt {}/{} after failure: {}",
                    result.getName(), retryCount, maxRetryCount,
                    result.getThrowable() != null ? result.getThrowable().getMessage() : "unknown");
            return true;
        }
        return false;
    }
}
