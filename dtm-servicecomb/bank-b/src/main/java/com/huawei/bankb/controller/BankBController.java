/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020. All rights reserved.
 */

package com.huawei.bankb.controller;

import com.huawei.common.BankService;
import com.huawei.common.error.ExceptionUtil;
import com.huawei.middleware.dtm.client.annotations.DTMTxBegin;

import com.netflix.config.DynamicIntProperty;
import com.netflix.config.DynamicLongProperty;
import com.netflix.config.DynamicPropertyFactory;

import org.apache.servicecomb.provider.rest.common.RestSchema;
import org.apache.servicecomb.provider.springmvc.reference.RestTemplateBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import javax.ws.rs.core.MediaType;

@Component
@RestSchema(schemaId = "bankb")
@RequestMapping(path = "/bank", produces = MediaType.APPLICATION_JSON)
public class BankBController {
    private final RestTemplate restInvoker = RestTemplateBuilder.create();

    private static final String lbMvcDtmTransferPath = "cse://%s/bank/transfer?transferMoney=%s&id=%s";

    private final BankService bankBService;

    @GetMapping(value = "/err-rate")
    public int getErrRate() {
        return errorRate.get();
    }

    DynamicIntProperty errorRate = DynamicPropertyFactory.getInstance().getIntProperty("dtm.error", 50);

    public BankBController(BankService bankBService) {
        this.bankBService = bankBService;
    }

    @DTMTxBegin(appName = "noninvasive-cse-dtmProviderB-transferIn")
    @GetMapping(value = "/transfer")
    public void dbtransfer(@RequestParam(name = "transferMoney") int transferMoney, @RequestParam(name = "id") int id) {
        ExceptionUtil.addRuntimeException(errorRate.get());
        bankBService.transferOut(id, transferMoney);  //DtmProviderA
    }
}
