package com.huawei.bankcenter.controller;

import com.huawei.middleware.dtm.client.annotations.DTMTxBegin;
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
@RequestMapping(value = "bank-center")
public class BankCenterController {
    private static final Logger LOGGER = LoggerFactory.getLogger(BankCenterController.class);

    private static final String BANKA_TRANSFER = "http://dtm-banka/bank-a/transfer?id=%s&money=%s";
    private static final String BANKA_INIT = "http://dtm-banka/bank-a/init?userIds=%s&money=%s";
    private static final String BANKA_QUERYBYID = "http://dtm-banka/bank-a/queryByID?id=%s";
    private static final String BANKA_QUERY = "http://dtm-banka/bank-a/query";

    private static final String BANKB_TRANSFER = "http://dtm-bankb/bank-b/transfer?id=%s&money=%s&errRate=%s";
    private static final String BANKB_INIT = "http://dtm-bankb/bank-b/init?userIds=%s&money=%s";
    private static final String BANKB_QUERYBYID = "http://dtm-bankb/bank-b/queryByID?id=%s";
    private static final String BANKB_QUERY = "http://dtm-bankb/bank-b/query";

    @Autowired
    private RestTemplate restTemplate;

    /**
     * 调用 bankA和bankB执行转账业务
     * @param id 账号
     * @param money 钱数
     * @param errRate 异常概率
     */
    @GetMapping(value = "transfer")
    @DTMTxBegin(appName = "noninvasive-transfer")
    public String transfer(@RequestParam(value = "id") int id, @RequestParam(value = "money") int money, @RequestParam(value = "errRate") int errRate) {
        LOGGER.info("Bank-center start invoke bankA and bankB");
        restTemplate.getForObject(String.format(BANKA_TRANSFER, id, money), String.class);
        restTemplate.getForObject(String.format(BANKB_TRANSFER, id, money, errRate), String.class);
        return "ok";
    }

    /**
     * 初始化 bankA和bankB数据库
     * @param userIds 账号
     * @param money 钱数
     */
    @GetMapping(value = "init")
    public String init(@RequestParam(value = "userIds") int userIds, @RequestParam(value = "money") int money) {
        LOGGER.info("Bank-center init bankA and bankB");
        restTemplate.getForObject(String.format(BANKA_INIT, userIds, money), String.class);
        restTemplate.getForObject(String.format(BANKB_INIT, userIds, money), String.class);
        return "ok";
    }

    /**
     * 根据 id 查询 bankA 余额
     * @param id 账号
     */
    @GetMapping(value = "queryAById")
    public long queryAById(@RequestParam(value = "id") int id) {
        LOGGER.info("Bank-center query bankA id {}", id);
        return restTemplate.getForObject(String.format(BANKA_QUERYBYID, id), Long.class);
    }

    /**
     * 根据 id 查询 bankB 余额
     * @param id 账号
     */
    @GetMapping(value = "queryBById")
    public long queryBById(@RequestParam(value = "id") int id) {
        LOGGER.info("Bank-center query bankB id {}", id);
        return restTemplate.getForObject(String.format(BANKB_QUERYBYID, id), Long.class);
    }

    /**
     * 查询 bankA 余额
     */
    @GetMapping(value = "queryA")
    public long queryA() {
        LOGGER.info("Bank-center query bankA");
        return restTemplate.getForObject(String.format(BANKA_QUERY), Long.class);
    }

    /**
     * 查询 bankB 余额
     */
    @GetMapping(value = "queryB")
    public long queryB() {
        LOGGER.info("Bank-center query bankB");
        return restTemplate.getForObject(String.format(BANKB_QUERY), Long.class);
    }
}
