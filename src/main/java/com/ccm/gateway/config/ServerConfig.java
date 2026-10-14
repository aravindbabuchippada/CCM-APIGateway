package com.ccm.gateway.config;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "microservices")
public class ServerConfig {
    private static Logger logger = LoggerFactory.getLogger(ServerConfig.class);

    private String securityUri;
    private String clientUri;
    private String customerUri;

    @PostConstruct
    public void logUri() {
        if(logger.isInfoEnabled()) {
            logger.info("Microservices URIs loaded: security={} client={}, customer={}", securityUri, clientUri, customerUri);
        }
    }
}
