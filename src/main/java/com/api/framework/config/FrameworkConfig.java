package com.api.framework.config;

import org.aeonbits.owner.Config;
import org.aeonbits.owner.Config.Sources;

/**
 * Framework configuration interface using Owner library.
 * Properties are loaded from config.properties file.
 * Supports environment-based overrides via system properties.
 */
@Sources({
    "system:properties",
    "system:env",
    "classpath:config.properties"
})
public interface FrameworkConfig extends Config {

    @Key("base.url")
    @DefaultValue("https://reqres.in")
    String baseUrl();

    @Key("reqres.base.url")
    @DefaultValue("https://reqres.in")
    String reqresBaseUrl();

    @Key("jsonplaceholder.base.url")
    @DefaultValue("https://jsonplaceholder.typicode.com")
    String jsonPlaceholderBaseUrl();

    @Key("connection.timeout")
    @DefaultValue("30000")
    int connectionTimeout();

    @Key("read.timeout")
    @DefaultValue("30000")
    int readTimeout();

    @Key("retry.count")
    @DefaultValue("3")
    int retryCount();

    @Key("enable.logging")
    @DefaultValue("true")
    boolean enableLogging();

    @Key("log.level")
    @DefaultValue("INFO")
    String logLevel();

    @Key("report.path")
    @DefaultValue("reports/")
    String reportPath();

    @Key("screenshot.on.failure")
    @DefaultValue("true")
    boolean screenshotOnFailure();

    @Key("environment")
    @DefaultValue("QA")
    String environment();

    @Key("reqres.api.key")
    @DefaultValue("reqres_cba85c36ac314e478cc78a2310c6fb92")
    String reqresApiKey();

    @Key("default.content.type")
    @DefaultValue("application/json")
    String defaultContentType();

    @Key("proxy.enabled")
    @DefaultValue("false")
    boolean proxyEnabled();

    @Key("proxy.host")
    @DefaultValue("localhost")
    String proxyHost();

    @Key("proxy.port")
    @DefaultValue("8080")
    int proxyPort();
}
