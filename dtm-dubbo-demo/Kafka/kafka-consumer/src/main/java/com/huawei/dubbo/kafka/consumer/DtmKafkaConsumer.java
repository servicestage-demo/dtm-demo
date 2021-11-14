/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021. All rights reserved.
 */

package com.huawei.dubbo.kafka.consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;

@EnableDiscoveryClient
@SpringBootApplication
public class DtmKafkaConsumer implements ApplicationRunner {
    @Autowired
    private ConsumerTemplate consumerTemplate;

    public static void main(String[] args) {
        SpringApplication.run(DtmKafkaConsumer.class, args);
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
