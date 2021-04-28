package com.huawei.banka.controller;

import com.huawei.common.impl.BankAService;
import com.huawei.middleware.dtm.client.context.DTMContext;
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
    public String transfer(@RequestParam(value = "id") int id, @RequestParam(value = "money") int money) {
        LOGGER.info("global tx id:{}, transfer in", DTMContext.getDTMContext().getGlobalTxId());
        bankAService.transferIn(id, money);
        return "ok";
    }

    /**
     * bankA 初始化
     * @param userIds 账号
     * @param money 钱数
     */
    @GetMapping(value = "init")
    public String init(@RequestParam(value = "userIds") int userIds, @RequestParam(value = "money") int money) {
        LOGGER.info("bankA init");
        bankAService.initUserAccount(userIds, money);
        return "ok";
    }

    /**
     * bankA 查询
     * @param id 账号
     * @return
     */
    @GetMapping(value = "queryByID")
    public long queryByID(@RequestParam(value = "id") int id) {
        return bankAService.queryMoneyById(id);
    }

    /**
     * bankA 查询
     * @return
     */
    @GetMapping(value = "query")
    public long query() {
        return bankAService.querySumMoney();
    }
}
