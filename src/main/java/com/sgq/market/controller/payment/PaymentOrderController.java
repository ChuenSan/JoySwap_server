package com.sgq.market.controller.payment;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.hutool.core.bean.BeanUtil;
import com.sgq.market.constants.ResultCode;
import com.sgq.market.entity.PaymentOrder;
import com.sgq.market.exception.ServiceException;
import com.sgq.market.model.R;
import com.sgq.market.model.dto.PaymentOrderDto;
import com.sgq.market.service.PaymentOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payment/order")
@SaCheckLogin
public class PaymentOrderController {
  @Autowired
  private PaymentOrderService paymentOrderService;
  @PostMapping
  public R<String> createPaymentOrder(@RequestBody PaymentOrderDto dto){
    String id = paymentOrderService.createPaymentOrder(dto.getOrderId());
    return R.ok(id);
  }
  @GetMapping
  public R<PaymentOrder> getPaymentOrder(String paymentOrderId){
    PaymentOrder paymentOrder = paymentOrderService.getById(paymentOrderId);
    if(BeanUtil.isEmpty(paymentOrder)) throw new ServiceException(ResultCode.NotFindError);
    return R.ok(paymentOrder);
  }
}
