package com.sgq.market.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sgq.market.entity.ProductVoucher;
import com.sgq.market.model.dto.ProductVoucherCreateDto;

public interface ProductVoucherService extends IService<ProductVoucher> {
  void create(ProductVoucherCreateDto dto);
  
}
