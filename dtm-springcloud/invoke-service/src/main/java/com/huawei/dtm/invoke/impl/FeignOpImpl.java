/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021. All rights reserved.
 */

package com.huawei.dtm.invoke.impl;

import com.huawei.dtm.invoke.IBankOperator;
import com.huawei.dtm.invoke.service.intf.BankCenterService;
import com.huawei.dtm.invoke.service.intf.MqService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnExpression("'${dtm.invoke.mode}' == 'feign'")
public class FeignOpImpl implements IBankOperator {
    private static final Logger LOGGER = LoggerFactory.getLogger(FeignOpImpl.class);

    private final BankCenterService bankCenterService;

    private final MqService mqService;

    public FeignOpImpl(BankCenterService bankCenterService, MqService mqService) {
        this.bankCenterService = bankCenterService;
        this.mqService = mqService;
    }

    @Override
    public void initBank(int accountNum, int initMoney) {
        for (int i = 0; i < accountNum; i++) {
            bankCenterService.init(i, initMoney);
        }
        LOGGER.info("Use feign invoke mode init bankA and bankB success");
    }

    @Override
    public void transferTcc(int userId, int transferMoney, boolean successTransfer) {
        bankCenterService.transferTcc(userId, transferMoney, successTransfer);
        LOGGER.info("Use feign invoke mode start transfer tcc");
    }

    @Override
    public long queryBankMoney(boolean isBankA, int userId) {
        if (isBankA) {
            return bankCenterService.queryAById(userId);
        }
        return bankCenterService.queryBById(userId);
    }

    @Override
    public void transferMq(int errRate, int transferMoney, int userId) {
        mqService.transfer(userId, transferMoney, errRate);
    }

    @Override
    public String microTransfer(int errRate, int transferMoney, int userId) {
        return bankCenterService.transfer(userId, transferMoney, errRate);
    }

    @Override
    public String currentMode() {
        return "feign";
    }
}
