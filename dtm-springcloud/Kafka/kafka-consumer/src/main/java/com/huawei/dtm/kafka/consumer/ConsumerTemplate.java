/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021. All rights reserved.
 */

package com.huawei.dtm.kafka.consumer;

import com.huawei.common.impl.BankBService;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import java.time.Duration;

public class ConsumerTemplate implements InitializingBean, DisposableBean {
    private final KafkaConsumer<String, String> consumer;
    private final BankBService bankBService;

    private static final Logger LOGGER = LoggerFactory.getLogger(ConsumerTemplate.class);

    public ConsumerTemplate(KafkaConsumer<String, String> consumer, BankBService bankBService) {
        this.consumer = consumer;
        this.bankBService = bankBService;
    }

    @Override
    public void destroy() throws Exception {
        consumer.close();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        // producer.start();
    }

    public void receiveMsg() {
        while (true) {
            // 这里的参数指的是轮询的时间间隔，也就是多长时间去拉一次数据
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(3000));
            records.forEach((ConsumerRecord<String, String> record) -> {
                LOGGER.info("Kafka Consumer Receive msg: {}", record);
                String[] arr = record.value().split("__");
                bankBService.transferOut(Integer.parseInt(arr[2]), Integer.parseInt(arr[1]));
            });
        }
    }
}
