/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021. All rights reserved.
 */

package com.huawei.dubbo.common.intf;

public interface IBankAController {
    String transfer(int id, int money, int errRate);

    void tryTransferIn(int id, int money);

    String init(int userId, int money);

    long query(int id);
}
