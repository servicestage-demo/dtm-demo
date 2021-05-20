package com.huawei.bankcenter.controller;

import com.huawei.bankcenter.service.BankAIntf;
import com.huawei.bankcenter.service.BankBIntf;
import com.huawei.middleware.dtm.client.annotations.DTMTxBegin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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

    private static final String BANKA_TCC = "http://DTM-BANKA/bank-a/transferTcc";

    private static final String BANKA_TRANSFER = "http://DTM-BANKA/bank-a/transfer?id=%s&money=%s&errRate=%s";

    private static final String BANKA_INIT = "http://DTM-BANKA/bank-a/init?userId=%s&money=%s";

    private static final String BANKA_QUERY = "http://DTM-BANKA/bank-a/query?id=%s";

    private static final String BANKB_TCC = "http://DTM-BANKB/bank-b/transferTcc";

    private static final String BANKB_TRANSFER = "http://DTM-BANKB/bank-b/transfer?id=%s&money=%s&errRate=%s";

    private static final String BANKB_INIT = "http://DTM-BANKB/bank-b/init?userId=%s&money=%s";

    private static final String BANKB_QUERY = "http://DTM-BANKB/bank-b/query?id=%s";

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private BankAIntf bankAIntf;

    @Autowired
    private BankBIntf bankBIntf;

    @Autowired
    @Qualifier("use-feign")
    private boolean useFeign;

    /**
     * 调用 bankA和bankB执行转账业务
     *
     * @param id      账号
     * @param money   钱数
     * @param errRate 异常概率
     */
    @GetMapping(value = "transfer")
    @DTMTxBegin(appName = "noninvasive-transfer")
    public String transfer(@RequestParam(value = "id") int id, @RequestParam(value = "money") int money,
        @RequestParam(value = "errRate") int errRate) {
        LOGGER.info("Bank-center start invoke bankA and bankB");
        if (!useFeign) {
            LOGGER.info("Start transfer---rest");
            restTemplate.getForObject(String.format(BANKA_TRANSFER, id, money, errRate), String.class);
            restTemplate.getForObject(String.format(BANKB_TRANSFER, id, money, errRate), String.class);
        } else {
            LOGGER.info("Start transfer---feign");
            bankAIntf.transfer(id, money, errRate);
            bankBIntf.transfer(id, money, errRate);
        }
        return "ok";
    }

    /**
     * TCC 模式调用 bankA和bankB
     */
    @GetMapping(value = "transferTcc")
    @DTMTxBegin(appName = "tcc-transfer")
    public String transferTcc(@RequestParam(value = "exception") int exception) {
        LOGGER.info("Bank-center start invoke bankA and bankB by tcc_mode");
        if (!useFeign) {
            LOGGER.info("Start tcc---rest");
            restTemplate.getForObject(String.format(BANKA_TCC), String.class);
            restTemplate.getForObject(String.format(BANKB_TCC), String.class);
            if (exception == 1) {
                throw new RuntimeException("Add a potential exception");
            }
        } else {
            LOGGER.info("Start tcc---feign");
            bankAIntf.transferTcc();
            bankBIntf.transferTcc();
            if (exception == 1) {
                throw new RuntimeException("Add a potential exception");
            }
        }
        return "ok";
    }

    /**
     * 初始化 bankA和bankB数据库
     *
     * @param userId 账号
     * @param money  钱数
     */
    @GetMapping(value = "init")
    public String init(@RequestParam(value = "userId") int userId, @RequestParam(value = "money") int money) {
        LOGGER.info("Bank-center init bankA and bankB---{}", System.currentTimeMillis());
        if (useFeign) {
            LOGGER.info("Start init---feign");
            bankBIntf.init(userId, money);
            bankAIntf.init(userId, money);
        } else {
            LOGGER.info("Start init---rest");
            restTemplate.getForObject(String.format(BANKB_INIT, userId, money), String.class);
            restTemplate.getForObject(String.format(BANKA_INIT, userId, money), String.class);
        }
        return "ok";
    }

    /**
     * 查询 bankA 余额
     *
     * @param id 账号
     */
    @GetMapping(value = "queryAById")
    public long queryAById(@RequestParam(value = "id") int id) {
        LOGGER.info("Bank-center start query bankA and bankB");
        if (useFeign) {
            LOGGER.info("Start queryAById---feign");
            return bankAIntf.query(id);
        } else {
            LOGGER.info("Start queryAById---rest");
            return restTemplate.getForObject(String.format(BANKA_QUERY, id), Long.class);
        }
    }

    /**
     * 查询 bankB 余额
     *
     * @param id 账号
     */
    @GetMapping(value = "queryBById")
    public long queryBById(@RequestParam(value = "id") int id) {
        LOGGER.info("Bank-center start query bankA and bankB");
        if (useFeign) {
            LOGGER.info("Start queryBById---feign");
            return bankBIntf.query(id);
        } else {
            LOGGER.info("Start queryBById---rest");
            return restTemplate.getForObject(String.format(BANKB_QUERY, id), Long.class);
        }
    }
}
