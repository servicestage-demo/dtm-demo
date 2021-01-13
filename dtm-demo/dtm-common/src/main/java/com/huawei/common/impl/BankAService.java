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
        jdbcTemplate.update(DtmConst.TransferSql.TRANSFER_IN_SQL, money, id);
    }

    @DTMTccBranch(identifier = "tcc-try-transfer-in", confirmMethod = "confirm", cancelMethod = "cancel")
    public void tryTransferIn() {
        DTMContext dtmContext = DTMContext.getDTMContext();
        LOGGER.info("Tcc global-tx-id : {}, try transfer in", dtmContext.getGlobalTxId());
    }

    public void confirm() {
        DTMContext dtmContext = DTMContext.getDTMContext();
        LOGGER.info("Tcc global-tx-id: {}, branch-tx-id: {} confirm transfer in", dtmContext.getGlobalTxId(), dtmContext.getBranchTxId());
    }

    public void cancel() {
        DTMContext dtmContext = DTMContext.getDTMContext();
        LOGGER.info("Tcc global-tx-id: {}, branch-tx-id: {} cancel transfer in", dtmContext.getGlobalTxId(), dtmContext.getBranchTxId());
    }

    public void tryTransferInUnable() {
        DTMContext dtmContext = DTMContext.getDTMContext();
        LOGGER.info("Tcc global-tx-id : {}, try transfer in", dtmContext.getGlobalTxId());
    }
}
