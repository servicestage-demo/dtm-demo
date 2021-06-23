/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020. All rights reserved.
 */

package com.huawei.bankcenter.controller;

import com.huawei.middleware.dtm.client.annotations.DTMTxBegin;
import com.huawei.middleware.dtm.client.context.DTMContext;

import com.netflix.config.DynamicIntProperty;
import com.netflix.config.DynamicPropertyFactory;
import org.apache.servicecomb.provider.rest.common.RestSchema;
import org.apache.servicecomb.provider.springmvc.reference.RestTemplateBuilder;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import javax.ws.rs.core.MediaType;

@Component
@RestSchema(schemaId = "bankcenter")
@RequestMapping(path = "/bank-center", produces = MediaType.APPLICATION_JSON)
public class BankCenterController {

    private final RestTemplate restInvoker = RestTemplateBuilder.create();

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(BankCenterController.class);

    private static final String BANKA_TCC = "cse://dtm-banka/bank-a/transferTcc?id=%s&money=%s";

    private static final String BANKB_TCC = "cse://dtm-bankb/bank-b/transferTcc?id=%s&money=%s";

    private static final String BANKA_TRANSFER = "cse://dtm-banka/bank-a/transfer?id=%s&money=%s&errRate=%s";

    private static final String BANKB_TRANSFER = "cse://dtm-bankb/bank-b/transfer?id=%s&money=%s&errRate=%s";

    private static final String BANKA_INIT = "cse://dtm-banka/bank-a/init?userId=%s&money=%s";

    private static final String BANKB_INIT = "cse://dtm-bankb/bank-b/init?userId=%s&money=%s";

    private static final String BANKA_QUERY = "cse://dtm-banka/bank-a/query?id=%s";

    private static final String BANKB_QUERY = "cse://dtm-bankb/bank-b/query?id=%s";

    @GetMapping(value = "/err-rate")
    public int getErrRate() {
        return errorRate.get();
    }

    DynamicIntProperty errorRate = DynamicPropertyFactory.getInstance().getIntProperty("dtm.error", 50);


    /**
     * 调用 bankA和bankB执行转账业务
     * @param id 账号
     * @param money 钱数
     * @param errRate 异常概率
     */
    @GetMapping(value = "transfer")
    @DTMTxBegin(appName = "noninvasive-transfer")
    public String transfer(@RequestParam(value = "id") int id, @RequestParam(value = "money") int money, @RequestParam(value = "errRate") int errRate) {
        LOGGER.info("Bank-center start invoke bankA and bankB: {}", DTMContext.getDTMContext().getGlobalTxId());
        restInvoker.getForObject(String.format(BANKA_TRANSFER, id, money, errRate), String.class);
        restInvoker.getForObject(String.format(BANKB_TRANSFER, id, money, errRate), String.class);
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
    @DTMTxBegin(appName = "tcc-transfer")
    public String transferTcc(@RequestParam(value = "id") int id, @RequestParam(value = "money") int money,
        @RequestParam(value = "exception") boolean exception) {
        LOGGER.info("Bank-center start invoke bankA and bankB by tcc_mode");
        restInvoker.getForObject(String.format(BANKA_TCC, id, money), String.class);
        restInvoker.getForObject(String.format(BANKB_TCC, id, money), String.class);
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
        restInvoker.getForObject(String.format(BANKA_INIT, userId, money), String.class);
        restInvoker.getForObject(String.format(BANKB_INIT, userId, money), String.class);
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
        return restInvoker.getForObject(String.format(BANKA_QUERY, id), Long.class);
    }

    /**
     * 查询 bankB 余额
     *
     * @param id 账号
     */
    @GetMapping(value = "queryBById")
    public long queryBById(@RequestParam(value = "id") int id) {
        LOGGER.info("Bank-center start query bankA and bankB");
        return restInvoker.getForObject(String.format(BANKB_QUERY, id), Long.class);
    }
}
