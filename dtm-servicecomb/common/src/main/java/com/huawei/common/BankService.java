/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020. All rights reserved.
 */

package com.huawei.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import javax.sql.DataSource;

public class BankService {
    protected JdbcTemplate jdbcTemplate;
    private static final Logger LOGGER = LoggerFactory.getLogger(BankService.class);
    protected DataSource dataSource;

    public BankService(JdbcTemplate jdbcTemplate, DataSource dataSource) {
        this.jdbcTemplate = jdbcTemplate;
        this.dataSource = dataSource;
    }

    public void initAllAccount(int userIds, int total) {
        jdbcTemplate.execute(
            "CREATE TABLE IF NOT EXISTS account (id INTEGER,type INTEGER, money INTEGER, primary key (id,type),check "
                + "(money>0));");
        jdbcTemplate.execute("truncate table account;");
        for (int i = 0 ; i < userIds; i++) {
            jdbcTemplate.update("INSERT into account values(?,0,?);", i, total);
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
        LOGGER.info("BankB transfer out");
        jdbcTemplate.update("UPDATE account SET money= money - ? WHERE id = ?;", money, id);
    }

    public void transferIn(int id, int money) {
        LOGGER.info("BankA transfer in");
        jdbcTemplate.update("UPDATE account SET money=money+? WHERE id=?;", money, id);
    }
}
