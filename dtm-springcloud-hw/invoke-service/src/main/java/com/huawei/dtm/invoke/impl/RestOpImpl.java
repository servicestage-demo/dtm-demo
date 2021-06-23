/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021. All rights reserved.
 */

package com.huawei.dtm.invoke.impl;

import com.huawei.dtm.invoke.IBankOperator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@ConditionalOnMissingBean(FeignOpImpl.class)
public class RestOpImpl implements IBankOperator {
    private static final Logger LOGGER = LoggerFactory.getLogger(RestOpImpl.class);

    private static final String CENTER_TRANSFER
        = "http://dtm-bankcenter/bank-center/transfer?id=%s&money=%s&errRate=%s";

    private static final String MQ_TRANSFER
        = "http://dtm-mq/bank-mq/transfer?id=%s&money=%s&errRate=%s";

    private static final String CENTER_TCC
        = "http://dtm-bankcenter/bank-center/transferTcc?id=%s&money=%s&exception=%s";

    private static final String CENTER_INIT = "http://dtm-bankcenter/bank-center/init?userId=%s&money=%s";

    private static final String CENTER_QUERYA_BY_ID = "http://dtm-bankcenter/bank-center/queryAById?id=%s";

    private static final String CENTER_QUERYB_BY_ID = "http://dtm-bankcenter/bank-center/queryBById?id=%s";

    private final RestTemplate restTemplate;

    public RestOpImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public void initBank(int accountNum, int initMoney) {
        for (int i = 0; i < accountNum; i++) {
            restTemplate.getForObject(String.format(CENTER_INIT, i, initMoney), String.class);
        }
        LOGGER.info("Use rest invoke mode init bankA and bankB success");
    }

    @Override
    public void transferTcc(int userId, int transferMoney, boolean successTransfer) {
        restTemplate.getForObject(String.format(CENTER_TCC, userId, transferMoney, successTransfer), String.class);
        LOGGER.info("Use rest invoke mode start transfer tcc");
    }

    @Override
    public long queryBankMoney(boolean isBankA, int userId) {
        String queryUrl = isBankA ? CENTER_QUERYA_BY_ID : CENTER_QUERYB_BY_ID;
        return restTemplate.getForObject(String.format(queryUrl, userId), Long.class);
    }

    @Override
    public void transferMq(int errRate, int transferMoney, int userId) {
        restTemplate.getForObject(String.format(MQ_TRANSFER, userId, transferMoney, errRate), String.class);
    }

    @Override
    public String microTransfer(int errRate, int transferMoney, int userId) {
        return restTemplate.getForObject(String.format(CENTER_TRANSFER, userId, transferMoney, errRate), String.class);
    }

    @Override
    public String currentMode() {
        return "rest";
    }
}
