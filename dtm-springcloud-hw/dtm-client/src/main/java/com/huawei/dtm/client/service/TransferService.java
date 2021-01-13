package com.huawei.dtm.client.service;

import com.huawei.common.impl.BankAService;
import com.huawei.common.impl.BankBService;
import com.huawei.common.util.ExceptionUtils;
import com.huawei.dtm.client.utils.CmdUtils;
import com.huawei.middleware.dtm.client.annotations.DTMTxBegin;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class TransferService {
    private static final String PRINT_TMPL = "|%14s|%19s|%19s|%13s|";

    private static final int ACCOUNT = 10;

    public static final int INIT_MONEY = 1000000;

    private BankAService bankAService;

    private BankBService bankBService;

    public TransferService(BankAService bankAService, BankBService bankBService) {
        this.bankAService = bankAService;
        this.bankBService = bankBService;
    }

    @DTMTxBegin(appName = "transfer-local")
    public void transferLocal(int userId, int money) {
        bankAService.transferIn(userId, money);
        ExceptionUtils.addRuntimeException(50);
        bankBService.transferOut(userId, money);
    }

    @DTMTxBegin(appName = "transfer-tcc-success")
    public void transferTccLocalSuccess() {
        bankAService.tryTransferIn();
        bankBService.tryTransferOut();
    }

    @DTMTxBegin(appName = "transfer-tcc-fail")
    public void transferTccLocalFail() {
        bankAService.tryTransferIn();
        bankBService.tryTransferOut();
        ExceptionUtils.addRuntimeException(100);
    }

    public void initBankAccount() {
        List<Integer> userIds = new ArrayList<>();
        for (int i = 0; i < ACCOUNT; i++) {
            userIds.add(i);
        }
        bankAService.initUserAccount(userIds, INIT_MONEY);
        bankBService.initUserAccount(userIds, INIT_MONEY);
        CmdUtils.println("Init bankA initB success");
    }

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
    }
}
