package com.huawei.dtm.invoke;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;

@EnableConfigurationProperties
@EnableFeignClients
@SpringBootApplication(scanBasePackages = {"com.huawei"}, exclude = {DataSourceAutoConfiguration.class})
public class InvokeMain {
    @Bean
    public FailedEventListener failedEventListener() {
        return new FailedEventListener();
    }

    public static void main(String[] args) {
        SpringApplication.run(InvokeMain.class, args);
    }
}
