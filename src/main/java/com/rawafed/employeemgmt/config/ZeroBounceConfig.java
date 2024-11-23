package com.rawafed.employeemgmt.config;

import com.zerobounce.ZeroBounceSDK;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class ZeroBounceConfig {
    private final MailValidationConfigProperties configProperties;

    @Bean
    public ZeroBounceSDK zeroBounceSDK() {
        ZeroBounceSDK.getInstance().initialize(configProperties.getApiKey());
        return ZeroBounceSDK.getInstance();
    }
}
