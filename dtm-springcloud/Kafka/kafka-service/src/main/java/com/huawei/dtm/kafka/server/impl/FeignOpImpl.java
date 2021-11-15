/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021. All rights reserved.
 */

package com.huawei.dtm.kafka.server.impl;

import com.huawei.dtm.kafka.server.IBankOperator;
import com.huawei.dtm.kafka.server.model.KafkaTemplate;
import com.huawei.dtm.kafka.server.service.BankAIntf;
import com.huawei.dtm.kafka.server.service.BankBIntf;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnExpression("'${dtm.invoke.mode}' == 'feign'")
public class FeignOpImpl implements IBankOperator {
    private static final Logger LOGGER = LoggerFactory.getLogger(FeignOpImpl.class);

    private final BankAIntf bankAIntf;

    private final BankBIntf bankBIntf;

    private final KafkaTemplate kafkaTemplate;

    public FeignOpImpl(BankAIntf bankAIntf, BankBIntf bankBIntf, KafkaTemplate kafkaTemplate) {
        this.bankAIntf = bankAIntf;
        this.bankBIntf = bankBIntf;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public String transfer(int userId, int money, int errRate) throws Exception {
        LOGGER.info("Start transfer---feign");
        bankAIntf.transfer(userId, money * 2, errRate);
        kafkaTemplate.sendMsg(userId, money - 50);
        bankBIntf.transfer(userId, money + 50, errRate);
        return "ok";
    }

}
