package com.automation.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigReader {

    private static Properties properties;

    static {
        try {
            properties = new Properties();

            FileInputStream file =
                    new FileInputStream(
                            "src/main/resources/config.properties"
                    );

            properties.load(file);

            file.close();

        } catch (IOException e) {
            throw new RuntimeException(
                    "Unable to load config.properties", e
            );
        }
    }

    public static String getProperty(String key) {
        return properties.getProperty(key);
    }
}