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

public class BankAService extends AbsBankService {
    private static final Logger LOGGER = LoggerFactory.getLogger(BankAService.class);

    private static Map<Long, Transfer> transferMap = new ConcurrentHashMap<>();

    private DataSourceTransactionManager dataSourceTransactionManager;

    public BankAService(JdbcTemplate jdbcTemplate, DataSource dataSource) {
        super(jdbcTemplate, dataSource);
        dataSourceTransactionManager = new DataSourceTransactionManager(dataSource);
    }

    public void transferIn(int id, int money) {
        LOGGER.info("BankA transfer in");
        jdbcTemplate.update(DtmConst.TransferSql.TRANSFER_IN_SQL, money, id);
    }

    public boolean tryTransferIn(int userId, int money) {
        LOGGER.info("BankA try transfer in");
        try {
            DTMContext dtmContext = DTMContext.getDTMContext();
            // 记录账户操作流水
            jdbcTemplate.update(DtmConst.TransferSql.TRANSFER_INSERT_TRANSACTION, dtmContext.getBranchTxId(), userId,
                money, "add");
            transferMap.put(dtmContext.getBranchTxId(), new Transfer(userId, money));
        } catch (Exception e) {
            System.out.print("first prepare:" + e);
            return false;
        }
        return true;
    }

    public void confirm() {
        DTMContext dtmContext = DTMContext.getDTMContext();
        LOGGER.info("Tcc global-tx-id: {}, branch-tx-id: {} confirm transfer in", dtmContext.getGlobalTxId(),
            dtmContext.getBranchTxId());
        // 找到账户操作流水 得到修改的帐户和钱数
        AccountTransaction accountTransaction = null;
        Transfer transfer = transferMap.remove(dtmContext.getBranchTxId());
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

    public void cancel() {
        DTMContext dtmContext = DTMContext.getDTMContext();
        LOGGER.info("Tcc global-tx-id: {}, branch-tx-id: {} cancel transfer in", dtmContext.getGlobalTxId(),
            dtmContext.getBranchTxId());
        transferMap.remove(dtmContext.getBranchTxId());
        try {
            // 删除流水
            jdbcTemplate.update("delete from account_transaction where tx_id ='" + dtmContext.getBranchTxId() + "'");
        } catch (Exception e) {
            LOGGER.error("cancel failed with exception:" + e);
            throw e;
        }
    }
}
