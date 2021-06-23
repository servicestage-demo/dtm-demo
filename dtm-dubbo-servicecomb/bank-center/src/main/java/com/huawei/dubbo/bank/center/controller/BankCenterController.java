/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021. All rights reserved.
 */

package com.huawei.dubbo.bank.center.controller;

import com.huawei.dubbo.common.intf.IBankAController;
import com.huawei.dubbo.common.intf.IBankBController;
import com.huawei.dubbo.common.intf.IBankCenterController;
import com.huawei.middleware.dtm.client.annotations.DTMTxBegin;
import com.huawei.middleware.dtm.client.context.DTMContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class BankCenterController implements IBankCenterController {
    private static final Logger LOGGER = LoggerFactory.getLogger(BankCenterController.class);

    @Autowired
    private IBankAController bankAController;

    @Autowired
    private IBankBController bankBController;

    @Override
    @DTMTxBegin(appName = "noninvasive-transfer-dubbo-servicecomb")
    public String transfer(int id, int money, int errRate) {
        LOGGER.info("Bank-center start invoke bankA and bankB: {}", DTMContext.getDTMContext().getGlobalTxId());
        bankAController.transfer(id, money, errRate);
        bankBController.transfer(id, money, errRate);
        return "ok";
    }

    @Override
    @DTMTxBegin(appName = "tcc-transfer-dubbo-servicecomb")
    public String transferTcc(int id, int money, boolean exception) {
        LOGGER.info("Bank-center start invoke bankA and bankB by tcc_mode");
        bankAController.tryTransferIn(id, money);
        bankBController.tryTransferOut(id, money);
        if (exception) {
            throw new RuntimeException("Add a potential exception");
        }
        return "ok";
    }

    @Override
    public String init(int userId, int money) {
        LOGGER.info("Bank-center init bankA and bankB");
        bankAController.init(userId, money);
        bankBController.init(userId, money);
        return "ok";
    }

    @Override
    public long queryById(boolean isBankA, int userId) {
        LOGGER.info("Bank-center queryById");
        if (isBankA) {
            return bankAController.query(userId);
        }
        return bankBController.query(userId);
    }
}
