package com.huawei.dtm.invoke;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@EnableConfigurationProperties
@SpringBootApplication(scanBasePackages = {"com.huawei"})
public class InvokeMain {
    @Bean
    public FailedEventListener failedEventListener() {
        return new FailedEventListener();
    }

    public static void main(String[] args) {
        SpringApplication.run(InvokeMain.class, args);
    }
}
