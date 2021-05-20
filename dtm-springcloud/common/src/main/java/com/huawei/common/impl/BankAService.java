package com.huawei.common.impl;

import com.huawei.common.AbsBankService;
import com.huawei.common.util.DtmConst;
import com.huawei.middleware.dtm.client.context.DTMContext;
import com.huawei.middleware.dtm.client.tcc.annotations.DTMTccBranch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

public class BankAService extends AbsBankService {
    private static final Logger LOGGER = LoggerFactory.getLogger(BankAService.class);

    public BankAService(JdbcTemplate jdbcTemplate, DataSource dataSource) {
        super(jdbcTemplate, dataSource);
    }

    public void transferIn(int id, int money) {
        LOGGER.info("BankA transfer in");
        jdbcTemplate.update(DtmConst.TransferSql.TRANSFER_IN_SQL, money, id);
    }
}
