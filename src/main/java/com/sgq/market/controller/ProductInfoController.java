package com.sgq.market.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.hutool.core.bean.BeanUtil;
import com.sgq.market.constants.ResultCode;
import com.sgq.market.entity.ProductInfo;
import com.sgq.market.entity.ProductOrder;
import com.sgq.market.entity.ProductVoucher;
import com.sgq.market.exception.ServiceException;
import com.sgq.market.model.R;
import com.sgq.market.model.dto.ProductInfoDto;
import com.sgq.market.model.dto.ProductInfoPageDto;
import com.sgq.market.model.vo.ProductInfoDetailVo;
import com.sgq.market.model.vo.ProductInfoPageVo;
import com.sgq.market.model.vo.ProductInfoTrendVo;
import com.sgq.market.service.ProductInfoService;
import com.sgq.market.service.ProductOrderService;
import com.sgq.market.service.ProductVoucherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zhangyihua
 * @version 1.0
 * @description TODO
 * @date 2024/3/1 21:02
 */
@RestController
@RequestMapping("/product/info")
@SaCheckLogin
public class ProductInfoController {
  @Autowired
  private ProductInfoService productInfoService;
  @Autowired
  private ProductOrderService productOrderService;
  @Autowired
  private ProductVoucherService productVoucherService;
  @PostMapping
  public R createProductInfo(@RequestBody ProductInfoDto dto){
    return productInfoService.createProductInfo(dto);
  }
  @GetMapping("/page")
  public R<List<ProductInfoPageVo>> getProductList(ProductInfoPageDto pageDto){
    List<ProductInfoPageVo> result =  productInfoService.getProductList(pageDto);
    return R.ok(result);
  }
  @GetMapping("/detail")
  public R<ProductInfoDetailVo> getProductInfo(String productId){
   ProductInfoDetailVo productInfoDetailVo =  productInfoService.getProductInfo(productId);
    ProductVoucher one = productVoucherService.lambdaQuery().eq(ProductVoucher::getProductId, productInfoDetailVo.getId()).one();
    productInfoDetailVo.setProductVoucher(one);
    return R.ok(productInfoDetailVo);
  }
  @DeleteMapping("/{id}")
  public R delProductInfo(@PathVariable("id") String id){
    boolean result = productInfoService.removeById(id);
    if(!result) throw new ServiceException(ResultCode.DeleteError);
    return R.ok();
  }
  @PutMapping("/disable/{id}")
  public R disableProductInfo(@PathVariable("id") String id) {
    ProductInfo productInfo = productInfoService.getById(id);
    if (BeanUtil.isEmpty(productInfo)) throw new ServiceException(ResultCode.NotFindError);
    productInfo.setStatus(10);
    boolean result = productInfoService.updateById(productInfo);
    if (!result) throw new ServiceException(ResultCode.UpdateError);
    return R.ok();
  }
  @GetMapping("/my")
  public R<List<ProductInfo>> getMyProductInfo() {
    List<ProductInfo> list = productInfoService.getMyProductInfoList();
    return R.ok(list);
  }
  @GetMapping("/my/collect")
  public R<List<ProductInfo>> getMyProductCollectInfo() {
    List<ProductInfo> list = productInfoService.getMyProductCollectInfo();
    return R.ok(list);
  }
  @GetMapping("/list/my")
  public R<List<ProductInfoTrendVo>> getMyProductInfoList(){
    List<ProductInfo> myProductList = productInfoService.getMyProductInfoList();
    List<ProductInfoTrendVo> publish = myProductList.stream().map(item -> {
      ProductInfoTrendVo bean = BeanUtil.toBean(item, ProductInfoTrendVo.class);
      bean.setType("publish");
      return bean;
    }).collect(Collectors.toList());
    
    List<ProductOrder> myBuyOrder = productOrderService.getMyBuyOrder();
    List<ProductInfoTrendVo> buy = myBuyOrder.stream().map(item -> {
      ProductInfo productInfo = productInfoService.getById(item.getProductId());
      ProductInfoTrendVo bean = BeanUtil.toBean(productInfo, ProductInfoTrendVo.class);
      bean.setCreateTime(item.getCreateTime().getTime());
      bean.setType("buy");
      return bean;
    }).collect(Collectors.toList());
    
    List<ProductOrder> mySellOrder = productOrderService.getMySellOrder();
    List<ProductInfoTrendVo> sell = mySellOrder.stream().map(item -> {
      ProductInfo productInfo = productInfoService.getById(item.getProductId());
      ProductInfoTrendVo bean = BeanUtil.toBean(productInfo, ProductInfoTrendVo.class);
      bean.setType("sell");
      bean.setCreateTime(item.getCreateTime().getTime());
      return bean;
    }).collect(Collectors.toList());
    publish.addAll(buy);
    publish.addAll(sell);
    publish.sort((o1, o2) -> (int) (o2.getCreateTime()-o1.getCreateTime()));
    return R.ok(publish);
  }
  @PutMapping("/like/count/{productId}")
  public R createLikeCount(@PathVariable("productId")String productId ){
    productInfoService.createLikeCount(productId);
    return R.ok();
  }
}

