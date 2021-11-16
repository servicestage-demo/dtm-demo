package com.huawei.mq.service.controller;

import com.huawei.middleware.dtm.client.annotations.DTMTxBegin;
import com.huawei.mq.service.model.RocketMqTemplate;

import org.apache.servicecomb.provider.springmvc.reference.RestTemplateBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping(value = "bank-mq")
public class MqController {
    private static final Logger LOGGER = LoggerFactory.getLogger(MqController.class);

    private static final String BANKA_TRANSFER = "cse://dtm-banka/bank-a/transfer?id=%s&money=%s&errRate=%s";

    private static final String BANKB_TRANSFER = "cse://dtm-bankb/bank-b/transfer?id=%s&money=%s&errRate=%s";
    private final RestTemplate restInvoker = RestTemplateBuilder.create();

    @Autowired
    private RocketMqTemplate rocketMqTemplate;

    /**
     * 调用 bankA和bankB执行转账业务
     *
     * @param id      账号
     * @param money   钱数
     * @param errRate 异常概率
     */
    @GetMapping(value = "transfer")
    @DTMTxBegin(appName = "noninvasive-transfer-mq-ServiceComb")
    public String transfer(@RequestParam(value = "id") int id, @RequestParam(value = "money") int money,
        @RequestParam(value = "errRate") int errRate) throws Exception {
        LOGGER.info("mq service start invoke bankA and bankB");
        restInvoker.getForObject(String.format(BANKA_TRANSFER, id, money * 2, errRate), String.class);
        rocketMqTemplate.sendMsg(id, money);
        restInvoker.getForObject(String.format(BANKB_TRANSFER, id, money, errRate), String.class);
        return "ok";
    }

}
