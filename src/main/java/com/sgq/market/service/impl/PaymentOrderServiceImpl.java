package com.sgq.market.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.sgq.market.Att;
import com.sgq.market.constants.ResultCode;
import com.sgq.market.entity.PaymentOrder;
import com.sgq.market.entity.PaymentType;
import com.sgq.market.entity.ProductOrder;
import com.sgq.market.entity.User;
import com.sgq.market.service.PaymentOrderService;
import com.sgq.market.service.PaymentTypeService;
import com.sgq.market.service.ProductOrderService;
import com.sgq.market.service.UserService;
import com.zyh.market.entity.*;
import com.sgq.market.exception.ServiceException;
import com.sgq.market.mapper.PaymentOrderMapper;
import com.sgq.market.model.dto.SystemPaymentOrderPageDto;
import com.zyh.market.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class PaymentOrderServiceImpl extends ServiceImpl<PaymentOrderMapper, PaymentOrder> implements PaymentOrderService {
  @Autowired
  private UserService userService;
  @Autowired
  private PaymentTypeService paymentTypeService;
  @Autowired
  private ProductOrderService productOrderService;
  @Autowired
  private PaymentOrderService paymentOrderService;
  @Override
  @Transactional(rollbackFor = Exception.class)
  public String createPaymentOrder(String orderId) {
    User user = userService.getById(StpUtil.getLoginIdAsString());
    ProductOrder productOrder = productOrderService.getById(orderId);
    if (BeanUtil.isEmpty(productOrder)) throw new ServiceException(ResultCode.ValidateError);
    if (productOrder.getDealStatus() != 0) throw new ServiceException(ResultCode.ValidateError);
    PaymentOrder paymentOrder = new PaymentOrder();
    paymentOrder.setOrderNumber(RandomUtil.randomNumbers(12));
    paymentOrder.setUserId(user.getId());
    paymentOrder.setPayPrice(0L);
    paymentOrder.setPayTypeId(1L); //微信支付
    productOrder.setDealStatus(2);
    PaymentType paymentType = paymentTypeService.getById(paymentOrder.getPayTypeId());
    paymentOrder.setPayPrice(productOrder.getBuyMoney() + paymentOrder.getPayPrice());
    if (paymentOrder.getPayPrice() < 0) throw new ServiceException(ResultCode.Fail);
    paymentOrder.setPayTypeName(paymentType.getTypeName());
    paymentOrder.setTimeCreate(new DateTime());
    paymentOrder.setTimeUpdate(new DateTime());
    paymentOrder.setProcessStatus(0);
    paymentOrder.setPaymentStatus(0);
    paymentOrder.setOrderStatus(0);
    save(paymentOrder);
    productOrder.setPayOrderId(paymentOrder.getId());
    productOrderService.updateById(productOrder);
    return paymentOrder.getId();
  }
  
  @Override
  @Transactional(rollbackFor = Exception.class)
  public void taskPaymentOrderPayTrue(PaymentOrder paymentOrder) {
    if (paymentOrder.getPayTypeId() == 1) {
      update_mall(paymentOrder);
    } else if (paymentOrder.getPayTypeId() == 2) {
  
    } else {
      log.info("未知的支付类型：" + paymentOrder.getPayTypeId());
    }
  }
  
  @Override
  public Page getPaymentOrderList(SystemPaymentOrderPageDto dto) {
    Page<PaymentOrder> page = lambdaQuery()
      .like(StrUtil.isNotEmpty(dto.getKey()), PaymentOrder::getOrderNumber, dto.getKey())
      .orderByDesc(PaymentOrder::getTimeCreate)
      .page(new Page<>(dto.getPageNumber(), dto.getPageSize()));
    return page;
  }
  
  @Override
  public Map getOrderDetail(String id) {
    HashMap<Object, Object> map = new HashMap<>();
    PaymentOrder paymentOrder = getById(id);
    if (BeanUtil.isEmpty(paymentOrder)) throw new ServiceException(ResultCode.NotFindError);
    User user = userService.getById(paymentOrder.getUserId());
    if (BeanUtil.isEmpty(user)) throw new ServiceException(ResultCode.NotFindError);
    map.put("paymentOrder",paymentOrder);
    map.put("user", user);
    return map;
  }
  
  private void update_mall(PaymentOrder paymentOrder) {
    paymentOrder.setProcessStatus(9);
    paymentOrder.setTimeFinish(new DateTime());
    paymentOrderService.updateById(paymentOrder);
    productOrderService.update()
      .eq(Att.ProductOrder.payOrderId, paymentOrder.getId())
      .set(Att.ProductOrder.payStatus, 9)
      .set(Att.ProductOrder.dealStatus, 3)
      .update();
  }
}
