package com.huawei.dtm.kafka.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;

@EnableDiscoveryClient
@EnableFeignClients
@SpringBootApplication
public class KafkaServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(KafkaServerApplication.class, args);
    }

    @Bean
    public FailedEventListener failedEventListener() {
        return new FailedEventListener();
    }
}
