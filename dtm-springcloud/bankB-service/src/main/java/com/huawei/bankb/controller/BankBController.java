package com.huawei.bankb.controller;

import com.huawei.common.impl.BankBService;
import com.huawei.common.util.ExceptionUtils;
import com.huawei.middleware.dtm.client.context.DTMContext;
import com.huawei.middleware.dtm.client.tcc.annotations.DTMTccBranch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RefreshScope
@RequestMapping(value = "bank-b")
public class BankBController {
    private static final Logger LOGGER = LoggerFactory.getLogger(BankBController.class);

    @Value("${dtm.sleep:0}")
    private int sleepMs;

    @Autowired
    private BankBService bankBService;

    /**
     * 概率抛出异常 同时 bankB转出
     * @param id 账号
     * @param money 钱数
     */
    @GetMapping(value = "transfer")
    public String transfer(@RequestParam(value = "id") int id, @RequestParam(value = "money") int money, @RequestParam(value = "errRate") int errRate) {
        LOGGER.info("global tx id:{}, transfer out", DTMContext.getDTMContext().getGlobalTxId());
        try {
            Thread.sleep(sleepMs);
        } catch (Throwable e) {
            //ignore
        }
        ExceptionUtils.addRuntimeException(errRate);
        bankBService.transferOut(id, money);
        return "ok";
    }

    /**
     * bankB 转入的TCC实现
     */
    @GetMapping(value = "transferTcc")
    @DTMTccBranch(identifier = "tcc-try-transfer-out", confirmMethod = "confirm", cancelMethod = "cancel")
    public void tryTransferOut() {
        DTMContext dtmContext = DTMContext.getDTMContext();
        LOGGER.info("Tcc global-tx-id : {}, try transfer out", dtmContext.getGlobalTxId());
    }

    public void confirm() {
        DTMContext dtmContext = DTMContext.getDTMContext();
        LOGGER.info("TCC confirm start");
        LOGGER.info("Customer-implemented business methods,Tcc global-tx-id: {}, branch-tx-id: {} confirm transfer out", dtmContext.getGlobalTxId(), dtmContext.getBranchTxId());
        LOGGER.info("TCC confirm end");
    }

    public void cancel() {
        DTMContext dtmContext = DTMContext.getDTMContext();
        LOGGER.info("TCC cancel start");
        LOGGER.info("Customer-implemented business methods,Tcc global-tx-id: {}, branch-tx-id: {} cancel transfer out", dtmContext.getGlobalTxId(), dtmContext.getBranchTxId());
        LOGGER.info("TCC cancel end");
    }

    /**
     * bankB 初始化
     * @param userId 账号
     * @param money 钱数
     */
    @GetMapping(value = "init")
    public String init(@RequestParam(value = "userId") int userId, @RequestParam(value = "money") int money) {
        LOGGER.info("bankB init");
        bankBService.initUserAccount(userId, money);
        return "ok";
    }

    /**
     * bankB 查询
     * @param id 账号
     * @return
     */
    @GetMapping(value = "query")
    public long query(@RequestParam(value = "id") int id) {
        return bankBService.queryMoneyById(id);
    }



    @GetMapping(value = "/sleep")
    public int getSleepMs() {
        return sleepMs;
    }

}
