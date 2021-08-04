package com.huawei.bankcenter.controller;

import com.huawei.bankcenter.IBankOperator;
import com.huawei.middleware.dtm.client.annotations.DTMTxBegin;

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
@RequestMapping(value = "bank-center")
public class BankCenterController {
    private static final Logger LOGGER = LoggerFactory.getLogger(BankCenterController.class);

    @Autowired
    private IBankOperator bankOperator;

    /**
     * 调用 bankA和bankB执行转账业务
     *
     * @param id      账号
     * @param money   钱数
     * @param errRate 异常概率
     */
    @GetMapping(value = "transfer")
    @DTMTxBegin(appName = "noninvasive-transfer-SpringCloud")
    public String transfer(@RequestParam(value = "id") int id, @RequestParam(value = "money") int money,
        @RequestParam(value = "errRate") int errRate) {
        LOGGER.info("Bank-center start invoke bankA and bankB");
        bankOperator.transfer(id, money, errRate);
        return "ok";
    }

    /**
     * TCC 模式调用 bankA和bankB
     *
     * @param id        id
     * @param money     money
     * @param exception exception
     * @return
     */
    @GetMapping(value = "transferTcc")
    @DTMTxBegin(appName = "tcc-transfer-SpringCloud")
    public String transferTcc(@RequestParam(value = "id") int id, @RequestParam(value = "money") int money,
        @RequestParam(value = "exception") boolean exception) {
        LOGGER.info("Bank-center start invoke bankA and bankB by tcc_mode");
        bankOperator.transferTcc(id, money);
        if (exception) {
            throw new RuntimeException("Add a potential exception");
        }
        return "ok";
    }

    /**
     * 初始化 bankA和bankB 账户表 + 流水表
     *
     * @param userId 账号
     * @param money  钱数
     */
    @GetMapping(value = "init")
    public String init(@RequestParam(value = "userId") int userId, @RequestParam(value = "money") int money) {
        LOGGER.info("Bank-center init bankA and bankB---{}", System.currentTimeMillis());
        bankOperator.init(userId, money);
        return "ok";
    }

    /**
     * 查询 bankA 余额
     *
     * @param id 账号
     */
    @GetMapping(value = "queryAById")
    public long queryAById(@RequestParam(value = "id") int id) {
        LOGGER.info("Bank-center start query bankA and bankB");
        return bankOperator.queryById(true, id);
    }

    /**
     * 查询 bankB 余额
     *
     * @param id 账号
     */
    @GetMapping(value = "queryBById")
    public long queryBById(@RequestParam(value = "id") int id) {
        LOGGER.info("Bank-center start query bankA and bankB");
        return bankOperator.queryById(false, id);
    }
}
