package com.sgq.market.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.sgq.market.constants.ResultCode;
import com.sgq.market.entity.ProductVoucher;
import com.sgq.market.exception.ServiceException;
import com.sgq.market.mapper.ProductVoucherMapper;
import com.sgq.market.model.dto.ProductVoucherCreateDto;
import com.sgq.market.service.ProductVoucherService;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Service
public class ProductVoucherServiceImpl extends ServiceImpl<ProductVoucherMapper, ProductVoucher> implements ProductVoucherService {
  @Override
  public void create(ProductVoucherCreateDto dto) {
    ProductVoucher one = lambdaQuery().eq(ProductVoucher::getProductId, dto.getProductId()).one();
    if(BeanUtil.isEmpty(one)){
      ProductVoucher productVoucher = new ProductVoucher();
      productVoucher.setProductId(dto.getProductId());
      productVoucher.setTitle("下单立减 "+ dto.getVoucherValue() + " 元");
      productVoucher.setVoucherValue((long) (100 * dto.getVoucherValue()));
      productVoucher.setStock(1);
      LocalDateTime beginTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(dto.getBeginTime()), ZoneId.systemDefault());
      LocalDateTime endTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(dto.getEndTime()), ZoneId.systemDefault());
      productVoucher.setBeginTime(beginTime);
      productVoucher.setEndTime(endTime);
      productVoucher.setVoucherStatus(9);
      productVoucher.setCreateTime(new Date().getTime());
      productVoucher.setUpdateTime(new Date().getTime());
      boolean save = save(productVoucher);
      if (!save) {
        throw new ServiceException(ResultCode.SaveError);
      }
    }else{
      one.setVoucherValue((long) (100 * dto.getVoucherValue()));
      one.setStock(1);
      one.setTitle("优惠卷" + dto.getVoucherValue() + "元");
      one.setVoucherValue((long) (100 * dto.getVoucherValue()));
      LocalDateTime beginTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(dto.getBeginTime()), ZoneId.systemDefault());
      LocalDateTime endTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(dto.getEndTime()), ZoneId.systemDefault());
      one.setBeginTime(beginTime);
      one.setEndTime(endTime);
      one.setVoucherStatus(9);
      one.setUpdateTime(new Date().getTime());
      boolean update = updateById(one);
      if (!update) {
        throw new ServiceException(ResultCode.UpdateError);
      }
    }
   
  }
}
