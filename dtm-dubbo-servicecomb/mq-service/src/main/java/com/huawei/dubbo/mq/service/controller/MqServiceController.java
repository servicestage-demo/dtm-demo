/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021. All rights reserved.
 */

package com.huawei.dubbo.mq.service.controller;

import com.huawei.dubbo.common.intf.IBankAController;
import com.huawei.dubbo.common.intf.IBankBController;
import com.huawei.dubbo.common.intf.IBankMqController;
import com.huawei.dubbo.mq.service.model.RocketMqTemplate;
import com.huawei.middleware.dtm.client.annotations.DTMTxBegin;
import com.huawei.middleware.dtm.client.context.DTMContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class MqServiceController implements IBankMqController {
    private static final Logger LOGGER = LoggerFactory.getLogger(MqServiceController.class);

    @Autowired
    private IBankAController bankAController;

    @Autowired
    private IBankBController bankBController;

    @Autowired
    private RocketMqTemplate rocketMqTemplate;

    @Override
    @DTMTxBegin(appName = "mq-transfer-dubbo-servicecomb")
    public String transfer(int id, int money, int errRate) throws Exception {
        LOGGER.info("mq service start invoke bankA and bankB: {}", DTMContext.getDTMContext().getGlobalTxId());
        bankAController.transfer(id, money * 2, errRate);
        rocketMqTemplate.sendMsg(id, money);
        bankBController.transfer(id, money, errRate);
        return "ok";
    }
}
