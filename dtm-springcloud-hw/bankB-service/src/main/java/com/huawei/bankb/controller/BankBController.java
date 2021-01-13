package com.huawei.bankb.controller;

import com.huawei.common.impl.BankAService;
import com.huawei.common.impl.BankBService;
import com.huawei.common.util.ExceptionUtils;
import com.huawei.middleware.dtm.client.context.DTMContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RefreshScope
@RequestMapping(value = "bank-b")
public class BankBController {
    private static final Logger LOGGER = LoggerFactory.getLogger(BankBController.class);

    @Value("${dtm.err:50}")
    private int errRate;

    @Value("${dtm.sleep:0}")
    private int sleepMs;

    @Autowired
    private BankBService bankBService;

    @GetMapping(value = "transfer")
    public String transfer(@RequestParam(value = "id") int id, @RequestParam(value = "money") int money) {
        LOGGER.info("global tx id:{}, transfer out", DTMContext.getDTMContext().getGlobalTxId());
        try {
            Thread.sleep(sleepMs);
        } catch (Throwable e) {
            //ignore
        }
        ExceptionUtils.addRuntimeException(errRate);
        bankBService.transferOut(id, money);
        return "ok";
    }

    @GetMapping(value = "/sleep")
    public int getSleepMs() {
        return sleepMs;
    }

    @GetMapping(value = "/err-rate")
    public int getErrRate() {
        return errRate;
    }
}
