package com.huawei.banka.controller;

import com.huawei.common.impl.BankAService;
import com.huawei.middleware.dtm.client.context.DTMContext;
import com.huawei.middleware.dtm.client.tcc.annotations.DTMTccBranch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RefreshScope
@RequestMapping(value = "bank-a")
public class BankAController {
    private static final Logger LOGGER = LoggerFactory.getLogger(BankAController.class);

    @Autowired
    private BankAService bankAService;

    /**
     * bankA转入
     * @param id 账号
     * @param money 钱数
     */
    @GetMapping(value = "transfer")
    public String transfer(@RequestParam(value = "id") int id, @RequestParam(value = "money") int money, @RequestParam(value = "errRate") int errRate) {
        LOGGER.info("global tx id:{}, transfer in", DTMContext.getDTMContext().getGlobalTxId());
        bankAService.transferIn(id, money);
        return "ok";
    }

    /**
     * bankA 转入的TCC实现
     */
    @GetMapping(value = "transferTcc")
    @DTMTccBranch(identifier = "tcc-try-transfer-in", confirmMethod = "confirm", cancelMethod = "cancel")
    public void tryTransferIn() {
        DTMContext dtmContext = DTMContext.getDTMContext();
        LOGGER.info("Tcc global-tx-id : {}, try transfer in", dtmContext.getGlobalTxId());
    }

    public void confirm() {
        DTMContext dtmContext = DTMContext.getDTMContext();
        LOGGER.info("TCC confirm start");
        LOGGER.info("Customer-implemented business methods,Tcc global-tx-id: {}, branch-tx-id: {} confirm transfer in", dtmContext.getGlobalTxId(), dtmContext.getBranchTxId());
        LOGGER.info("TCC confirm end");
    }

    public void cancel() {
        DTMContext dtmContext = DTMContext.getDTMContext();
        LOGGER.info("TCC cancel start");
        LOGGER.info("Customer-implemented business methods,Tcc global-tx-id: {}, branch-tx-id: {} cancel transfer in", dtmContext.getGlobalTxId(), dtmContext.getBranchTxId());
        LOGGER.info("TCC cancel end");
    }

    /**
     * bankA 初始化
     * @param userId 账号
     * @param money 钱数
     */
    @GetMapping(value = "init")
    public String init(@RequestParam(value = "userId") int userId, @RequestParam(value = "money") int money) {
        LOGGER.info("bankA init");
        bankAService.initUserAccount(userId, money);
        return "ok";
    }

    /**
     * bankA 查询
     * @param id 账号
     */
    @GetMapping(value = "query")
    public long query(@RequestParam(value = "id") int id) {
        return bankAService.queryMoneyById(id);
    }
}
