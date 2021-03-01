/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020. All rights reserved.
 */

package com.huawei.banka.controller;

import com.huawei.common.BankService;
import com.huawei.common.util.CmdUtils;
import com.huawei.middleware.dtm.client.annotations.DTMTxBegin;
import com.huawei.middleware.dtm.client.context.DTMContext;

import org.apache.servicecomb.provider.rest.common.RestSchema;
import org.apache.servicecomb.provider.springmvc.reference.RestTemplateBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import javax.ws.rs.core.MediaType;

@Component
@RestSchema(schemaId = "banka")
@RequestMapping(path = "/bank", produces = MediaType.APPLICATION_JSON)
public class BankAController {
    private final RestTemplate restInvoker = RestTemplateBuilder.create();

    private static final String lbMvcDtmTransferPath = "cse://%s/bank/transfer?transferMoney=%s&id=%s";

    private final BankService bankAService;

    public BankAController(BankService bankAService) {
        this.bankAService = bankAService;
    }

    @DTMTxBegin(appName = "noninvasive-cse-dtmProviderA-transferIn")
    @GetMapping(value = "/transfer")
    public void dbtransfer(@RequestParam(name = "transferMoney") int transferMoney, @RequestParam(name = "id") int id) {
        String dtmProviderB = "bank-b";
        bankAService.transferIn(id, transferMoney);  //DtmProviderA
        restInvoker.getForObject(
            String.format(lbMvcDtmTransferPath, dtmProviderB, transferMoney, id),
            String.class);
    }
}
