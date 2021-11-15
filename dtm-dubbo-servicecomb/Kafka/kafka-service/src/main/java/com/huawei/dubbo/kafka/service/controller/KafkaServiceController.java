/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021. All rights reserved.
 */

package com.huawei.dubbo.kafka.service.controller;

import com.huawei.dubbo.common.intf.IBankAController;
import com.huawei.dubbo.common.intf.IBankBController;
import com.huawei.dubbo.common.intf.IBankKafkaController;

import com.huawei.dubbo.kafka.service.model.KafkaTemplate;
import com.huawei.middleware.dtm.client.annotations.DTMTxBegin;
import com.huawei.middleware.dtm.client.context.DTMContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class KafkaServiceController implements IBankKafkaController {
    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaServiceController.class);

    @Autowired
    private IBankAController bankAController;

    @Autowired
    private IBankBController bankBController;

    @Autowired
    private KafkaTemplate kafkaTemplate;

    @Override
    @DTMTxBegin(appName = "kafka-transfer-dubbo-servicecomb")
    public String transfer(int id, int money, int errRate) throws Exception {
        LOGGER.info("mq service start invoke bankA and bankB: {}", DTMContext.getDTMContext().getGlobalTxId());
        bankAController.transfer(id, money * 2, errRate);
        kafkaTemplate.sendMsg(id, money - 50);
        bankBController.transfer(id, money + 50, errRate);
        return "ok";
    }
}
