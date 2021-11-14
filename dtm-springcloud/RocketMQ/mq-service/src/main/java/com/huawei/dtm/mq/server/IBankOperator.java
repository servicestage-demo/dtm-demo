/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021. All rights reserved.
 */

package com.huawei.dtm.mq.server;

public interface IBankOperator {
    /**
     * 调用银行开始转账
     * @param userId 用户ID
     * @param money 转账金额
     * @param errRate 失败率
     */
    String transfer(int userId, int money, int errRate) throws Exception;
}
