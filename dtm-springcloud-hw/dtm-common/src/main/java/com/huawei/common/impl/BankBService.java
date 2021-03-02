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
        jdbcTemplate.update(DtmConst.TransferSql.TRANSFER_OUT_SQL, money, id);
    }
    /**
     * bankB 转入的TCC实现
     */
    @DTMTccBranch(identifier = "tcc-try-transfer-out", confirmMethod = "confirm", cancelMethod = "cancel")
    public void tryTransferOut() {
        DTMContext dtmContext = DTMContext.getDTMContext();
        LOGGER.info("Tcc global-tx-id : {}, try transfer out", dtmContext.getGlobalTxId());
    }

    public void confirm() {
        DTMContext dtmContext = DTMContext.getDTMContext();
        LOGGER.info("Tcc global-tx-id: {}, branch-tx-id: {} confirm transfer out", dtmContext.getGlobalTxId(), dtmContext.getBranchTxId());
    }

    public void cancel() {
        DTMContext dtmContext = DTMContext.getDTMContext();
        LOGGER.info("Tcc global-tx-id: {}, branch-tx-id: {} cancel transfer out", dtmContext.getGlobalTxId(), dtmContext.getBranchTxId());
    }
}
