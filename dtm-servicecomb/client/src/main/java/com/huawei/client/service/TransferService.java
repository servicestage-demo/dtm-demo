/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2019-2019. All rights reserved.
 */

package com.huawei.client.service;

import com.huawei.common.BankService;
import com.huawei.common.util.CmdUtils;

import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class TransferService {
    private static final String PRINT_TMPL = "|%14s|%19s|%19s|%13s|";

    public static final int ACCOUNT_NUMBER = 500;

    public static int initialAccountMoney = 1000000;

    private BankService bankAService;

    private BankService bankBService;

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(TransferService.class);

    public TransferService(BankService bankAService, BankService bankBService
    ) {
        this.bankAService = bankAService;
        this.bankBService = bankBService;
    }

    /**
     * 初始化数据库
     */
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

    // @DTMTxBegin(appName = "noninvasive-transfer-service-select-for-update", timeout = 30000)
    public void queryAllMoneyWithTx() {
        queryMoney();
    }

    /**
     * 查询bankA和bankB余额
     */
    public void queryMoney() {
        CmdUtils.println("|--- userId ---|--- bankA-money ---|--- bankB-money ---|---- sum ----|");
        for (int id = 0; id < ACCOUNT_NUMBER; id++) {
            long bankA = bankAService.queryAccountMoney(id);
            long bankB = bankBService.queryAccountMoney(id);
            long total = bankA + bankB;
            if (total != 2 * initialAccountMoney) {
                CmdUtils.println("account of userId:%s error. bankA:%s, bankB:%s, total:%s ",
                    id + "", bankA + "", bankB + "", total + "");
            } else {
                CmdUtils.println(PRINT_TMPL, id + "", bankA + "", bankB + "", total + "");
            }
        }
        long totalA = bankAService.queryAccountMoneySum();
        long totalB = bankBService.queryAccountMoneySum();
        long total = totalA + totalB;
        LOGGER.info("Run finish. total a {},total b {},sum {}", totalA, totalB, total);
    }
}
