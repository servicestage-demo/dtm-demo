package com.huawei.kafka.consumer;

import org.apache.servicecomb.springboot2.starter.EnableServiceComb;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableServiceComb
public class KafkaConsumerApplication implements ApplicationRunner {

    @Autowired
    private ConsumerTemplate consumerTemplate;

    public static void main(String[] args) throws Exception {
        try {
            SpringApplication.run(KafkaConsumerApplication.class, args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run(ApplicationArguments args) {
        consumerTemplate.receiveMsg();
    }
}
