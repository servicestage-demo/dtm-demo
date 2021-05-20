package com.huawei.bankb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;

@EnableEurekaClient
@EnableConfigurationProperties
@SpringBootApplication(scanBasePackages = {"com.huawei.bankb", "com.huawei.common"})
public class BankBMain {
    public static void main(String[] args) {
        SpringApplication.run(BankBMain.class, args);
    }

    @Bean
    public FailedEventListener failedEventListener() {
        return new FailedEventListener();
    }
}
