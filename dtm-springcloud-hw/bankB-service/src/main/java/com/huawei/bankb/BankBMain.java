package com.huawei.bankb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;

@EnableDiscoveryClient
@EnableConfigurationProperties
@SpringBootApplication(scanBasePackages = {"com.huawei"})
public class BankBMain {
    @Bean
    public FailedEventListener failedEventListener() {
        return new FailedEventListener();
    }

    public static void main(String[] args) {
        SpringApplication.run(BankBMain.class, args);
    }
}
