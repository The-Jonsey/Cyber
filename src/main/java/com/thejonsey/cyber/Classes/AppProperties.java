package com.thejonsey.cyber.Classes;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Reads config from app properties
 */
@Configuration("appProperties")
@ConfigurationProperties(prefix="com.thejonsey.cyber")
public class AppProperties {
    public Integer getPage_size() {
        return page_size;
    }

    public void setPage_size(Integer page_size) {
        this.page_size = page_size;
    }

    private Integer page_size;
    private Integer analysis_amount;

    public Integer getAnalysis_amount() {
        return analysis_amount;
    }

    public void setAnalysis_amount(Integer analysis_amount) {
        this.analysis_amount = analysis_amount;
    }
}
