package com.huawei.dtm.invoke.service;

import com.huawei.common.impl.BankAService;
import com.huawei.common.impl.BankBService;
import com.huawei.dtm.invoke.utils.CmdUtils;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

@Component
public class TransferService {
    private static final String PRINT_TMPL = "|%14s|%19s|%19s|%13s|";

    private static final int ACCOUNT = 500;

    public static final int INIT_MONEY = 1000000;

    public static final int TRAN_MONEY = 100;

    private static final String CENTER_TRANSFER = "http://dtm-bankcenter/bank-center/transfer?id=%s&money=%s&errRate=%s";
    private static final String CENTER_INIT = "http://dtm-bankcenter/bank-center/init?userIds=%s&money=%s";
    private static final String CENTER_QUERYA = "http://dtm-bankcenter/bank-center/queryAById?id=%s";
    private static final String CENTER_QUERYB = "http://dtm-bankcenter/bank-center/queryBById?id=%s";

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(TransferService.class);

    private BankAService bankAService;

    private BankBService bankBService;

    private BufferedReader console = new BufferedReader(new InputStreamReader(System.in));

    @Autowired
    private RestTemplate restTemplate;

    public TransferService(BankAService bankAService, BankBService bankBService) {
        this.bankAService = bankAService;
        this.bankBService = bankBService;
    }

    /**
     * 微服务调用bankA转入、bankB转出
     */
    public void doExecuteMicro(List<Integer> userIds) throws Exception {
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
                        restTemplate.getForObject(String.format(CENTER_TRANSFER, userIds.get((count * txNum + j) % ACCOUNT), TRAN_MONEY, errRate), String.class);
                        Thread.sleep(100);
                    } catch (Exception e) {
                        // ignore
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
        restTemplate.getForObject(String.format(CENTER_INIT, ACCOUNT, INIT_MONEY), String.class);
        LOGGER.info("Init bankA bankB success");
    }

    /**
     * 查询 Bank A 和 Bank B 余额
     */
    public void queryBankMoney() {
        CmdUtils.println("|--- userId ---|--- bankA-money ---|--- bankB-money ---|---- sum ----|");
        for (int i = 0; i < ACCOUNT; i++) {
            long bankA = restTemplate.getForObject(String.format(CENTER_QUERYA, i), Long.class);
            long bankB = restTemplate.getForObject(String.format(CENTER_QUERYB, i), Long.class);
            long total = bankA + bankB;
            if (total != INIT_MONEY * 2) {
                CmdUtils.println("[ERROR] user id： %s, bankA: %s, bankB: %s, total: %s",
                        i + "", bankA + "", bankB + "", total + "");
            } else {
                CmdUtils.println(PRINT_TMPL, i + "", bankA + "", bankB + "", total + "");
            }
        }
        long totalA = bankAService.querySumMoney();
        long totalB = bankBService.querySumMoney();
        long total = totalA + totalB;
        LOGGER.info("Run finish. total a {},total b {},sum {}", totalA, totalB, total);
    }
}
