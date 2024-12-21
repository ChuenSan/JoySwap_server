package com.sgq.market.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;


import com.sgq.market.constants.ResultCode;
import com.sgq.market.entity.PaymentOrder;
import com.sgq.market.entity.PaymentPay;
import com.sgq.market.entity.PaymentType;
import com.sgq.market.exception.ServiceException;
import com.sgq.market.mapper.PaymentPayMapper;
import com.sgq.market.service.PaymentOrderService;
import com.sgq.market.service.PaymentPayService;
import com.sgq.market.service.PaymentTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@Service
public class PaymentPayServiceImpl extends ServiceImpl<PaymentPayMapper, PaymentPay> implements PaymentPayService {
  @Autowired
  private PaymentOrderService paymentOrderService;
  @Autowired
  private PaymentTypeService paymentTypeService;
  @Override
  @Transactional(rollbackFor = Exception.class)
  public String createPaymentPay(String paymentOrderId) {
    SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
    PaymentOrder paymentOrder= getPayOrderInfo(paymentOrderId);
    Calendar cal = Calendar.getInstance();
    cal.setTime(new Date());
    cal.add(Calendar.MINUTE, -5);
    Date failTime = cal.getTime();
    if (paymentOrder.getTimeCreate().getTime() < failTime.getTime()) throw new ServiceException(ResultCode.Fail,"支付超时");
    if (paymentOrder.getPayPrice() < 0) throw new ServiceException(ResultCode.Fail);
    PaymentType paymentType = paymentTypeService.getById(paymentOrder.getPayTypeId());
    PaymentPay pay = new PaymentPay();
    pay.setPaymentType("weixin");
    pay.setPaymentPrice(paymentOrder.getPayPrice());
    pay.setPaymentPrice((long) (paymentOrder.getPayPrice() * paymentType.getWxPay()));
    pay.setUserId(paymentOrder.getUserId());
    pay.setOrderId(paymentOrder.getId());
    pay.setPaymentTimeStart(formatter.format(new Date()));
    Calendar cal1 = Calendar.getInstance();
    cal.setTime(new Date());
    cal.add(Calendar.MINUTE, 5);
    Date expireTime = cal1.getTime();
 
    String format = formatter.format(expireTime);
    pay.setPaymentTimeExpire(format);
    pay.setTimeCreate(new DateTime());
    pay.setTimeUpdate(new DateTime());
    boolean save = save(pay);
    paymentOrder.setPaymentPayId(pay.getId());
    paymentOrder.setPaymentStatus(1);
    boolean update = paymentOrderService.updateById(paymentOrder);
    return pay.getId();
  }
  
  @Override
  public void finishPay(String paymentPayId) {
    PaymentPay paymentPay = getById(paymentPayId);
    if(BeanUtil.isEmpty(paymentPay)) throw new ServiceException(ResultCode.ValidateError);
    PaymentOrder paymentOrder = paymentOrderService.getById(paymentPay.getOrderId());
    if (BeanUtil.isEmpty(paymentOrder)) throw new ServiceException(ResultCode.ValidateError);
    paymentPay.setPaymentResultData("支付成功");
    if (paymentPay.getPaymentStatus() == 9 || paymentPay.getPaymentStatus() == 8) throw new ServiceException(ResultCode.Fail, "订单错误");
    paymentPay.setPaymentStatus(9);
    paymentPay.setProcessStatus(1);
    paymentPay.setTimeUpdate(new DateTime());
    
    boolean update = updateById(paymentPay);
  }
  
  @Override
  @Transactional(rollbackFor = Exception.class)
  public void taskPaymentPayTrue(PaymentPay pay) {
    pay.setProcessStatus(9);
    pay.setTimeFinish(new DateTime());
    boolean update = updateById(pay);
    
    PaymentOrder order = paymentOrderService.getById(pay.getOrderId());
    order.setPaymentStatus(9);
    order.setOrderStatus(2);
    order.setTimeFinish(new DateTime());
    order.setPaymentType(pay.getPaymentType());
    order.setProcessStatus(1);
    paymentOrderService.updateById(order);
  }
  
  
  public PaymentOrder getPayOrderInfo(String paymentOrderId) {
    PaymentOrder paymentOrder = paymentOrderService.getById(paymentOrderId);
    if (BeanUtil.isEmpty(paymentOrder)) throw new ServiceException(ResultCode.Fail, "订单不存在");
    if (paymentOrder.getOrderStatus() > 0) throw new ServiceException(ResultCode.Fail, "订单已锁定");
    if (!StpUtil.getLoginIdAsString().equals(paymentOrder.getUserId())) throw new ServiceException(ResultCode.Fail, "订单不存在");
    Calendar cal = Calendar.getInstance();
    cal.setTime(new Date());
    cal.add(Calendar.MINUTE, -5);
    Date time = cal.getTime();
    if (paymentOrder.getTimeCreate().getTime() < time.getTime()) throw new ServiceException(ResultCode.Fail, "支付超时");
    return paymentOrder;
  }
}
