package com.huawei.dubbo.kafka.consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportResource;

@EnableConfigurationProperties
@SpringBootApplication
@ImportResource({"classpath*:spring/dubbo-provider.xml", "classpath*:spring/dubbo-servicecomb.xml"})
public class KafkaConsumerMain implements ApplicationRunner {

    @Autowired
    private ConsumerTemplate consumerTemplate;

    public static void main(String[] args) {
        new SpringApplicationBuilder(KafkaConsumerMain.class).web(WebApplicationType.NONE).run(args);
    }

    @Override
    public void run(ApplicationArguments args) {
        consumerTemplate.receiveMsg();
    }

    @Bean
    public FailedEventListener failedEventListener() {
        return new FailedEventListener();
    }
}
