package com.huawei.dubbo.common.impl;

import com.huawei.dubbo.common.AbsBankService;
import com.huawei.dubbo.common.util.AccountTransaction;
import com.huawei.dubbo.common.util.AccountTransactionMapper;
import com.huawei.dubbo.common.util.DtmConst;
import com.huawei.middleware.dtm.client.context.DTMContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.sql.DataSource;

public class BankBService extends AbsBankService {
    private static final Logger LOGGER = LoggerFactory.getLogger(BankBService.class);
    private static Map<Long, Transfer> transferMap = new ConcurrentHashMap<>();
    private DataSourceTransactionManager dataSourceTransactionManager;

    public BankBService(JdbcTemplate jdbcTemplate, DataSource dataSource) {
        super(jdbcTemplate, dataSource);
        dataSourceTransactionManager = new DataSourceTransactionManager(dataSource);
    }

    public void transferOut(int id, int money) {
        LOGGER.info("BankB transfer out");
        jdbcTemplate.update(DtmConst.TransferSql.TRANSFER_OUT_SQL, money, id);
    }

    public void tryTransferOut(int userId, int money) {
        LOGGER.info("BankB try transfer Out");
        TransactionDefinition txDef = new DefaultTransactionDefinition();
        TransactionStatus txStatus = null;
        try {
            DTMContext dtmContext = DTMContext.getDTMContext();
            // 记录账户操作流水
            txStatus = dataSourceTransactionManager.getTransaction(txDef);
            jdbcTemplate.update(DtmConst.TransferSql.TRANSFER_INSERT_TRANSACTION, dtmContext.getBranchTxId(), userId,
                money, "minus");
            int affectedRow = jdbcTemplate.update(DtmConst.TransferSql.TRANSFER_OUT_SQL, money, userId);
            if (affectedRow == 0) {
                dataSourceTransactionManager.rollback(txStatus);
                throw new RuntimeException(userId + ":余额不足");
            }
            transferMap.put(dtmContext.getBranchTxId(), new Transfer(userId, money));
            dataSourceTransactionManager.commit(txStatus);
        }catch (Exception e){
            System.out.print("second prepare:" + e);
            if (txStatus != null) {
                dataSourceTransactionManager.rollback(txStatus);
            }
        }
    }

    public void confirm() {
        DTMContext dtmContext = DTMContext.getDTMContext();
        LOGGER.info("Tcc global-tx-id: {}, branch-tx-id: {} confirm transfer out", dtmContext.getGlobalTxId(),
            dtmContext.getBranchTxId());
        // 校验账户
        AccountTransaction accountTransaction = null;
        Transfer transfer = transferMap.remove(dtmContext.getBranchTxId());
        try {
            if (transfer == null) {
                try {
                    accountTransaction = jdbcTemplate.queryForObject(
                        "select tx_id, user_id, money, type from account_transaction where tx_id= '"
                            + dtmContext.getBranchTxId() + "';",
                        new AccountTransactionMapper());
                    transfer = new Transfer(accountTransaction.getUserId(), accountTransaction.getMoney());
                } catch (EmptyResultDataAccessException e) {
                    return;
                }
            }
            jdbcTemplate.update("delete from account_transaction where tx_id ='" + dtmContext.getBranchTxId() + "'");
        } catch (Exception e) {
            LOGGER.error("confirm failed with exception:" + e);
            throw e;
        }
    }

    public void cancel() {
        DTMContext dtmContext = DTMContext.getDTMContext();
        LOGGER.info("Tcc global-tx-id: {}, branch-tx-id: {} cancel transfer out", dtmContext.getGlobalTxId(),
            dtmContext.getBranchTxId());
        Transfer transfer = transferMap.remove(dtmContext.getBranchTxId());
        AccountTransaction accountTransaction = null;
        TransactionDefinition txDef = new DefaultTransactionDefinition();
        TransactionStatus txStatus = null;
        try {
            if (transfer == null) {
                try {
                    accountTransaction = jdbcTemplate.queryForObject(
                        "select tx_id, user_id, money, type from account_transaction where tx_id= '"
                            + dtmContext.getBranchTxId() + "';",
                        new AccountTransactionMapper());
                    transfer = new Transfer(accountTransaction.getUserId(), accountTransaction.getMoney());
                } catch (EmptyResultDataAccessException e) {
                    // ignore
                    return;
                }
            }
            txStatus = dataSourceTransactionManager.getTransaction(txDef);
            jdbcTemplate.update(DtmConst.TransferSql.TRANSFER_IN_SQL, transfer.getMoney(),
                transfer.getUserId());
            jdbcTemplate.update("delete from account_transaction where tx_id ='" + dtmContext.getBranchTxId() + "'");
            dataSourceTransactionManager.commit(txStatus);
        } catch (Exception e) {
            if (txStatus != null) {
                dataSourceTransactionManager.rollback(txStatus);
            }
            throw e;
        }
    }
}
