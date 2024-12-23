package com.sgq.market.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.BeanCopier;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.sgq.market.constants.ResultCode;
import com.sgq.market.entity.*;
import com.sgq.market.service.*;
import com.sgq.market.entity.*;
import com.sgq.market.exception.ServiceException;
import com.sgq.market.mapper.ProductInfoMapper;
import com.sgq.market.model.R;
import com.sgq.market.model.dto.ProductInfoDto;
import com.sgq.market.model.dto.ProductInfoPageDto;
import com.sgq.market.model.dto.SystemProductInfoPageDto;
import com.sgq.market.model.vo.ProductInfoDetailVo;
import com.sgq.market.model.vo.ProductInfoPageVo;
import com.sgq.market.model.vo.ProductInfoVoucherVo;
import com.sgq.market.service.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author sgq
 * @version 1.0
 * @description TODO
 * @date 2024/3/1 21:03
 */
@Service
public class ProductInfoServiceImpl extends ServiceImpl<ProductInfoMapper, ProductInfo> implements ProductInfoService {
  @Autowired
  private UserService userService;
  @Autowired
  private ProductTypeService productTypeService;
  @Autowired
  private ProductCollectService productCollectService;
  @Autowired
  private ProductVoucherService productVoucherService;
  @Override
  public R createProductInfo(ProductInfoDto dto) {
    ProductInfo productInfo = BeanUtil.toBean(dto, ProductInfo.class);
    if (StrUtil.isNotEmpty(dto.getType())) {
      ProductType productType = productTypeService.lambdaQuery().eq(ProductType::getTypeCode, dto.getType()).one();
      if (!BeanUtil.isEmpty(productType)) {
        productInfo.setTypeCode(productType.getTypeCode());
        productInfo.setTypeName(productType.getTypeName());
      }
    }
    productInfo.setPrice((long) (dto.getPrice() * 100));
    productInfo.setOriginalPrice((long) (dto.getOriginalPrice() * 100));
    productInfo.setStatus(1);
    productInfo.setLikeCount(0);
    productInfo.setUserId(StpUtil.getLoginIdAsString());
    productInfo.setCreateTime(new Date().getTime());
    productInfo.setUpdateTime(new Date().getTime());
    boolean save = save(productInfo);
    if (!save) throw new ServiceException(ResultCode.SaveError);
    return R.ok();
  }
  
  @Override
  public List<ProductInfoPageVo> getProductList(ProductInfoPageDto pageDto) {
    Page<ProductInfo> page = lambdaQuery()
      .eq(StrUtil.isNotEmpty(pageDto.getTypeCode()), ProductInfo::getTypeCode, pageDto.getTypeCode())
      .eq(ProductInfo::getStatus, 9)
      .and(StrUtil.isNotEmpty(pageDto.getKey()), wrapper -> {
        wrapper.like(StrUtil.isNotEmpty(pageDto.getKey()), ProductInfo::getTitle, pageDto.getKey());
      })
      .orderByDesc(ProductInfo::getCreateTime)
      .page(new Page<>(pageDto.getPageNumber(), pageDto.getPageSize()));
    List<ProductInfo> records = page.getRecords();
    List<ProductInfoPageVo> voList = records.stream().map(item -> {
      User user = userService.getById(item.getUserId());
      ProductInfoPageVo vo = BeanUtil.toBean(item, ProductInfoPageVo.class);
      if (!BeanUtil.isEmpty(user)) {
        vo.setAvatar(user.getAvatar());
      }
      return vo;
    }).collect(Collectors.toList());
    return voList;
  }
  
  @Override
  public ProductInfoDetailVo getProductInfo(String productId) {
    ProductInfo productInfo = getById(productId);
    if (BeanUtil.isEmpty(productInfo)) throw new ServiceException(ResultCode.NotFindError);
    ProductInfoDetailVo productInfoDetailVo = BeanUtil.toBean(productInfo, ProductInfoDetailVo.class);
    User user = userService.getById(productInfo.getUserId());
    if (BeanUtil.isEmpty(user)) throw new ServiceException(ResultCode.NotFindError);
    productInfoDetailVo.setUserInfo(user);
    return productInfoDetailVo;
  }
  
  @Override
  public List<ProductInfo> getMyProductInfoList() {
    String userId = StpUtil.getLoginIdAsString();
    List<ProductInfo> list = lambdaQuery()
      .eq(ProductInfo::getUserId, userId)
      .ne(ProductInfo::getStatus,10)
      .orderByDesc(ProductInfo::getCreateTime).list();
    return list;
  }
  
  @Override
  public void createLikeCount(String productId) {
    ProductInfo productInfo = getById(productId);
    if (BeanUtil.isEmpty(productInfo)) throw new ServiceException(ResultCode.NotFindError);
    Integer likeCount = productInfo.getLikeCount();
    productInfo.setLikeCount(likeCount + 1);
    boolean update = updateById(productInfo);
    if (!update) throw new ServiceException(ResultCode.UpdateError);
  }
  
  @Override
  public Page getProductInfoList(SystemProductInfoPageDto dto) {
    Page<ProductInfo> page = lambdaQuery()
      .eq(StrUtil.isNotEmpty(dto.getStatus()), ProductInfo::getStatus, dto.getStatus())
      .like(StrUtil.isNotEmpty(dto.getKey()), ProductInfo::getTitle, dto.getKey()).or()
      .like(StrUtil.isNotEmpty(dto.getKey()), ProductInfo::getTypeName, dto.getKey())
      .ne(ProductInfo::getStatus, 10)
      .orderByDesc(ProductInfo::getCreateTime)
      .page(new Page<>(dto.getPageNumber(), dto.getPageSize()));
    Page<ProductInfoVoucherVo> resultPage = copyPage(page, ProductInfoVoucherVo.class);
    List<ProductInfoVoucherVo> records = resultPage.getRecords();
    for (ProductInfoVoucherVo vo : records) {
      ProductVoucher one = productVoucherService.lambdaQuery().eq(ProductVoucher::getProductId, vo.getId()).one();
      vo.setProductVoucher(one);
    }
    return resultPage;
  }
  
  public static <T> Page<T> copyPage(Page sPage, Class<T> tClass) {
    Page<T> tPage = new Page();
    tPage.setPages(sPage.getPages());
    tPage.setCurrent(sPage.getCurrent());
    tPage.setSize(sPage.getSize());
    tPage.setTotal(sPage.getTotal());
    List<T> tList = new ArrayList();
    Iterator var4 = sPage.getRecords().iterator();
    
    while (var4.hasNext()) {
      Object record = var4.next();
      tList.add(toBean(record, tClass));
    }
    
    tPage.setRecords(tList);
    return tPage;
  }
  
  public static <T> T toBean(Object source, Class<T> clazz) {
    T target = ReflectUtil.newInstance(clazz, new Object[0]);
    copyProperties(source, target);
    return target;
  }
  
  public static void copyProperties(Object source, Object target) {
    copyProperties(source, target, CopyOptions.create());
  }
  public static void copyProperties(Object source, Object target, CopyOptions copyOptions) {
    if (null == copyOptions) {
      copyOptions = new CopyOptions();
    }
    
    BeanCopier.create(source, target, copyOptions).copy();
  }
  @Override
  public Map getDetail(String id) {
    ProductInfo productInfo = getById(id);
    if (BeanUtil.isEmpty(productInfo)) throw new ServiceException(ResultCode.NotFindError);
    User user = userService.getById(productInfo.getUserId());
    if (BeanUtil.isEmpty(user)) throw new ServiceException(ResultCode.NotFindError);
    HashMap<Object, Object> map = new HashMap<>();
    map.put("productInfo", productInfo);
    map.put("user", user);
    return map;
  }
  
  @Override
  public List<ProductInfo> getMyProductCollectInfo() {
    String userId = StpUtil.getLoginIdAsString();
    List<ProductCollect> collectList = productCollectService.lambdaQuery().eq(ProductCollect::getUserId, userId).list();
    List<ProductInfo> collect1 = collectList.stream().map(collect -> {
      ProductInfo productInfo = getById(collect.getProductId());
      ProductVoucher one = productVoucherService.lambdaQuery().eq(ProductVoucher::getProductId, productInfo.getId()).one();
      if(!BeanUtil.isEmpty(one)){
        productInfo.setProductVoucher(one);
      }
      return productInfo;
    }).collect(Collectors.toList());
    return collect1;
  }
  
  @Override
  public void passProduct(String id) {
    ProductInfo productInfo = getById(id);
    if (BeanUtil.isEmpty(productInfo)) throw new ServiceException(ResultCode.NotFindError);
    if(productInfo.getStatus() != 1) throw new ServiceException(ResultCode.ValidateError);
    productInfo.setStatus(9);
    boolean update = updateById(productInfo);
    if (!update) throw new ServiceException(ResultCode.UpdateError);
    
  }
  
  @Override
  public Long getTodayCount() {
    //获取今日0点开始时间
    LocalDateTime startDay = LocalDate.now().atStartOfDay();
    long startTime = startDay.toEpochSecond(ZoneOffset.UTC) * 1000;
    long endTime = new Date().getTime();
    Long count = lambdaQuery().between(ProductInfo::getCreateTime, startTime, endTime).count();
    return count;
  }
  
  @Override
  public Long getMonthCount() {
    LocalDate now = LocalDate.now();
    LocalDateTime startOfMonth = now.with(TemporalAdjusters.firstDayOfMonth()).atStartOfDay();
    long startTime = startOfMonth.toEpochSecond(ZoneOffset.UTC) * 1000;
    long endTime = new Date().getTime();
    Long count = lambdaQuery().between(ProductInfo::getCreateTime, startTime, endTime).count();
    return count;
  }
  
  @Override
  public void failProduct(String id) {
    ProductInfo productInfo = getById(id);
    if (BeanUtil.isEmpty(productInfo)) throw new ServiceException(ResultCode.NotFindError);
    if (productInfo.getStatus() != 1) throw new ServiceException(ResultCode.ValidateError);
    productInfo.setStatus(2);
    boolean update = updateById(productInfo);
    if (!update) throw new ServiceException(ResultCode.UpdateError);
  }
  
  @Override
  public void downProduct(String id) {
    ProductInfo productInfo = getById(id);
    if (BeanUtil.isEmpty(productInfo)) throw new ServiceException(ResultCode.NotFindError);
    if (productInfo.getStatus() != 9) throw new ServiceException(ResultCode.ValidateError);
    productInfo.setStatus(2);
    boolean update = updateById(productInfo);
    if (!update) throw new ServiceException(ResultCode.UpdateError);
  }
}
