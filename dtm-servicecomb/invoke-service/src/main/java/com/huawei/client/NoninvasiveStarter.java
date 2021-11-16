/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2019-2019. All rights reserved.
 */

package com.huawei.client;

import com.huawei.client.service.TransferService;
import com.huawei.client.utils.CmdUtils;

import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 程序的入口，根据输入分别去调用不同的场景用例
 */
public class NoninvasiveStarter {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(NoninvasiveStarter.class);

    private static final int ACCOUNT = 500;

    public static final int MICRO_TRANSFER = 0;

    public static final int MQ_TRANS = 1;

    public static final int KAFKA_TRANS = 2;

    private final TransferService transferService = new TransferService();

    public void start() throws Exception {
        Thread.sleep(8000);
        List<Integer> userIds = new ArrayList<>();
        for (int i = 0; i < ACCOUNT; i++) {
            userIds.add(i);
        }
        Collections.shuffle(userIds);
        while (true) {
            try {
                Arrays.stream(MenuOperationsEnum.values()).forEach(CmdUtils::println);
                CmdUtils.println("请选择命令执行操作");
                int cmd = CmdUtils.readCmd(6);
                switch (MenuOperationsEnum.values()[cmd]) {
                    case EXIT:
                        System.exit(0);
                        break;
                    case DTM_INIT_DB:
                        transferService.initBankAccount();
                        break;
                    case DTM_QUERY_ACCOUNT:
                        transferService.queryBankMoney();
                        break;
                    case DTM_TRANSFER_MICRO:
                        transferService.doExecuteMicro(userIds, MICRO_TRANSFER);
                        break;
                    case DTM_MQ_MICRO:
                        transferService.doExecuteMicro(userIds, MQ_TRANS);
                        break;
                    case DTM_KAFKA_MICRO:
                        transferService.doExecuteMicro(userIds, KAFKA_TRANS);
                        break;
                    case DTM_TCC_MICRO:
                        transferService.transferTcc();
                        break;
                }
            } catch (Throwable e) {
                // ignore
                LOGGER.error(e.getMessage());
            }
        }
    }
}
