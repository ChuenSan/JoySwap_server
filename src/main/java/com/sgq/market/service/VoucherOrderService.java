package com.sgq.market.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sgq.market.entity.VoucherOrder;

public interface VoucherOrderService extends IService<VoucherOrder> {
  void seckill(String voucherId);
  
  void createVoucherId(String voucherId, String userId);
}
