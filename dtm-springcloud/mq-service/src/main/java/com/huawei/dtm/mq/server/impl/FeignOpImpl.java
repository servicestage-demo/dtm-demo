/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021. All rights reserved.
 */

package com.huawei.dtm.mq.server.impl;

import com.huawei.dtm.mq.server.IBankOperator;
import com.huawei.dtm.mq.server.model.RocketMqTemplate;
import com.huawei.dtm.mq.server.service.BankAIntf;
import com.huawei.dtm.mq.server.service.BankBIntf;

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

    private final RocketMqTemplate mqTemplate;

    public FeignOpImpl(BankAIntf bankAIntf, BankBIntf bankBIntf, RocketMqTemplate mqTemplate) {
        this.bankAIntf = bankAIntf;
        this.bankBIntf = bankBIntf;
        this.mqTemplate = mqTemplate;
    }

    @Override
    public String transfer(int userId, int money, int errRate) throws Exception {
        LOGGER.info("Start transfer---feign");
        bankAIntf.transfer(userId, money * 2, errRate);
        mqTemplate.sendMsg(userId, money);
        bankBIntf.transfer(userId, money, errRate);
        return "ok";
    }

}
