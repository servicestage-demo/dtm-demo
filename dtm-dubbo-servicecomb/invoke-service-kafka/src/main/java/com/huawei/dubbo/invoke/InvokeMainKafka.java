package com.huawei.dubbo.invoke;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportResource;

@EnableConfigurationProperties
@SpringBootApplication(scanBasePackages = {"com.huawei"})
@ImportResource({"classpath*:spring/dubbo-provider.xml", "classpath*:spring/dubbo-servicecomb.xml"})
public class InvokeMainKafka {
    public static void main(String[] args) {
        new SpringApplicationBuilder(InvokeMainKafka.class).web(WebApplicationType.NONE).run(args);
    }

    @Bean
    public FailedEventListener failedEventListener() {
        return new FailedEventListener();
    }
}
