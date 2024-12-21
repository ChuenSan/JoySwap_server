package com.sgq.market.controller.admin;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckRole;
import com.sgq.market.constants.ResultCode;
import com.sgq.market.exception.ServiceException;
import com.sgq.market.model.R;
import com.sgq.market.model.dto.ProductVoucherCreateDto;
import com.sgq.market.service.ProductVoucherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/product/voucher")
@SaCheckLogin
//@SaCheckRole(value = {"admin"})
public class ProductVoucherAdminController {
  @Autowired
  private ProductVoucherService productVoucherService;
  @PostMapping
  public R create(@RequestBody ProductVoucherCreateDto dto){
    productVoucherService.create(dto);
    return R.ok();
  }
  @DeleteMapping("/{id}")
  public R delete(@PathVariable("id") String id) {
    boolean update = productVoucherService.removeById(id);
    if(!update){
      throw new ServiceException(ResultCode.DeleteError);
    }
    return R.ok();
  }
}
