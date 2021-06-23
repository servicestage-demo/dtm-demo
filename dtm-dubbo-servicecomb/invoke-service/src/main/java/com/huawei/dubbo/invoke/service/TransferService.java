package com.huawei.dubbo.invoke.service;

import static com.huawei.dubbo.invoke.InvokeStarter.ACCOUNT;

import com.huawei.dubbo.common.intf.IBankCenterController;

import com.huawei.dubbo.invoke.utils.CmdUtils;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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

    private static final String PRINT_TMPL = "|%14s|%19s|%19s|%13s|";

    private static final String CENTER_TRANSFER
        = "http://DTM-BANKCENTER/bank-center/transfer?id=%s&money=%s&errRate=%s";

    private static final String CENTER_TCC
        = "http://DTM-BANKCENTER/bank-center/transferTcc?id=%s&money=%s&errRate=%s";

    private static final String CENTER_INIT = "http://DTM-BANKCENTER/bank-center/init?userId=%s&money=%s";

    private static final String CENTER_QUERYABYID = "http://DTM-BANKCENTER/bank-center/queryAById?id=%s";

    private static final String CENTER_QUERYBBYID = "http://DTM-BANKCENTER/bank-center/queryBById?id=%s";

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(TransferService.class);

    private BufferedReader console = new BufferedReader(new InputStreamReader(System.in));

    @Autowired
    private IBankCenterController bankCenterController;

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
                        bankCenterController.transfer(userIds.get((count * txNum + j) % ACCOUNT),
                            TRAN_MONEY, errRate);
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
            bankCenterController.init(i, INIT_MONEY);
        }
        LOGGER.info("init bankA and bankB success");
    }

    /**
     * TCC 调用
     */
    public void transferTcc() throws IOException {
        CmdUtils.println("是否模拟发生异常情况: 输入 0 无异常  输入 1 发生异常");
        int exception = CmdUtils.readCmd(2);
        int userId = new Random().nextInt(ACCOUNT);
        bankCenterController.transferTcc(userId, TRAN_MONEY, exception != 0);
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
            long bankA = bankCenterController.queryById(true, i);
            long bankB = bankCenterController.queryById(false, i);
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
