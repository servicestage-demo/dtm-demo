/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021. All rights reserved.
 */

package com.huawei.dubbo.bankb.controller;

import com.huawei.dubbo.common.impl.BankBService;
import com.huawei.dubbo.common.intf.IBankBController;
import com.huawei.dubbo.common.util.ExceptionUtils;
import com.huawei.middleware.dtm.client.context.DTMContext;
import com.huawei.middleware.dtm.client.tcc.annotations.DTMTccBranch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class BankBController implements IBankBController {
    private static final Logger LOGGER = LoggerFactory.getLogger(BankBController.class);

    @Autowired
    private BankBService bankBService;

    @Override
    public String transfer(int id, int money, int errRate) {
        LOGGER.info("global tx id:{}, transfer out", DTMContext.getDTMContext().getGlobalTxId());
        ExceptionUtils.addRuntimeException(errRate);
        bankBService.transferOut(id, money);
        return "ok";
    }

    @Override
    @DTMTccBranch(identifier = "tcc-bankb-try-transfer-out", confirmMethod = "confirm", cancelMethod = "cancel")
    public void tryTransferOut(int id, int money) {
        bankBService.tryTransferOut(id, money);
    }

    public void confirm() {
        bankBService.confirm();
    }

    public void cancel() {
        bankBService.cancel();
    }

    @Override
    public String init(int userId, int money) {
        LOGGER.info("bankB init");
        bankBService.initUserAccount(userId, money);
        return "ok";
    }

    @Override
    public long query(int id) {
        return bankBService.queryMoneyById(id);
    }
}
