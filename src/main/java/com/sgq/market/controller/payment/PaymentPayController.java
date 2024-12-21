package com.sgq.market.controller.payment;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.sgq.market.model.R;
import com.sgq.market.model.dto.PaymentPayDto;
import com.sgq.market.service.PaymentPayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payment/pay")
@SaCheckLogin
public class PaymentPayController {
  @Autowired
  PaymentPayService paymentPayService;
  @PostMapping
  public R createPaymentPay(@RequestBody PaymentPayDto dto){
    String id = paymentPayService.createPaymentPay(dto.getPaymentOrderId());
    return R.ok(id);
  }
  @PutMapping("/finish/{paymentPayId}")
  public R finishPay(@PathVariable("paymentPayId") String paymentPayId){
    paymentPayService.finishPay(paymentPayId);
    return R.ok();
  }
}
