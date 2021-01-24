/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020. All rights reserved.
 */

package com.huawei.bankb.controller;

import com.huawei.common.BankService;
import com.huawei.middleware.dtm.client.annotations.DTMTxBegin;

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

    public BankBController(BankService bankBService) {
        this.bankBService = bankBService;
    }

    @DTMTxBegin(appName = "noninvasive-cse-dtmProviderB-transferIn")
    @GetMapping(value = "/transfer")
    public void dbtransfer(@RequestParam(name = "transferMoney") int transferMoney, @RequestParam(name = "id") int id) {
        String dtmProviderC = "bank-c";
        bankBService.transferIn(id, transferMoney);  //DtmProviderA
        restInvoker.getForObject(String.format(lbMvcDtmTransferPath, dtmProviderC, transferMoney, id),
            String.class);
    }
}
