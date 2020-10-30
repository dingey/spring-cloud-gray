package com.github.dingey.gray;

import org.springframework.util.StringUtils;

/**
 * @author d
 */
public class GrayContext {
    private static final ThreadLocal<String> version = new ThreadLocal<>();
    public static final String PREFIX = "version";

    private GrayContext() {
    }

    public static boolean isGray() {
        return StringUtils.hasText(getVersion());
    }

    public static String getVersion() {
        return version.get();
    }

    public static void setVersion(String version) {
        GrayContext.version.set(version);
    }

    public static void removeVersion() {
        GrayContext.version.remove();
    }
}
