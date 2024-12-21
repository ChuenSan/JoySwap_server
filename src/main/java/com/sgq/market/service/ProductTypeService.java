package com.sgq.market.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sgq.market.entity.ProductType;
import com.sgq.market.model.dto.SystemProductTypePageDto;

public interface ProductTypeService extends IService<ProductType> {
  Page getTypePageList(SystemProductTypePageDto dto);
}
