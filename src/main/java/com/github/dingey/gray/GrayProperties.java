package com.github.dingey.gray;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

/**
 * @author d
 */
@RefreshScope
@ConfigurationProperties(prefix = "gray")
public class GrayProperties {
    /**
     * 是否启用灰度
     */
    private boolean enable = false;

    /**
     * 是否隔离灰度环境，true下如果非灰度环境无实例，则抛出异常；false下非灰度环境无实例会调灰度服务
     */
    private boolean isolation = false;

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public boolean isIsolation() {
        return isolation;
    }

    public void setIsolation(boolean isolation) {
        this.isolation = isolation;
    }
}
