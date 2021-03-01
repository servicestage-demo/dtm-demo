/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020. All rights reserved.
 */

package com.huawei.common;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import java.util.List;

import javax.sql.DataSource;

public class BankService {
    protected JdbcTemplate jdbcTemplate;

    protected DataSource dataSource;

    public BankService(JdbcTemplate jdbcTemplate, DataSource dataSource) {
        this.jdbcTemplate = jdbcTemplate;
        this.dataSource = dataSource;
    }

    public void initAllAccount(List<Integer> userIds, int total) {
        jdbcTemplate.execute(
            "CREATE TABLE IF NOT EXISTS account (id INTEGER,type INTEGER, money INTEGER, primary key (id,type),check "
                + "(money>0));");
        jdbcTemplate.execute("truncate table account;");
        for (int userId : userIds) {
            jdbcTemplate.update("INSERT into account values(?,0,?);", userId, total);
        }
    }

    public long queryAccountMoney(int id) {
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet("select money from account where id=" + id + " for update;");
        if (rowSet.next()) {
            return rowSet.getLong("money");
        }
        return 0;
    }

    public long queryAccountMoneySum() {
        return jdbcTemplate.queryForObject("select sum(money) from account;", Long.class);
    }

    public void transferOut(int id, int money) {
        jdbcTemplate.update("UPDATE account SET money= money - ? WHERE id = ?;", money, id);
    }

    public void transferIn(int id, int money) {
        jdbcTemplate.update("UPDATE account SET money=money+? WHERE id=?;", money, id);
    }
}
