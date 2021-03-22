package com.huawei.dtm.client;

import com.huawei.dtm.client.service.TransferService;
import com.huawei.dtm.client.utils.CmdUtils;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * 程序的入口，根据输入分别去调用不同的场景用例
 */
@Component
public class ClientStarter implements ApplicationRunner {
    private static final String BANKA_PATH = "http://dtm-banka/bank-a/transfer?id=%s&money=%s&errRate=%s";

    private BufferedReader console = new BufferedReader(new InputStreamReader(System.in));

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(ClientStarter.class);

    @Autowired
    private TransferService transferService;

    @Autowired
    private RestTemplate restTemplate;

    private static final int ACCOUNT = 500;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        Thread.sleep(8000);
        List<Integer> userIds = new ArrayList<>();
        for (int i = 0; i < ACCOUNT; i++) {
            userIds.add(i);
        }
        Collections.shuffle(userIds);
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
                        doExecuteMicro(userIds);
                        break;
                }
            } catch (Throwable throwable) {
                // ignore
                LOGGER.error(throwable.getMessage());
            }
        }
    }

    /**
     * 微服务调用bankA转入、bankB转出
     */
    private void doExecuteMicro(List<Integer> userIds) throws Exception {
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
                    int money = 100;
                    try {
                        restTemplate.getForObject(
                            String.format(BANKA_PATH, userIds.get((count * txNum + j) % ACCOUNT), money, errRate),
                            String.class);
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
}
