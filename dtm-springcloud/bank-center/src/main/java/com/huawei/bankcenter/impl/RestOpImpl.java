/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021. All rights reserved.
 */

package com.huawei.bankcenter.impl;

import com.huawei.bankcenter.IBankOperator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@ConditionalOnMissingBean(FeignOpImpl.class)
public class RestOpImpl implements IBankOperator {
    private static final Logger LOGGER = LoggerFactory.getLogger(RestOpImpl.class);

    private static final String BANKA_TCC = "http://DTM-BANKA/bank-a/transferTcc?id=%s&money=%s";

    private static final String BANKB_TCC = "http://DTM-BANKB/bank-b/transferTcc?id=%s&money=%s";

    private static final String BANKA_TRANSFER = "http://DTM-BANKA/bank-a/transfer?id=%s&money=%s&errRate=%s";

    private static final String BANKB_TRANSFER = "http://DTM-BANKB/bank-b/transfer?id=%s&money=%s&errRate=%s";

    private static final String BANKA_INIT = "http://DTM-BANKA/bank-a/init?userId=%s&money=%s";

    private static final String BANKB_INIT = "http://DTM-BANKB/bank-b/init?userId=%s&money=%s";

    private static final String BANKA_QUERY = "http://DTM-BANKA/bank-a/query?id=%s";

    private static final String BANKB_QUERY = "http://DTM-BANKB/bank-b/query?id=%s";

    private final RestTemplate restTemplate;

    public RestOpImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public String transfer(int userId, int money, int errRate) {
        LOGGER.info("Start transfer---rest");
        restTemplate.getForObject(String.format(BANKA_TRANSFER, userId, money, errRate), String.class);
        restTemplate.getForObject(String.format(BANKB_TRANSFER, userId, money, errRate), String.class);
        return "ok";
    }

    @Override
    public String transferTcc(int userId, int money) {
        LOGGER.info("Start tcc---rest");
        restTemplate.getForObject(String.format(BANKA_TCC, userId, money), String.class);
        restTemplate.getForObject(String.format(BANKB_TCC, userId, money), String.class);
        return "ok";
    }

    @Override
    public void init(int userId, int money) {
        LOGGER.info("Start init---rest");
        restTemplate.getForObject(String.format(BANKA_INIT, userId, money), String.class);
        restTemplate.getForObject(String.format(BANKB_INIT, userId, money), String.class);
    }

    @Override
    public long queryById(boolean isBankA, int userId) {
        LOGGER.info("Start queryById---rest");
        if (isBankA) {
            return restTemplate.getForObject(String.format(BANKA_QUERY, userId), Long.class);
        }
        return restTemplate.getForObject(String.format(BANKB_QUERY, userId), Long.class);
    }
}
