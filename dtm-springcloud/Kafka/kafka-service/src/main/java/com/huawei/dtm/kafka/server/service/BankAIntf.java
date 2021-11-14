/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020. All rights reserved.
 */

package com.huawei.dtm.kafka.server.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "DTM-BANKA")
@RequestMapping(value = "bank-a")
public interface BankAIntf {
    @GetMapping(value = "transfer")
    String transfer(@RequestParam(value = "id") int id, @RequestParam(value = "money") int money,
        @RequestParam(value = "errRate") int errRate);

    @GetMapping(value = "transferTcc")
    String transferTcc(@RequestParam(value = "id") int id, @RequestParam(value = "money") int money);

    @GetMapping(value = "init")
    String init(@RequestParam(value = "userId") int userId, @RequestParam(value = "money") int money);

    @GetMapping(value = "query")
    long query(@RequestParam(value = "id") int id);
}
