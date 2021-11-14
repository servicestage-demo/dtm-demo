package com.huawei.dtm.kafka.consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;

@EnableEurekaClient
@EnableFeignClients
@SpringBootApplication
public class KafkaConsumerApplication implements ApplicationRunner {

    @Autowired
    private ConsumerTemplate consumerTemplate;

    public static void main(String[] args) {
        SpringApplication.run(KafkaConsumerApplication.class, args);

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
