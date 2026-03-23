package com.api.framework.listeners;

import com.api.framework.utils.ExtentReportManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.*;

/**
 * TestNGListener - Implements multiple TestNG listener interfaces.
 * Hooks into suite/test/method lifecycle for logging and reporting.
 */
public class TestNGListener implements ITestListener, ISuiteListener {

    private static final Logger log = LogManager.getLogger(TestNGListener.class);

    // ==================== ISuiteListener ====================

    @Override
    public void onStart(ISuite suite) {
        log.info("╔══════════════════════════════════════════╗");
        log.info("  Suite Started : {}", suite.getName());
        log.info("╚══════════════════════════════════════════╝");
    }

    @Override
    public void onFinish(ISuite suite) {
        log.info("╔══════════════════════════════════════════╗");
        log.info("  Suite Finished : {}", suite.getName());
        log.info("╚══════════════════════════════════════════╝");
        ExtentReportManager.flush();
    }

    // ==================== ITestListener ====================

    @Override
    public void onTestStart(ITestResult result) {
        log.info("▶ TEST STARTED : {}", result.getName());
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        log.info("✔ TEST PASSED  : {} ({}ms)",
                result.getName(),
                result.getEndMillis() - result.getStartMillis());
    }

    @Override
    public void onTestFailure(ITestResult result) {
        log.error("✘ TEST FAILED  : {} — {}",
                result.getName(),
                result.getThrowable() != null ? result.getThrowable().getMessage() : "N/A");
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        log.warn("⚠ TEST SKIPPED : {}", result.getName());
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
        log.warn("~ TEST WITHIN SUCCESS THRESHOLD : {}", result.getName());
    }

    @Override
    public void onStart(ITestContext context) {
        log.info("── Test context started : {}", context.getName());
    }

    @Override
    public void onFinish(ITestContext context) {
        log.info("── Test context finished : {} | Passed={} | Failed={} | Skipped={}",
                context.getName(),
                context.getPassedTests().size(),
                context.getFailedTests().size(),
                context.getSkippedTests().size());
    }
}
