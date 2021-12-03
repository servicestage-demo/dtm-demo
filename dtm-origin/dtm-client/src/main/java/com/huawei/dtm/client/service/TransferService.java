package com.huawei.dtm.client.service;

import com.huawei.common.impl.BankAService;
import com.huawei.common.impl.BankBService;
import com.huawei.common.util.ExceptionUtils;
import com.huawei.dtm.client.utils.CmdUtils;
import com.huawei.middleware.dtm.client.annotations.DTMTxBegin;

import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
public class TransferService {
    private static final String PRINT_TMPL = "|%14s|%19s|%19s|%13s|";

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(TransferService.class);

    private static final int ACCOUNT = 500;

    public static final int INIT_MONEY = 1000000;

    public static final int TRAN_MONEY = 100;

    private BankAService bankAService;

    private BankBService bankBService;

    public TransferService(BankAService bankAService, BankBService bankBService) {
        this.bankAService = bankAService;
        this.bankBService = bankBService;
    }

    /**
     * 非侵入用例 -> 使用DTM事务 本地场景验证
     */
    @DTMTxBegin(appName = "transfer-local")
    public void transferLocal(int userId, int money, int errRate) {
        bankAService.transferIn(userId, money);
        Thread.sleep(200);
        ExceptionUtils.addRuntimeException(errRate);
        bankBService.transferOut(userId, money);
    }

    /**
     * 非侵入用例 -> 不使用DTM事务 本地场景验证
     */
    public void transferLocalUnable(int userId, int money, int errRate) {
        bankAService.transferIn(userId, money);
        ExceptionUtils.addRuntimeException(errRate);
        bankBService.transferOut(userId, money);
    }

    /**
     * TCC 用例 -> 使用DTM事务验证成功场景
     */
    @DTMTxBegin(appName = "transfer-tcc-success")
    public void transferTccLocal() {
        CmdUtils.println("是否模拟发生异常情况: 输入 0 无异常  输入 1 发生异常");
        int exception = CmdUtils.readCmd(2);
        int userId = new Random().nextInt(ACCOUNT);
        bankAService.tryTransferIn(userId, TRAN_MONEY);
        bankBService.tryTransferOut(userId, TRAN_MONEY);
        if (exception == 1) {
            ExceptionUtils.addRuntimeException(100);
        }
    }

    /**
     * 初始化数据库
     */
    public void initBankAccount() {
        List<Integer> userIds = new ArrayList<>();
        for (int i = 0; i < ACCOUNT; i++) {
            userIds.add(i);
        }
        bankAService.initUserAccount(userIds, INIT_MONEY);
        bankBService.initUserAccount(userIds, INIT_MONEY);
        LOGGER.info("Init bankA initB success");
    }

    /**
     * 查询 Bank A 和 Bank B 余额
     */
    public void queryBankMoney() {
        CmdUtils.println("|--- userId ---|--- bankA-money ---|--- bankB-money ---|---- sum ----|");
        for (int i = 0; i < ACCOUNT; i++) {
            long bankA = bankAService.queryMoneyById(i);
            long bankB = bankBService.queryMoneyById(i);
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
