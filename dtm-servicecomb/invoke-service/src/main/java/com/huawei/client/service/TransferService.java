/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2019-2019. All rights reserved.
 */

package com.huawei.client.service;

import com.huawei.client.utils.CmdUtils;

import org.apache.servicecomb.provider.springmvc.reference.RestTemplateBuilder;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

@Component
public class TransferService {
    public static final int INIT_MONEY = 1000000;

    public static final int TRAN_MONEY = 100;

    public static final int MICRO_TRANSFER = 0;

    public static final int MQ_TRANS = 1;

    public static final int KAFKA_TRANS = 2;

    public static final int ACCOUNT = 500;

    private static final String PRINT_TMPL = "|%14s|%19s|%19s|%13s|";

    private static final String CENTER_TRANSFER
        = "cse://dtm-bankcenter/bank-center/transfer?id=%s&money=%s&errRate=%s";

    private static final String CENTER_TCC
        = "cse://dtm-bankcenter/bank-center/transferTcc?id=%s&money=%s&exception=%s";

    private static final String CENTER_INIT = "cse://dtm-bankcenter/bank-center/init?userId=%s&money=%s";

    private static final String CENTER_QUERYA_BY_ID = "cse://dtm-bankcenter/bank-center/queryAById?id=%s";

    private static final String CENTER_QUERYB_BY_ID = "cse://dtm-bankcenter/bank-center/queryBById?id=%s";

    private static final String MQ_TRANSFER
        = "cse://dtm-mq/bank-mq/transfer?id=%s&money=%s&errRate=%s";

    private static final String KAFKA_TRANSFER
        = "cse://dtm-kafka/bank-kafka/transfer?id=%s&money=%s&errRate=%s";

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(TransferService.class);

    private final RestTemplate restInvoker = RestTemplateBuilder.create();

    private BufferedReader console = new BufferedReader(new InputStreamReader(System.in));

    /**
     * 微服务调用bankA转入、bankB转出
     */
    public void doExecuteMicro(List<Integer> userIds, int type) throws Exception {
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
                        switch (type) {
                            case MICRO_TRANSFER:
                                restInvoker.getForObject(String.format(CENTER_TRANSFER, userIds.get((count * txNum + j)) % ACCOUNT, TRAN_MONEY, errRate), String.class);
                                break;
                            case MQ_TRANS:
                                restInvoker.getForObject(String.format(MQ_TRANSFER, userIds.get((count * txNum + j)) % ACCOUNT, TRAN_MONEY, errRate), String.class);
                                break;
                            case KAFKA_TRANS:
                                restInvoker.getForObject(String.format(KAFKA_TRANSFER, userIds.get((count * txNum + j)) % ACCOUNT, TRAN_MONEY, errRate), String.class);
                                break;
                        }
                        Thread.sleep(100);
                    } catch (Exception e) {
                        // ignore
                        LOGGER.error("Failed to execute : ", e);
                    }
                }
                countDownLatch.countDown();
            }).start();
        }
        try {
            countDownLatch.await();
        } catch (Throwable throwable) {
            // ignore
        }
        CmdUtils.println("total cost: %s ms", System.currentTimeMillis() - beforeTime + "");
        //        queryBankMoney();
    }

    /**
     * 初始化数据库
     */
    public void initBankAccount() {
        for (int i = 0; i < ACCOUNT; i++) {
            restInvoker.getForObject(String.format(CENTER_INIT, i, INIT_MONEY), String.class);
        }
        LOGGER.info("Init bankA bankB success");
    }

    /**
     * TCC 调用
     */
    public void transferTcc() throws IOException {
        CmdUtils.println("是否模拟发生异常情况: 输入 0 无异常  输入 1 发生异常");
        int exception = CmdUtils.readCmd(2);
        int userId = new Random().nextInt(ACCOUNT);
        restInvoker.getForObject(String.format(CENTER_TCC, userId, TRAN_MONEY, exception != 0), String.class);
        LOGGER.info("TCC bankA bankB success");
    }

    /**
     * 查询 Bank A 和 Bank B 余额
     */
    public void queryBankMoney() {
        CmdUtils.println("|--- userId ---|--- bankA-money ---|--- bankB-money ---|---- sum ----|");
        long totalA = 0;
        long totalB = 0;
        for (int i = 0; i < ACCOUNT; i++) {
            long bankA = restInvoker.getForObject(String.format(CENTER_QUERYA_BY_ID, i), Long.class);
            long bankB = restInvoker.getForObject(String.format(CENTER_QUERYB_BY_ID, i), Long.class);
            totalA += bankA;
            totalB += bankB;
            long total = bankA + bankB;
            if (total != INIT_MONEY * 2) {
                CmdUtils.println("[ERROR] user id： %s, bankA: %s, bankB: %s, total: %s",
                    i + "", bankA + "", bankB + "", total + "");
            } else {
                CmdUtils.println(PRINT_TMPL, i + "", bankA + "", bankB + "", total + "");
            }
        }
        long total = totalA + totalB;
        LOGGER.info("Run finish. total a {},total b {},sum {}", totalA, totalB, total);
    }
}
