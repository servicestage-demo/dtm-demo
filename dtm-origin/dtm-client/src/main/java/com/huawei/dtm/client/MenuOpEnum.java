package com.huawei.dtm.client;

public enum MenuOpEnum {
    DTM_INIT_DB("初始化数据库， 重置账号资金"),
    DTM_QUERY_ACCOUNT("查询 Bank A 和 Bank B 余额"),
    DTM_TRANSFER_LOCAL_UNABLE("非侵入用例 -> 不使用DTM事务 本地场景验证"),
    DTM_TRANSFER_LOCAL("非侵入用例 -> DTM 事务 本地场景验证"),
    DTM_TCC_TRANSFER_LOCAL_SUCCESS_UNABLE("TCC 用例 -> 不使用DTM事务 本地场景验证成功场景"),
    DTM_TCC_TRANSFER_LOCAL_SUCCESS("TCC 用例 -> DTM 事务 本地场景验证成功场景"),
    DTM_TCC_TRANSFER_LOCAL_FAIL_UNABLE("TCC 用例 -> 不使用DTM事务 本地场景验证失败场景"),
    DTM_TCC_TRANSFER_LOCAL_FAIL("TCC 用例 -> DTM 事务 本地场景验证失败场景"),
    EXIT("EXIT");

    private String des;

    MenuOpEnum(String des) {
        this.des = des;
    }


    @Override
    public String toString() {
        return String.format("[%s] %s;", ordinal(), this.des);
    }

    public String getDes() {
        return des;
    }
}
