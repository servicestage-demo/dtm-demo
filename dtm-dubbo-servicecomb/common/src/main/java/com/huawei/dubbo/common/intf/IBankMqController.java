/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021. All rights reserved.
 */

package com.huawei.dubbo.common.intf;

public interface IBankMqController {
    String transfer(int id, int money, int errRate) throws Exception;
}
