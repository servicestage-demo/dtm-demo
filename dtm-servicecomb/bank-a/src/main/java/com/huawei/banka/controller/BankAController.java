/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020. All rights reserved.
 */

package com.huawei.banka.controller;

import com.huawei.common.BankService;
import com.huawei.middleware.dtm.client.context.DTMContext;

import org.apache.servicecomb.provider.rest.common.RestSchema;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.ws.rs.core.MediaType;

@Component
@RestSchema(schemaId = "banka")
@RequestMapping(path = "/banka", produces = MediaType.APPLICATION_JSON)
public class BankAController {
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(BankAController.class);

    private final BankService bankAService;

    public BankAController(BankService bankAService) {
        this.bankAService = bankAService;
    }

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
        bankAService.initAllAccount(userIds, money);
        return "ok";
    }

    /**
     * bankA 查询
     * @param id 账号
     * @return
     */
    @GetMapping(value = "queryByID")
    public long queryByID(@RequestParam(value = "id") int id) {
        return bankAService.queryAccountMoney(id);
    }

    /**
     * bankA 查询
     * @return
     */
    @GetMapping(value = "query")
    public long query() {
        return bankAService.queryAccountMoneySum();
    }
}
