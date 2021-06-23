/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020. All rights reserved.
 */

package com.huawei.client.service.intf;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping(value = "bank-mq")
public interface MqService {

    @GetMapping(value = "transfer")
    String transfer(@RequestParam(value = "id") int id, @RequestParam(value = "money") int money,
        @RequestParam(value = "errRate") int errRate);
}
