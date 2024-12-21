package com.sgq.market.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sgq.market.entity.ProductCollect;
import com.sgq.market.model.R;

public interface ProductCollectService extends IService<ProductCollect> {
  R delete(String id);
}
