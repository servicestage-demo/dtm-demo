/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021. All rights reserved.
 */

package com.huawei.mq.service.impl;


import com.huawei.mq.service.IBankOperator;
import com.huawei.mq.service.model.RocketMqTemplate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@ConditionalOnMissingBean(FeignOpImpl.class)
public class RestOpImpl implements IBankOperator {
    private static final Logger LOGGER = LoggerFactory.getLogger(RestOpImpl.class);

    private static final String BANKA_TRANSFER = "http://dtm-banka/bank-a/transfer?id=%s&money=%s&errRate=%s";

    private static final String BANKB_TRANSFER = "http://dtm-bankb/bank-b/transfer?id=%s&money=%s&errRate=%s";

    private final RestTemplate restTemplate;

    private final RocketMqTemplate mqTemplate;

    public RestOpImpl(RestTemplate restTemplate, RocketMqTemplate mqTemplate) {
        this.restTemplate = restTemplate;
        this.mqTemplate = mqTemplate;
    }

    @Override
    public String transfer(int userId, int money, int errRate) throws Exception {
        LOGGER.info("Start transfer---rest");
        restTemplate.getForObject(String.format(BANKA_TRANSFER, userId, money * 2, errRate), String.class);
        mqTemplate.sendMsg(userId, money);
        restTemplate.getForObject(String.format(BANKB_TRANSFER, userId, money, errRate), String.class);
        return "ok";
    }

}
