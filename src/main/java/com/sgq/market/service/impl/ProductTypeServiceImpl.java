package com.sgq.market.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sgq.market.entity.ProductType;
import com.sgq.market.mapper.ProductTypeMapper;
import com.sgq.market.model.dto.SystemProductTypePageDto;
import com.sgq.market.service.ProductTypeService;
import org.springframework.stereotype.Service;

@Service
public class ProductTypeServiceImpl extends ServiceImpl<ProductTypeMapper, ProductType> implements ProductTypeService {
  @Override
  public Page getTypePageList(SystemProductTypePageDto dto) {
    Page<ProductType> page = lambdaQuery()
      .like(StrUtil.isNotEmpty(dto.getKey()), ProductType::getTypeCode, dto.getKey()).or()
      .like(StrUtil.isNotEmpty(dto.getKey()), ProductType::getTypeName, dto.getKey())
      .orderByDesc(ProductType::getCreateTime)
      .page(new Page<>(dto.getPageNumber(), dto.getPageSize()));
    return page;
  }
}
