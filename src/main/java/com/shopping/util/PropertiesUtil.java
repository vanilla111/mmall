package com.shopping.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

/**
 * Created by wang on 2017/5/14.
 */
@Slf4j
public class PropertiesUtil {

    private static Properties properties;

    static {
        String fileName = "shopping.properties";
        properties = new Properties();
        try {
            properties.load(
                    new InputStreamReader(PropertiesUtil.class.getClassLoader().getResourceAsStream(fileName), "UTF-8")
            );
        } catch (IOException e) {
            log.error("配置文件异常");
        }
    }

    public static String getProperty(String key) {
        String value = properties.getProperty(key.trim());
        if (StringUtils.isBlank(value))
            return null;

        return value.trim();
    }

    public static String getProperty(String key, String defaultValue) {
        String value = properties.getProperty(key.trim());
        if (StringUtils.isBlank(value))
            return defaultValue;

        return value.trim();
    }
}
