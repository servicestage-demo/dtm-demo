package com.huawei.dtm.invoke;

import com.huawei.dtm.invoke.service.TransferService;
import com.huawei.dtm.invoke.utils.CmdUtils;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 程序的入口，根据输入分别去调用不同的场景用例
 */
@Component
public class InvokeStarter implements ApplicationRunner {
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(InvokeStarter.class);

    private static final int ACCOUNT = 500;

    @Autowired
    private TransferService transferService;

    @Autowired
    @Qualifier("use-feign")
    private boolean useFeign;

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
                CmdUtils.println("请输入命令执行操作：(当前远程调用/%s)", useFeign ? "feign" : "rest");
                int cmd = CmdUtils.readCmd(8);
                switch (MenuOpEnum.values()[cmd]) {
                    case EXIT:
                        System.exit(0);
                        break;
                    case DTM_INIT_DB:
                        transferService.initBankAccount(useFeign);
                        break;
                    case DTM_QUERY_ACCOUNT:
                        transferService.queryBankMoney(useFeign);
                        break;
                    case DTM_TRANSFER_MICRO:
                        transferService.doExecuteMicro(userIds, useFeign);
                        break;
                    case DTM_TCC_MICRO:
                        transferService.transferTcc(useFeign);
                        break;
                }
            } catch (Throwable throwable) {
                // ignore
                LOGGER.error(throwable.getMessage());
            }
        }
    }
}
