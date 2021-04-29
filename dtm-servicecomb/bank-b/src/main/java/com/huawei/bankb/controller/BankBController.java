/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020. All rights reserved.
 */

package com.huawei.bankb.controller;

import com.huawei.common.BankService;
import com.huawei.common.error.ExceptionUtil;

import com.huawei.middleware.dtm.client.context.DTMContext;
import com.netflix.config.DynamicIntProperty;
import com.netflix.config.DynamicPropertyFactory;

import org.apache.servicecomb.provider.rest.common.RestSchema;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.ws.rs.core.MediaType;

@Component
@RestSchema(schemaId = "bankb")
@RequestMapping(path = "/bankb", produces = MediaType.APPLICATION_JSON)
public class BankBController {
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(BankBController.class);

    private final BankService bankBService;

    @GetMapping(value = "/err-rate")
    public int getErrRate() {
        return errorRate.get();
    }

    DynamicIntProperty errorRate = DynamicPropertyFactory.getInstance().getIntProperty("dtm.error", 50);

    public BankBController(BankService bankBService) {
        this.bankBService = bankBService;
    }
    /**
     * 概率抛出异常 同时 bankB转出
     * @param id 账号
     * @param money 钱数
     */
    @GetMapping(value = "transfer")
    public String transfer(@RequestParam(value = "id") int id, @RequestParam(value = "money") int money, @RequestParam(value = "errRate") int errRate) {
        LOGGER.info("global tx id:{}, transfer out", DTMContext.getDTMContext().getGlobalTxId());
        ExceptionUtil.addRuntimeException(errRate);
        bankBService.transferOut(id, money);
        return "ok";
    }

    /**
     * bankB 初始化
     * @param userIds 账号
     * @param money 钱数
     */
    @GetMapping(value = "init")
    public String init(@RequestParam(value = "userIds") int userIds, @RequestParam(value = "money") int money) {
        LOGGER.info("bankB init");
        bankBService.initAllAccount(userIds, money);
        return "ok";
    }

    /**
     * bankB 查询
     * @param id 账号
     * @return
     */
    @GetMapping(value = "queryByID")
    public long queryByID(@RequestParam(value = "id") int id) {
        return bankBService.queryAccountMoney(id);
    }

    /**
     * bankB 查询
     * @return
     */
    @GetMapping(value = "query")
    public long query() {
        return bankBService.queryAccountMoneySum();
    }
}
