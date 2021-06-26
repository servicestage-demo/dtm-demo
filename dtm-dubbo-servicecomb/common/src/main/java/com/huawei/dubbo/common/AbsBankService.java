package com.huawei.dubbo.common;

import com.huawei.dubbo.common.util.DtmConst;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import javax.sql.DataSource;

public abstract class AbsBankService {

    protected JdbcTemplate jdbcTemplate;

    protected DataSource dataSource;

    public AbsBankService(JdbcTemplate jdbcTemplate, DataSource dataSource) {
        this.jdbcTemplate = jdbcTemplate;
        this.dataSource = dataSource;
    }

    public void initUserAccount(int userId, int total) {
        if (userId == 0) {
            jdbcTemplate.execute(DtmConst.InitSql.CREATE_TABLE_SQL);
            jdbcTemplate.execute(DtmConst.InitSql.TRUNCATE_SQL);
            jdbcTemplate.execute(DtmConst.InitSql.CREATE_TABLE_TRANSACTION_SQL);
            jdbcTemplate.execute(DtmConst.InitSql.TRUNCATE_TRANSACTION_SQL);
        }
        jdbcTemplate.update(DtmConst.InitSql.INSERT_SQL, userId, total);
    }

    public long queryMoneyById(int id) {
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(String.format(DtmConst.QuerySql.QUERY_USER_BY_ID, id));
        if (rowSet.next()) {
            return rowSet.getLong(DtmConst.Column.MONEY);
        }
        return 0L;
    }

    public long querySumMoney() {
        return jdbcTemplate.queryForObject(DtmConst.QuerySql.QUERY_BANK_SUM_MONEY, Long.class);
    }

    public static class Transfer {
        int userId;
        int money;

        public Transfer(int userId, int money) {
            super();
            this.userId = userId;
            this.money = money;
        }

        public int getUserId() {
            return userId;
        }

        public int getMoney() {
            return money;
        }
    }
}