package com.huawei.banka.controller;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.huawei.common.controller.IBankAController;
import com.huawei.common.controller.IBankBController;
import com.huawei.common.impl.BankAService;
import com.huawei.middleware.dtm.client.annotations.DTMTxBegin;
import com.huawei.middleware.dtm.client.context.DTMContext;

@Path("bank-a")
@Produces({MediaType.APPLICATION_JSON})
public class BankAController implements IBankAController {
  private static final Logger LOGGER = LoggerFactory.getLogger(BankAController.class);

  @Autowired
  private BankAService bankAService;

  @Autowired
  private IBankBController bankBController;

  @Override
  @GET
  @Path("/transfer")
  @DTMTxBegin(appName = "noninvasive-transfer")
  public String transfer(@QueryParam(value = "id") int id, @QueryParam(value = "money") int money) {
    LOGGER.info("global tx id:{}, transfer in", DTMContext.getDTMContext().getGlobalTxId());
    bankAService.transferIn(id, money);
    bankBController.transfer(id, money);
    return "ok";
  }
}
