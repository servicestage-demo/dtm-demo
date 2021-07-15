/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2019. All rights reserved.
 */
package com.huawei.client;

public enum MenuOperationsEnum {
    DTM_INIT_DB("初始化数据库， 重置账号资金"),
    DTM_QUERY_ACCOUNT("查询 Bank A 和 Bank B 余额"),
    DTM_TRANSFER_MICRO("非侵入用例 -> DTM 事务 微服务场景调用"),
    DTM_TCC_MICRO("TCC用例 -> DTM 事务 微服务场景调用"),
    DTM_MQ_MICRO("DTM对接消息用例 -> DTM 事务 微服务场景调用"),
    EXIT("EXIT");

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
