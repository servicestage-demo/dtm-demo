package com.huawei.dtm.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@EnableConfigurationProperties
@SpringBootApplication(scanBasePackages = {"com.huawei"})
public class ClientMain {

    @Bean
    public FailedEventListener failedEventListener() {
        return new FailedEventListener();
    }

    public static void main(String[] args) {
        SpringApplication.run(ClientMain.class, args);
    }
}
