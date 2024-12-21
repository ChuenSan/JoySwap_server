package com.sgq.market.service;

import cn.dev33.satoken.stp.SaTokenInfo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import com.sgq.market.entity.SystemUser;
import com.sgq.market.model.R;
import com.sgq.market.model.dto.SystemUserCreateDto;
import com.sgq.market.model.dto.SystemUserLoginDto;
import com.sgq.market.model.dto.SystemUserPageDto;
import com.sgq.market.model.dto.SystemUserUpdateDto;

/**
 * @author zhangyihua
 * @version 1.0
 * @description TODO
 * @date 2024/2/18 21:46
 */
public interface SystemUserService extends IService<SystemUser> {
  SaTokenInfo login(SystemUserLoginDto request);
  
  R<SystemUser> getUserInfo();
  
  Page getUserPageList(SystemUserPageDto userPageDto);
  
  void create(SystemUserCreateDto dto);
  
  void edit(SystemUserUpdateDto dto);
}
