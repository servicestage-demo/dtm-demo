/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021. All rights reserved.
 */

package com.huawei.dubbo.kafka.service.model;

import com.huawei.middleware.dtm.client.tcc.kafka.DtmKafkaProducer;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import java.util.UUID;

public class KafkaTemplate implements InitializingBean, DisposableBean {
    private final DtmKafkaProducer<String, String> producer;

    public KafkaTemplate(DtmKafkaProducer<String, String> producer) {
        this.producer = producer;
    }

    @Override
    public void destroy() throws Exception {
        producer.close();
    }

    @Override
    public void afterPropertiesSet() throws Exception {

    }

    public void sendMsg(int userId, int money) throws Exception {
        String uuid = UUID.randomUUID().toString();
        String msgBody = uuid + "__" + money + "__" + userId;
        ProducerRecord<String, String> record = new ProducerRecord<>("dtm-kafka", msgBody);
        producer.send(record);
    }
}
