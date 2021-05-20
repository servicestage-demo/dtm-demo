package com.huawei.common.impl;

import com.huawei.common.AbsBankService;
import com.huawei.common.util.DtmConst;
import com.huawei.middleware.dtm.client.context.DTMContext;
import com.huawei.middleware.dtm.client.tcc.annotations.DTMTccBranch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

public class BankBService extends AbsBankService {
    private static final Logger LOGGER = LoggerFactory.getLogger(BankBService.class);

    public BankBService(JdbcTemplate jdbcTemplate, DataSource dataSource) {
        super(jdbcTemplate, dataSource);
    }

    public void transferOut(int id, int money) {
        LOGGER.info("BankB transfer out");
        jdbcTemplate.update(DtmConst.TransferSql.TRANSFER_OUT_SQL, money, id);
    }
}
