/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021. All rights reserved.
 */

package com.huawei.dtm.kafka.server.impl;

import com.huawei.dtm.kafka.server.IBankOperator;
import com.huawei.dtm.kafka.server.model.KafkaTemplate;

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

    private final KafkaTemplate kafkaTemplate;

    public RestOpImpl(RestTemplate restTemplate, KafkaTemplate kafkaTemplate) {
        this.restTemplate = restTemplate;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public String transfer(int userId, int money, int errRate) throws Exception {
        LOGGER.info("Start transfer---rest");
        restTemplate.getForObject(String.format(BANKA_TRANSFER, userId, money * 2, errRate), String.class);
        kafkaTemplate.sendMsg(userId, money-50);
        restTemplate.getForObject(String.format(BANKB_TRANSFER, userId, money+50, errRate), String.class);
        return "ok";
    }

}
