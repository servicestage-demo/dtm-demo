package com.huawei.bankb.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.huawei.common.controller.IBankBController;
import com.huawei.common.impl.BankBService;
import com.huawei.common.util.ExceptionUtils;
import com.huawei.middleware.dtm.client.context.DTMContext;

public class BankBController implements IBankBController {
  private static final Logger LOGGER = LoggerFactory.getLogger(BankBController.class);

  @Value("${dtm.err:50}")
  private int errRate;

  @Value("${dtm.sleep:0}")
  private int sleepMs;

  @Autowired
  private BankBService bankBService;

  @Override
  public String transfer(int id, int money) {
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

  @Override
  public int getSleepMs() {
    return sleepMs;
  }

  @Override
  public int getErrRate() {
    return errRate;
  }
}
