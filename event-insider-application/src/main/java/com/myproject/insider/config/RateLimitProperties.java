package com.myproject.insider.config;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ConfigurationProperties(prefix = "app.rate-limit")
public class RateLimitProperties {

    private boolean enabled = true;
    private int maxRequests = 100;
    private Duration window = Duration.ofMinutes(15);
    private String counterKeyPrefix = "rl:ip:";
    private String blacklistRedisKey = "rl:blacklist:ips";
    private List<String> blacklistBootstrapIps = new ArrayList<>();
}
