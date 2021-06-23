package com.huawei.common.util;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class AccountTransactionMapper implements RowMapper<AccountTransaction> {


    @Override
    public AccountTransaction mapRow(ResultSet resultSet, int i) throws SQLException {
        // 获取结果集中的数据
        String txId = resultSet.getString("tx_id");
        int accountNo = resultSet.getInt("user_id");
        int amount = resultSet.getInt("money");
        String type = resultSet.getString("type");
        // 把数据封装成User对象
        AccountTransaction accountTransaction = new AccountTransaction();
        accountTransaction.setTxId(txId);
        accountTransaction.setUserId(accountNo);
        accountTransaction.setMoney(amount);
        accountTransaction.setType(type);
        return accountTransaction;
    }
}
