/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2019-2019. All rights reserved.
 */

package com.huawei.client.service;

import com.huawei.common.BankService;
import com.huawei.common.error.ExceptionUtil;
import com.huawei.common.util.CmdUtils;
import com.huawei.middleware.dtm.client.annotations.DTMTxBegin;
import com.huawei.middleware.dtm.client.context.DTMContext;

import java.util.ArrayList;
import java.util.List;

public class TransferService {
    private static final String PRINT_TMPL = "|%14s|%19s|%19s|%13s|";

    public static final int ACCOUNT_NUMBER = 10;

    public static int initialAccountMoney = 1000000;

    private BankService bankAService;

    private BankService bankBService;

    public TransferService(BankService bankAService, BankService bankBService
       ) {
        this.bankAService = bankAService;
        this.bankBService = bankBService;
    }

    public void initAllAccount() {
        List<Integer> userIds = new ArrayList<>();
        for (int i = 0; i < ACCOUNT_NUMBER; i++) {
            userIds.add(i);
        }
        int value = 1000000;
        bankAService.initAllAccount(userIds, value);
        bankBService.initAllAccount(userIds, value);
        CmdUtils.println(
            "Init account: userIds %s , bank a value %s ,bank b value %s ,total %s ",
            userIds + "", value + "", value + "", value + value + "");
    }

    @DTMTxBegin(appName = "noninvasive-transfer-service")
    public void transfer(int userId, int money) {
        long globalTxId = DTMContext.getDTMContext().getGlobalTxId();
        CmdUtils.println("run transfer service with user id: %s, globalTxId: %s",
            userId + "", globalTxId + "");
        bankAService.transferIn(userId, money);
        ExceptionUtil.addRuntimeException(50);
        bankBService.transferOut(userId, money);
    }

    @DTMTxBegin(appName = "noninvasive-transfer-service-select-for-update", timeout = 30000)
    public void queryAllMoneyWithTx() {
        queryMoney();
    }

    public void queryMoney() {
        CmdUtils.println("|--- userId ---|--- bankA-money ---|--- bankB-money ---|---- sum ----|");
        for (int id = 0; id < ACCOUNT_NUMBER; id++) {
            long bankA = bankAService.queryAccountMoney(id);
            long bankB = bankBService.queryAccountMoney(id);
            long total = bankA + bankB;
            if (total != 2 * initialAccountMoney) {
                CmdUtils.println("account of userId:%s error. bankA:%s, bankB:%s, total:%s ",
                    id + "", bankA + "", bankB + "", total + "");
            }
            else {
                CmdUtils.println(PRINT_TMPL, id + "", bankA + "", bankB + "", total + "");
            }
        }
    }
}
