package com.app.BugBee.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ssl.SslBundles;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

public class SSLConfiguration {

    @Value("${SSL_ALIAS}")
    private String keyAlias;

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder, SslBundles sslBundles) {
        return builder.setSslBundle(sslBundles.getBundle(keyAlias)).build();
    }
}
