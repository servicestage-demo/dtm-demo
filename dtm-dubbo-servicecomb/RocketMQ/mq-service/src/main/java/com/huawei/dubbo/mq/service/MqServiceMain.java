package com.huawei.dubbo.mq.service;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportResource;

@EnableConfigurationProperties
@SpringBootApplication
@ImportResource({"classpath*:spring/dubbo-provider.xml", "classpath*:spring/dubbo-servicecomb.xml"})
public class MqServiceMain {
    public static void main(String[] args) {
        new SpringApplicationBuilder(MqServiceMain.class).web(WebApplicationType.NONE).run(args);
    }

    @Bean
    public FailedEventListener failedEventListener() {
        return new FailedEventListener();
    }
}
