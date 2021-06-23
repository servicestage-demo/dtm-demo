/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021. All rights reserved.
 */

package com.huawei.dubbo.mq.service.model;

import com.huawei.middleware.dtm.client.tcc.rocketmq.DtmRocketMqProducer;

import org.apache.rocketmq.common.message.Message;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class RocketMqTemplate implements InitializingBean, DisposableBean {
    private final DtmRocketMqProducer producer;

    public RocketMqTemplate(DtmRocketMqProducer producer) {
        this.producer = producer;
    }

    @Override
    public void destroy() throws Exception {
        producer.shutdown();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        producer.start();
    }

    public void sendMsg(int userId, int money) throws Exception {
        // 为了保证消费幂等性，每一个消息带一个 唯一的UUID。
        String uuid = UUID.randomUUID().toString();
        String msgBody = uuid + "__" + money + "__" + userId;
        Message msg =
            new Message("dtm-topic-mq", "tag", msgBody.getBytes(StandardCharsets.UTF_8));
        producer.sendMessageInTransaction(msg, null);
    }
}
