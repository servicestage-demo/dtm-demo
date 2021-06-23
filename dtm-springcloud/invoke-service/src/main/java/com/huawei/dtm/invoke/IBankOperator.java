/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021. All rights reserved.
 */

package com.huawei.dtm.invoke;

public interface IBankOperator {
    /**
     * 初始化银行
     * @param accountNum 银行账号数量
     * @param initMoney 每个银行初始化金额
     */
    void initBank(int accountNum, int initMoney);

    /**
     * 银行转账
     * @param userId 用户id
     * @param transferMoney 转账金额
     * @param successTransfer 成功转账
     */
    void transferTcc(int userId, int transferMoney, boolean successTransfer);

    /**
     * 查询银行余额
     * @param isBankA 是否是 bankA
     * @param userId 查询余额的用户ID
     * @return 用户的账户余额
     */
    long queryBankMoney(boolean isBankA, int userId);

    void transferMq(int errRate, int transferMoney, int userId);

    String microTransfer(int errRate, int transferMoney, int userId);

    String currentMode();
}
