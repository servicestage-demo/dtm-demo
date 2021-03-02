package com.huawei.banka.controller;

import com.huawei.common.impl.BankAService;
import com.huawei.middleware.dtm.client.annotations.DTMTxBegin;
import com.huawei.middleware.dtm.client.context.DTMContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RefreshScope
@RequestMapping(value = "bank-a")
public class BankAController {
    private static final Logger LOGGER = LoggerFactory.getLogger(BankAController.class);

    private static final String BANKB_URL_PATH = "http://dtm-bankb/bank-b/transfer?id=%s&money=%s";

    @Autowired
    private BankAService bankAService;

    @Autowired
    private RestTemplate restTemplate;
    /**
     * bankA转入 同时调用 bankB转出
     * @param id 账号
     * @param money 钱数
     */
    @GetMapping(value = "transfer")
    @DTMTxBegin(appName = "noninvasive-transfer")
    public String transfer(@RequestParam(value = "id") int id, @RequestParam(value = "money") int money) {
        LOGGER.info("global tx id:{}, transfer in", DTMContext.getDTMContext().getGlobalTxId());
        bankAService.transferIn(id, money);
        restTemplate.getForObject(String.format(BANKB_URL_PATH, id, money), String.class);
        return "ok";
    }
}
