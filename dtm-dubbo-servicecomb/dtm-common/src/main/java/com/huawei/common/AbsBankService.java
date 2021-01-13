package com.huawei.common;

import com.huawei.common.util.DtmConst;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import javax.sql.DataSource;
import java.util.List;

public abstract class AbsBankService {

    protected JdbcTemplate jdbcTemplate;

    protected DataSource dataSource;

    public AbsBankService(JdbcTemplate jdbcTemplate, DataSource dataSource) {
        this.jdbcTemplate = jdbcTemplate;
        this.dataSource = dataSource;
    }

    public void initUserAccount(List<Integer> userIds, int total) {
        jdbcTemplate.execute(DtmConst.InitSql.CREATE_TABLE_SQL);
        jdbcTemplate.execute(DtmConst.InitSql.TRUNCATE_SQL);
        for (Integer userId : userIds) {
            jdbcTemplate.update(DtmConst.InitSql.INSERT_SQL, userId, total);
        }
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
}
