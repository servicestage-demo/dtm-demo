package com.huawei.dubbo.common.util;

public class AccountTransaction {

    /**
     * 事务id
     */
    private String txId;

    /**
     * 操作账户
     */
    private int userId;

    /**
     * 操作金额
     */
    private int money;

    /**
     * 操作类型，加钱还是减钱
     */
    private String type;

    public String getTxId() {
        return txId;
    }

    public void setTxId(String txId) {
        this.txId = txId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
