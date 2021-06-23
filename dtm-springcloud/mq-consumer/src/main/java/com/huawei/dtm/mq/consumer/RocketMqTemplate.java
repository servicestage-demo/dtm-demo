/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021. All rights reserved.
 */

package com.huawei.dtm.mq.consumer;

import com.huawei.middleware.dtm.client.tcc.rocketmq.DtmRocketMqProducer;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.common.message.Message;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import java.nio.charset.StandardCharsets;

public class RocketMqTemplate implements InitializingBean, DisposableBean {
    private final DtmRocketMqProducer producer;

    private final DefaultMQPushConsumer consumer;

    public RocketMqTemplate(DtmRocketMqProducer producer, DefaultMQPushConsumer consumer) {
        this.producer = producer;
        this.consumer = consumer;
    }

    @Override
    public void destroy() throws Exception {
        producer.shutdown();
        consumer.shutdown();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        producer.start();
        consumer.start();
    }

    public void sendMsg(String body) throws Exception {
        Message msg =
            new Message("dtm-topic-mq", "tag", body.getBytes(StandardCharsets.UTF_8));
        producer.sendMessageInTransaction(msg, null);
    }
}
