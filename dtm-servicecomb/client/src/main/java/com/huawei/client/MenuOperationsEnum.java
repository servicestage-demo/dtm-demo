/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2019. All rights reserved.
 */
package com.huawei.client;

public enum MenuOperationsEnum {
    INIT_DB("初始化数据库，重置账号金额"),
    QUERY_ACCOUNT("查询 BankA 和 BankB 账号余额"),
    TRANSFER_DEMO_LOCAL("非侵入用例 -> DTM 事务 本地场景验证"),
    EXIT("退出"),
    ;

    private String description;

    MenuOperationsEnum(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s;", ordinal(), this.description);
    }

    public String getDescription() {
        return description;
    }
}
