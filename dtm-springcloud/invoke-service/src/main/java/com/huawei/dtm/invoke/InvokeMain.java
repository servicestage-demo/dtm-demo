package com.huawei.dtm.invoke;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;

@EnableConfigurationProperties
@EnableEurekaClient
@EnableFeignClients
@SpringBootApplication(scanBasePackages = {"com.huawei.dtm.invoke"})
public class InvokeMain {
    public static void main(String[] args) {
        SpringApplication.run(InvokeMain.class, args);
    }

    @Bean
    public FailedEventListener failedEventListener() {
        return new FailedEventListener();
    }
}
