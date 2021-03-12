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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
/**
 * 程序的入口，根据输入分别去调用不同的场景用例
 */
@Component
public class NoninvasiveStarter {
    public String lbMvcDtmTransferPath = "cse://%s/bank/transfer?transferMoney=%s&id=%s&errRate=%s";
    private BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
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
                    case TRANSFER_DEMO:
                        doExecuteDemo();
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
    /**
     * 微服务调用bankA转入、bankB转出
     */
    private void doExecuteDemo() throws Exception {
        CmdUtils.println("请输入线程数量:事物数量:异常概率");
        String input = console.readLine();
        int threadNum = Integer.parseInt(input.split(":")[0]);
        int txNum = Integer.parseInt(input.split(":")[1]);
        int errRate = Integer.parseInt(input.split(":")[2]);
        Random random = new Random();
        CountDownLatch countDownLatch = new CountDownLatch(threadNum);
        long beforeTime = System.currentTimeMillis();
        for (int i = 0; i < threadNum; i++) {
            Thread.sleep(10);
            new Thread(() -> {
                for (int j = 0; j < txNum; j++) {
                    int money = 100;
                    try {
                        restInvoker.getForObject(String.format(lbMvcDtmTransferPath, "bank-a", money, random.nextInt(10), errRate),
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
}
