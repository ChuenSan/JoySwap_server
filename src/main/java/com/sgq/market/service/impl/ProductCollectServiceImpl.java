package com.sgq.market.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sgq.market.constants.ResultCode;
import com.sgq.market.entity.ProductCollect;
import com.sgq.market.exception.ServiceException;
import com.sgq.market.mapper.ProductCollectMapper;
import com.sgq.market.model.R;
import com.sgq.market.service.ProductCollectService;
import org.springframework.stereotype.Service;

@Service
public class ProductCollectServiceImpl extends ServiceImpl<ProductCollectMapper, ProductCollect> implements ProductCollectService {
  @Override
  public R delete(String productId) {
    ProductCollect collect = lambdaQuery().eq(ProductCollect::getUserId, StpUtil.getLoginIdAsString())
      .eq(ProductCollect::getProductId, productId).one();
    if (null == collect) throw new ServiceException(ResultCode.NotFindError);
    if (!collect.getUserId().equals(StpUtil.getLoginIdAsString())) throw new ServiceException(ResultCode.NotFindError);
    boolean remove = lambdaUpdate()
      .eq(ProductCollect::getUserId, StpUtil.getLoginIdAsString())
      .eq(ProductCollect::getProductId, productId).remove();
    if (!remove) throw new ServiceException(ResultCode.DeleteError);
    return R.ok();
  }
}
