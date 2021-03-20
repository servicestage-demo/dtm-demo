package com.huawei.dtm.client;

import com.huawei.common.impl.BankAService;
import com.huawei.dtm.client.service.TransferService;
import com.huawei.dtm.client.utils.CmdUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
/**
 * 程序的入口，根据输入分别去调用不同的场景用例
 */
@Component
public class ClientStarter implements ApplicationRunner {
    @Autowired
    private TransferService transferService;
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(ClientStarter.class);
    private BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
    @Override
    public void run(ApplicationArguments args) throws Exception {
        Thread.sleep(8000);
        while (true) {
            try {
                Arrays.stream(MenuOpEnum.values()).forEach(CmdUtils::println);
                CmdUtils.println("请输入命令执行操作：");
                int cmd = CmdUtils.readCmd(9);
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
                    case DTM_TRANSFER_LOCAL_UNABLE:
                        doExecuteLocalUnable();
                        break;
                    case DTM_TRANSFER_LOCAL:
                        doExecuteLocal();
                        break;
                    case DTM_TCC_TRANSFER_LOCAL_SUCCESS_UNABLE:
                        transferService.transferTccLocalSuccessUnable();
                        break;
                    case DTM_TCC_TRANSFER_LOCAL_SUCCESS:
                        transferService.transferTccLocalSuccess();
                        break;
                    case DTM_TCC_TRANSFER_LOCAL_FAIL_UNABLE:
                        transferService.transferTccLocalFailUnable();
                        break;
                    case DTM_TCC_TRANSFER_LOCAL_FAIL:
                        transferService.transferTccLocalFail();
                        break;
                }
            } catch (Throwable throwable) {
                LOGGER.error(throwable.getMessage());
            }
        }
    }

    private void doExecuteLocal() throws Exception {
        CmdUtils.println("请输入线程数量:单线程事务数量:异常概率");
        String input = console.readLine();
        int threadNum = Integer.parseInt(input.split(":")[0]);
        int txNum = Integer.parseInt(input.split(":")[1]);
        int errRate = Integer.parseInt(input.split(":")[2]);
        if(threadNum < 1 || threadNum > 15){
            throw new IllegalArgumentException("线程数量取值范围为1到15的整数");
        }
        if(txNum < 1 || txNum > 100){
            throw new IllegalArgumentException("单线程事务数量取值范围为1到100的整数");
        }
        if(errRate < 0 || errRate > 100){
            throw new IllegalArgumentException("异常概率取值范围为0到100的整数");
        }
        Random random = new Random();
        CountDownLatch countDownLatch = new CountDownLatch(threadNum);
        long beforeTime = System.currentTimeMillis();
        for (int i = 0; i < threadNum; i++) {
            Thread.sleep(600);
            new Thread(() -> {
                for (int j = 0; j < txNum; j++) {
                    int money = 100;
                    try {
                        transferService.transferLocal(random.nextInt(10), money, errRate);
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
    private void doExecuteLocalUnable() throws Exception {
        CmdUtils.println("请输入线程数量:单线程事务数量:异常概率");
        String input = console.readLine();
        int threadNum = Integer.parseInt(input.split(":")[0]);
        int txNum = Integer.parseInt(input.split(":")[1]);
        int errRate = Integer.parseInt(input.split(":")[2]);
        if(threadNum < 1 || threadNum > 15){
            throw new IllegalArgumentException("线程数量取值范围为1到15的整数");
        }
        if(txNum < 1 || txNum > 100){
            throw new IllegalArgumentException("单线程事务数量取值范围为1到100的整数");
        }
        if(errRate < 0 || errRate > 100){
            throw new IllegalArgumentException("异常概率取值范围为0到100的整数");
        }
        Random random = new Random();
        CountDownLatch countDownLatch = new CountDownLatch(threadNum);
        long beforeTime = System.currentTimeMillis();
        for (int i = 0; i < threadNum; i++) {
            new Thread(() -> {
                for (int j = 0; j < txNum; j++) {
                    int money = 100;
                    try {
                        transferService.transferLocalUnable(random.nextInt(10), money, errRate);
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
