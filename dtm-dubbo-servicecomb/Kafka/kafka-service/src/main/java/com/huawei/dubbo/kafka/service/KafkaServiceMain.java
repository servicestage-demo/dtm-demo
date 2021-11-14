package com.huawei.dubbo.kafka.service;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportResource;

@EnableConfigurationProperties
@SpringBootApplication
@ImportResource({"classpath*:spring/dubbo-provider.xml", "classpath*:spring/dubbo-servicecomb.xml"})
public class KafkaServiceMain {
    public static void main(String[] args) {
        new SpringApplicationBuilder(KafkaServiceMain.class).web(WebApplicationType.NONE).run(args);
    }

    @Bean
    public FailedEventListener failedEventListener() {
        return new FailedEventListener();
    }
}
