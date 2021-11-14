/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021. All rights reserved.
 */

package com.huawei.dubbo.mq.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;

@EnableDiscoveryClient
@SpringBootApplication
public class DtmMqService {
    public static void main(String[] args) {
        SpringApplication.run(DtmMqService.class, args);
    }

    @Bean
    public FailedEventListener failedEventListener() {
        return new FailedEventListener();
    }
}
