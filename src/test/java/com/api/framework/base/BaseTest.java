package com.api.framework.base;

import com.api.framework.config.ConfigManager;
import com.api.framework.config.FrameworkConfig;
import com.api.framework.utils.ExtentReportManager;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import io.restassured.RestAssured;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.RestAssuredConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.ITestResult;
import org.testng.annotations.*;

import java.lang.reflect.Method;

@Listeners({com.api.framework.listeners.TestNGListener.class})
public class BaseTest {

    protected static final Logger log = LogManager.getLogger(BaseTest.class);
    protected static final FrameworkConfig config = ConfigManager.getConfig();

    protected ExtentReports extent;
    protected ExtentTest test;

    @BeforeSuite(alwaysRun = true)
    public void beforeSuite() {
        log.info("====================================================");
        log.info("  STARTING TEST SUITE");
        log.info("  Environment  : {}", config.environment());
        log.info("  Base URL     : {}", config.reqresBaseUrl());
        log.info("====================================================");

        extent = ExtentReportManager.getInstance();

        RestAssured.config = RestAssuredConfig.config()
                .httpClient(HttpClientConfig.httpClientConfig()
                        .setParam("http.connection.timeout", config.connectionTimeout())
                        .setParam("http.socket.timeout", config.readTimeout())
                        .setParam("http.connection-manager.timeout", config.connectionTimeout()));

        RestAssured.urlEncodingEnabled = true;
    }

    @AfterSuite(alwaysRun = true)
    public void afterSuite() {
        log.info("Test suite finished — flushing reports.");
        ExtentReportManager.flush();
    }

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        if (extent == null) {
            extent = ExtentReportManager.getInstance();
        }
        log.info("------ Starting test class: {} ------", this.getClass().getSimpleName());
    }

    @AfterClass(alwaysRun = true)
    public void afterClass() {
        log.info("------ Finished test class: {} ------", this.getClass().getSimpleName());
    }

    @BeforeMethod(alwaysRun = true)
    public void beforeMethod(Method method) {
        if (extent == null) {
            extent = ExtentReportManager.getInstance();
        }

        String testName  = method.getName();
        String className = method.getDeclaringClass().getSimpleName();
        log.info(">>> START: {}.{}", className, testName);

        Test annotation = method.getAnnotation(Test.class);
        String description = (annotation != null && !annotation.description().isEmpty())
                ? annotation.description() : testName;

        test = extent.createTest(testName, description);
        test.assignCategory(className);
        ExtentReportManager.setTest(test);
    }

    @AfterMethod(alwaysRun = true)
    public void afterMethod(ITestResult result) {
        ExtentTest currentTest = ExtentReportManager.getTest();
        if (currentTest != null) {
            switch (result.getStatus()) {
                case ITestResult.SUCCESS:
                    currentTest.log(Status.PASS, "Test PASSED");
                    log.info("<<< PASS: {}", result.getName());
                    break;
                case ITestResult.FAILURE:
                    currentTest.log(Status.FAIL, "Test FAILED: " + result.getThrowable().getMessage());
                    currentTest.fail(result.getThrowable());
                    log.error("<<< FAIL: {} — {}", result.getName(), result.getThrowable().getMessage());
                    break;
                case ITestResult.SKIP:
                    currentTest.log(Status.SKIP, "Test SKIPPED");
                    log.warn("<<< SKIP: {}", result.getName());
                    break;
                default:
                    break;
            }
        }
        ExtentReportManager.removeTest();
    }

    protected void logStep(String message) {
        log.info(message);
        ExtentTest t = ExtentReportManager.getTest();
        if (t != null) t.log(Status.INFO, message);
    }

    protected void logPass(String message) {
        log.info("PASS — {}", message);
        ExtentTest t = ExtentReportManager.getTest();
        if (t != null) t.log(Status.PASS, message);
    }

    protected void logWarning(String message) {
        log.warn(message);
        ExtentTest t = ExtentReportManager.getTest();
        if (t != null) t.log(Status.WARNING, message);
    }
}