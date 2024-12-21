package com.sgq.market.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sgq.market.entity.ProductOrder;
import com.sgq.market.model.dto.SystemProductOrderPageDto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface ProductOrderService extends IService<ProductOrder> {
  String createOrder(String productId, String info, String address, String idname, String phone);
  
  void taskMallProductOrderTimeOut(ProductOrder obj);
  
  HashMap<String, String> getUserOrderStat();
  
  
  List<ProductOrder> getMySellOrder();
  
  
  List<ProductOrder> getMyBuyOrder();
  
  
  Page getProductOrderList(SystemProductOrderPageDto dto);
  
  Map getOrderDetail(String productOrderId);
  
  
  Page getProductOrderEvaluateList(SystemProductOrderPageDto dto);
  
  Long getTodayCount();
  
  
  Long getMonthCount();
  
  
  Long getTodayMoneyCount();
  
  Long getMonthMoneyCount();
  
  List<Map> getTableData();
  
  List<Map<String, Object>> getVideoData();
  
  Map<String, List<Map<String, Long>>> getOrderData();
  
}
