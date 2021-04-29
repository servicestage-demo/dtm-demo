/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2019-2019. All rights reserved.
 */

package com.huawei.client.service;

import com.huawei.common.util.CmdUtils;

import org.apache.servicecomb.provider.springmvc.reference.RestTemplateBuilder;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.concurrent.CountDownLatch;
@Component
public class TransferService {
    private static final String PRINT_TMPL = "|%14s|%19s|%19s|%13s|";

    private static final int ACCOUNT = 500;

    public static final int ACCOUNT_NUMBER = 500;

    public static int initialAccountMoney = 1000000;

    public static final int TRAN_MONEY = 100;

    private static final String CENTER_TRANSFER = "cse://bank-center/bankcenter/transfer?id=%s&money=%s&errRate=%s";

    private static final String CENTER_INIT = "cse://bank-center/bankcenter/init?userIds=%s&money=%s";
    private static final String CENTER_QUERYABYID = "cse://bank-center/bankcenter/queryAById?id=%s";
    private static final String CENTER_QUERYBBYID = "cse://bank-center/bankcenter/queryBById?id=%s";

    private static final String CENTER_QUERYA = "cse://bank-center/bankcenter/queryA";
    private static final String CENTER_QUERYB = "cse://bank-center/bankcenter/queryB";


    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(TransferService.class);

    private BufferedReader console = new BufferedReader(new InputStreamReader(System.in));

    private final RestTemplate restInvoker = RestTemplateBuilder.create();

    /**
     * 初始化数据库
     */
    public void initAllAccount() {
        restInvoker.getForObject(String.format(CENTER_INIT, ACCOUNT, initialAccountMoney), String.class);
        LOGGER.info("Init bankA bankB success");
    }

    /**
     * 查询bankA和bankB余额
     */
    public void queryMoney() {
        CmdUtils.println("|--- userId ---|--- bankA-money ---|--- bankB-money ---|---- sum ----|");
        for (int id = 0; id < ACCOUNT_NUMBER; id++) {
            long bankA = restInvoker.getForObject(String.format(CENTER_QUERYABYID, id), Long.class);
            long bankB = restInvoker.getForObject(String.format(CENTER_QUERYBBYID, id), Long.class);
            long total = bankA + bankB;
            if (total != 2 * initialAccountMoney) {
                CmdUtils.println("account of userId:%s error. bankA:%s, bankB:%s, total:%s ",
                    id + "", bankA + "", bankB + "", total + "");
            } else {
                CmdUtils.println(PRINT_TMPL, id + "", bankA + "", bankB + "", total + "");
            }
        }
        long totalA = restInvoker.getForObject(String.format(CENTER_QUERYA), Long.class);
        long totalB = restInvoker.getForObject(String.format(CENTER_QUERYB), Long.class);
        long total = totalA + totalB;
        LOGGER.info("Run finish. total a {},total b {},sum {}", totalA, totalB, total);
    }

    /**
     * 微服务调用bankA转入、bankB转出
     */
    public void doExecuteDemo(List<Integer> userIds) throws Exception {
        CmdUtils.println("请输入线程数量:单线程事务数量:异常概率");
        String input = console.readLine();
        int threadNum = Integer.parseInt(input.split(":")[0]);
        int txNum = Integer.parseInt(input.split(":")[1]);
        int errRate = Integer.parseInt(input.split(":")[2]);
        if (threadNum < 1 || threadNum > 10) {
            throw new IllegalArgumentException("线程数量取值范围为1到10的整数");
        }
        if (txNum < 1 || txNum > 50) {
            throw new IllegalArgumentException("单线程事务数量取值范围为1到50的整数");
        }
        if (errRate < 0 || errRate > 100) {
            throw new IllegalArgumentException("异常概率取值范围为0到100的整数");
        }
        CountDownLatch countDownLatch = new CountDownLatch(threadNum);
        long beforeTime = System.currentTimeMillis();
        for (int i = 0; i < threadNum; i++) {
            final int count = i;
            new Thread(() -> {
                for (int j = 0; j < txNum; j++) {
                    try {
                        restInvoker.getForObject(String.format(CENTER_TRANSFER, userIds.get((count * txNum + j) % ACCOUNT), TRAN_MONEY, errRate), String.class);
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
        queryMoney();
    }
}
