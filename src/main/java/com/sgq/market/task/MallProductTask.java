package com.sgq.market.task;


import com.sgq.market.Att;
import com.sgq.market.entity.ProductOrder;
import com.sgq.market.service.ProductOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Slf4j
@Component
@EnableScheduling
public class MallProductTask {
  @Autowired
  private ProductOrderService mallProductOrderService;
  
  @Scheduled(cron = "*/30 * * * * *")
  public void taskMallProductOrderTimeOut() {


    
    try {
      Calendar cal = Calendar.getInstance();
      cal.setTime(new Date());
      cal.add(Calendar.MINUTE, -10);
      Date time = cal.getTime();
      List<ProductOrder> objs = mallProductOrderService.query().eq(Att.ProductOrder.dealStatus, 2).le(Att.ProductOrder.createTime, time).list();
      for (ProductOrder obj : objs) {
        try {
          mallProductOrderService.taskMallProductOrderTimeOut(obj);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  
}
