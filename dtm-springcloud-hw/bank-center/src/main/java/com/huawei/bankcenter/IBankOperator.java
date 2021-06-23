/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021. All rights reserved.
 */

package com.huawei.bankcenter;

public interface IBankOperator {
    /**
     * 调用银行开始转账
     * @param userId 用户ID
     * @param money 转账金额
     * @param errRate 失败率
     */
    String transfer(int userId, int money, int errRate);

    String transferTcc(int userId, int money);

    void init(int userId, int money);

    long queryById(boolean isBankA, int userId);
}
