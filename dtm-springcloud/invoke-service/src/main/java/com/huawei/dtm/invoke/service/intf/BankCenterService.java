/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020. All rights reserved.
 */

package com.huawei.dtm.invoke.service.intf;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "DTM-BANKCENTER")
@RequestMapping(value = "bank-center")
public interface BankCenterService {

    @GetMapping(value = "init")
    String init(@RequestParam(value = "userId") int userId, @RequestParam(value = "money") int money);

    @GetMapping(value = "queryAById")
    long queryAById(@RequestParam(value = "id") int id);

    @GetMapping(value = "queryBById")
    long queryBById(@RequestParam(value = "id") int id);

    @GetMapping(value = "transfer")
    String transfer(@RequestParam(value = "id") int id, @RequestParam(value = "money") int money,
        @RequestParam(value = "errRate") int errRate);

    @GetMapping(value = "transferTcc")
    String transferTcc(@RequestParam(value = "exception") int exception);
}
