/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2019-2019. All rights reserved.
 */

package com.huawei.client;

import com.huawei.client.service.TransferService;
import com.huawei.common.util.CmdUtils;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 程序的入口，根据输入分别去调用不同的场景用例
 */
@Component
public class NoninvasiveStarter {

    @Autowired
    private TransferService transferService;

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(NoninvasiveStarter.class);

    private static final int ACCOUNT = 500;

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
                    case INIT_DB:
                        transferService.initAllAccount();
                        break;
                    case QUERY_ACCOUNT:
                        transferService.queryMoney();;
                        break;
                    case TRANSFER_DEMO:
                        transferService.doExecuteDemo(userIds);
                        break;
                    case EXIT:
                        System.exit(0);
                        break;
                }
            } catch (Throwable e) {
                // ignore
                LOGGER.error(e.getMessage());
            }
        }
    }
}
