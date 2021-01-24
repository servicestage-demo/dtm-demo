/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2019-2019. All rights reserved.
 */

package com.huawei.client;

import com.huawei.client.service.TransferService;
import com.huawei.common.util.CmdUtils;

import org.apache.servicecomb.provider.springmvc.reference.RestTemplateBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

@Component
public class NoninvasiveStarter {
    public String lbMvcDtmTransferPath = "cse://%s/bank/transfer?transferMoney=%s&id=%s";

    @Autowired
    private TransferService service;

    private final RestTemplate restInvoker = RestTemplateBuilder.create();

    public void start() throws Exception {
        Thread.sleep(6000);
        while (true) {
            try {
                Arrays.stream(MenuOperationsEnum.values()).forEach(CmdUtils::println);
                CmdUtils.println("请选择命令执行操作");
                int cmd = CmdUtils.readCmd(6);
                switch (MenuOperationsEnum.values()[cmd]) {
                    case INIT_DB:
                        service.initAllAccount();
                        break;
                    case QUERY_ACCOUNT:
                        service.queryAllMoneyWithTx();
                        break;
                    case TRANSFER_DEMO_LOCAL:
                        doExecuteMulti();
                        break;
                    case EXIT:
                        System.exit(0);
                        break;
                }
            } catch (Throwable e) {
                // ignore
            }
        }
    }

    private void doExecuteDemo() throws InterruptedException {
        int threadNum = 10;
        int txNum = 40;
        Random random = new Random();
        CountDownLatch countDownLatch = new CountDownLatch(threadNum);
        long beforeTime = System.currentTimeMillis();
        for (int i = 0; i < threadNum; i++) {
            Thread.sleep(10);
            new Thread(() -> {
                for (int j = 0; j < txNum; j++) {
                    int money = 100;
                    try {
                        restInvoker.getForObject(String.format(lbMvcDtmTransferPath, random.nextInt() % 10, money),
                            String.class);
                        Thread.sleep(100);
                    } catch (Exception e) {
                        // ignore
                        e.printStackTrace();
                    }
                }
                countDownLatch.countDown();
            }).start();
        }
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        CmdUtils.println("total cost: %s ms", System.currentTimeMillis() - beforeTime + "");
        service.queryMoney();
    }

    private void doExecuteMulti() throws InterruptedException {
        String readStr = "20|50";
        String[] split = readStr.split("\\|");
        int threadNum = Integer.parseInt(split[0]);
        int txNum = Integer.parseInt(split[1]);
        Random random = new Random();
        CountDownLatch countDownLatch = new CountDownLatch(threadNum);
        long beforeTime = System.currentTimeMillis();
        for (int i = 0; i < threadNum; i++) {
            new Thread(() -> {
                for (int j = 0; j < txNum; j++) {
                    int money = 100;
                    try {
                        service.transfer(random.nextInt() % 10, money);
                    } catch (Exception e) {
                        // ignore
                        //e.printStackTrace();
                    }
                }
                countDownLatch.countDown();
            }).start();
        }
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            //e.printStackTrace();
        }
        CmdUtils.println("total cost: %s ms", System.currentTimeMillis() - beforeTime + "");
        service.queryMoney();
    }
}
