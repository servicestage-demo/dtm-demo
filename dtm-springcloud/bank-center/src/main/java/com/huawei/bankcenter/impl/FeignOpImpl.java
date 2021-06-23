/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021. All rights reserved.
 */

package com.huawei.bankcenter.impl;

import com.huawei.bankcenter.IBankOperator;
import com.huawei.bankcenter.service.BankAIntf;
import com.huawei.bankcenter.service.BankBIntf;

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

    public FeignOpImpl(BankAIntf bankAIntf, BankBIntf bankBIntf) {
        this.bankAIntf = bankAIntf;
        this.bankBIntf = bankBIntf;
    }

    @Override
    public String transfer(int userId, int money, int errRate) {
        LOGGER.info("Start transfer---feign");
        bankAIntf.transfer(userId, money, errRate);
        bankBIntf.transfer(userId, money, errRate);
        return "ok";
    }

    @Override
    public String transferTcc(int userId, int money) {
        LOGGER.info("Start tcc---feign");
        bankAIntf.transferTcc(userId, money);
        bankBIntf.transferTcc(userId, money);
        return "ok";
    }

    @Override
    public void init(int userId, int money) {
        LOGGER.info("Start init---feign");
        bankAIntf.init(userId, money);
        bankBIntf.init(userId, money);
    }

    @Override
    public long queryById(boolean isBankA, int userId) {
        LOGGER.info("Start queryById---feign");
        if (isBankA) {
            return bankAIntf.query(userId);
        }
        return bankBIntf.query(userId);
    }
}
