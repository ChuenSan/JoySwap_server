package com.sgq.market.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sgq.market.entity.ProductInfo;
import com.sgq.market.model.R;
import com.sgq.market.model.dto.ProductInfoDto;
import com.sgq.market.model.dto.ProductInfoPageDto;
import com.sgq.market.model.dto.SystemProductInfoPageDto;
import com.sgq.market.model.vo.ProductInfoDetailVo;
import com.sgq.market.model.vo.ProductInfoPageVo;

import java.util.List;
import java.util.Map;

/**
 * @author sgq
 * @version 1.0
 * @description TODO
 * @date 2024/3/1 21:03
 */
public interface ProductInfoService extends IService<ProductInfo> {
  R createProductInfo(ProductInfoDto dto);
  
  List<ProductInfoPageVo> getProductList(ProductInfoPageDto pageDto);
  
  ProductInfoDetailVo getProductInfo(String productId);
  
  List<ProductInfo> getMyProductInfoList();
  
  
  void createLikeCount(String productId);
  
  Page getProductInfoList(SystemProductInfoPageDto dto);
  
  Map getDetail(String id);
  
  List<ProductInfo> getMyProductCollectInfo();
  
  
  void passProduct(String id);
  
  Long getTodayCount();
  
  
  Long getMonthCount();
  
  
  void failProduct(String id);
  
  void downProduct(String id);
}
