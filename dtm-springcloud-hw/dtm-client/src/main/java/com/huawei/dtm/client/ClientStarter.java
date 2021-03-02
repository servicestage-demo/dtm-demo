package com.huawei.dtm.client;

import com.huawei.dtm.client.service.TransferService;
import com.huawei.dtm.client.utils.CmdUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
/**
 * 程序的入口，根据输入分别去调用不同的场景用例
 */
@Component
public class ClientStarter implements ApplicationRunner {
    private static final String BANKA_PATH = "http://dtm-banka/bank-a/transfer?id=%s&money=%s";

    @Autowired
    private TransferService transferService;

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        Thread.sleep(8000);
        while (true) {
            try {
                Arrays.stream(MenuOpEnum.values()).forEach(CmdUtils::println);
                CmdUtils.println("请输入命令执行操作：");
                int cmd = CmdUtils.readCmd(8);
                switch (MenuOpEnum.values()[cmd]) {
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
                        doExecuteMicro();
                        break;
                }
            } catch (Throwable throwable) {
                // ignore
            }
        }
    }

    private void doExecuteMicro() throws Exception {
        int threadNum = 10;
        int txNum = 40;
        Random random = new Random();
        CountDownLatch countDownLatch = new CountDownLatch(threadNum);
        long beforeTime = System.currentTimeMillis();
        for (int i = 0; i < threadNum; i++) {
            new Thread(() -> {
                for (int j = 0; j < txNum; j++) {
                    int money = 100;
                    try {
                        restTemplate.getForObject(String.format(BANKA_PATH, random.nextInt() % 10, money), String.class);
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
        transferService.queryBankMoney();
    }

    private void doExecuteLocal() throws Exception {
        int threadNum = 20;
        int txNum = 50;
        Random random = new Random();
        CountDownLatch countDownLatch = new CountDownLatch(threadNum);
        long beforeTime = System.currentTimeMillis();
        for (int i = 0; i < threadNum; i++) {
            new Thread(() -> {
                for (int j = 0; j < txNum; j++) {
                    int money = 100;
                    try {
                        transferService.transferLocal(random.nextInt() % 10, money);
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
        transferService.queryBankMoney();
    }
}
