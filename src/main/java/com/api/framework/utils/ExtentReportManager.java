package com.api.framework.utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import com.api.framework.config.ConfigManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * ExtentReportManager - Thread-safe singleton managing ExtentReports lifecycle.
 * Generates a timestamped HTML report under the configured reports/ directory.
 */
public class ExtentReportManager {

    private static final Logger log = LogManager.getLogger(ExtentReportManager.class);
    private static ExtentReports extentReports;
    private static final ThreadLocal<ExtentTest> extentTest = new ThreadLocal<>();

    private ExtentReportManager() {}

    public static synchronized ExtentReports getInstance() {
        if (extentReports == null) {
            String timestamp = LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
            String reportPath = ConfigManager.getConfig().reportPath()
                    + "ExtentReport_" + timestamp + ".html";

            ExtentSparkReporter sparkReporter = new ExtentSparkReporter(reportPath);
            sparkReporter.config().setTheme(Theme.DARK);
            sparkReporter.config().setDocumentTitle("API Automation Report");
            sparkReporter.config().setReportName("RestAssured Framework — Test Results");
            sparkReporter.config().setEncoding("UTF-8");

            extentReports = new ExtentReports();
            extentReports.attachReporter(sparkReporter);
            extentReports.setSystemInfo("Framework", "RestAssured + TestNG");
            extentReports.setSystemInfo("Environment",
                    ConfigManager.getConfig().environment());
            extentReports.setSystemInfo("Base URL",
                    ConfigManager.getConfig().reqresBaseUrl());
            extentReports.setSystemInfo("Java Version",
                    System.getProperty("java.version"));
            extentReports.setSystemInfo("OS",
                    System.getProperty("os.name") + " " + System.getProperty("os.arch"));

            log.info("ExtentReports initialized. Report path: {}", reportPath);
        }
        return extentReports;
    }

    public static void setTest(ExtentTest test) {
        extentTest.set(test);
    }

    public static ExtentTest getTest() {
        return extentTest.get();
    }

    public static void removeTest() {
        extentTest.remove();
    }

    public static synchronized void flush() {
        if (extentReports != null) {
            extentReports.flush();
            log.info("ExtentReports flushed successfully.");
        }
    }
}
