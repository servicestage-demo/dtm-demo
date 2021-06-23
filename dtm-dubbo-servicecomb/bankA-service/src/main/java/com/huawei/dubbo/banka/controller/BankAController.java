/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021. All rights reserved.
 */

package com.huawei.dubbo.banka.controller;

import com.huawei.dubbo.common.impl.BankAService;
import com.huawei.dubbo.common.intf.IBankAController;
import com.huawei.middleware.dtm.client.context.DTMContext;
import com.huawei.middleware.dtm.client.tcc.annotations.DTMTccBranch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class BankAController implements IBankAController {
    private static final Logger LOGGER = LoggerFactory.getLogger(BankAController.class);

    @Autowired
    private BankAService bankAService;

    @Override
    public String transfer(int id, int money, int errRate) {
        LOGGER.info("global tx id:{}, transfer in", DTMContext.getDTMContext().getGlobalTxId());
        bankAService.transferIn(id, money);
        return "ok";
    }

    @Override
    @DTMTccBranch(identifier = "tcc-banka-try-transfer-in", confirmMethod = "confirm", cancelMethod = "cancel")
    public void tryTransferIn(int id, int money) {
        bankAService.tryTransferIn(id, money);
    }

    public void confirm() {
        bankAService.confirm();
    }

    public void cancel() {
        bankAService.cancel();
    }

    @Override
    public String init(int userId, int money) {
        LOGGER.info("bankA init");
        bankAService.initUserAccount(userId, money);
        return "ok";
    }

    @Override
    public long query(int id) {
        return bankAService.queryMoneyById(id);
    }
}
