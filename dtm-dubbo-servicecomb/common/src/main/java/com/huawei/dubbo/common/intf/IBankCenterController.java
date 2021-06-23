/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021. All rights reserved.
 */

package com.huawei.dubbo.common.intf;

public interface IBankCenterController {
    String transfer(int id, int money, int errRate);

    String transferTcc(int id, int money, boolean exception);

    String init(int userId, int money);

    long queryById(boolean isBankA, int userId);
}
