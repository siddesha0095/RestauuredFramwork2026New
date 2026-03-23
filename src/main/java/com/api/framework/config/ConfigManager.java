package com.api.framework.config;

import org.aeonbits.owner.ConfigFactory;

/**
 * ConfigManager - Singleton that provides access to FrameworkConfig.
 * Uses Owner library for type-safe property binding with fallback support.
 */
public class ConfigManager {

    private static FrameworkConfig config;

    private ConfigManager() {}

    public static FrameworkConfig getConfig() {
        if (config == null) {
            synchronized (ConfigManager.class) {
                if (config == null) {
                    config = ConfigFactory.create(FrameworkConfig.class,
                            System.getProperties(), System.getenv());
                }
            }
        }
        return config;
    }
}
