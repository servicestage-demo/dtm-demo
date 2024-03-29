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

import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

@DubboService(version = "1.0.0", protocol = "dubbo", timeout = 30_000)
public class MqServiceController implements IBankMqController {
    private static final Logger LOGGER = LoggerFactory.getLogger(MqServiceController.class);

    @DubboReference(version = "1.0.0", protocol = "dubbo", timeout = 30000)
    private IBankAController bankAController;

    @DubboReference(version = "1.0.0", protocol = "dubbo", timeout = 30000)
    private IBankBController bankBController;

    @Autowired
    private RocketMqTemplate rocketMqTemplate;

    @Override
    @DTMTxBegin(appName = "mq-transfer-dubbo")
    public String transfer(int id, int money, int errRate) throws Exception {
        LOGGER.info("mq service start invoke bankA and bankB: {}", DTMContext.getDTMContext().getGlobalTxId());
        bankAController.transfer(id, money * 2, errRate);
        rocketMqTemplate.sendMsg(id, money);
        bankBController.transfer(id, money, errRate);
        return "ok";
    }
}
