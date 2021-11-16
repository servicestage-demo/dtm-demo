package com.huawei.dtm.kafka.server.controller;

import com.huawei.dtm.kafka.server.IBankOperator;
import com.huawei.middleware.dtm.client.annotations.DTMTxBegin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RefreshScope
@RequestMapping(value = "bank-kafka")
public class MqController {
    private static final Logger LOGGER = LoggerFactory.getLogger(MqController.class);

    private final IBankOperator bankOperator;

    public MqController(IBankOperator bankOperator) {
        this.bankOperator = bankOperator;
    }

    /**
     * 调用 bankA 转账 200
     * 消息consumer 调用bankB 转账 50
     * 调用 bankB 转账 150
     *
     * @param id      账号
     * @param money   钱数
     * @param errRate 异常概率
     */
    @GetMapping(value = "transfer")
    @DTMTxBegin(appName = "noninvasive-transfer-kafka-SpringCloudHW")
    public String transfer(@RequestParam(value = "id") int id, @RequestParam(value = "money") int money,
        @RequestParam(value = "errRate") int errRate) throws Exception {
        LOGGER.info("Kafka service start invoke bankA and bankB");
        bankOperator.transfer(id, money, errRate);
        return "ok";
    }

}
