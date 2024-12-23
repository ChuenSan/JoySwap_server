package com.sgq.market.service;

import cn.dev33.satoken.stp.SaTokenInfo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sgq.market.entity.User;
import com.sgq.market.model.R;
import com.baomidou.mybatisplus.extension.service.IService;

import com.sgq.market.model.dto.UpdateUserInfoDto;
import com.sgq.market.model.dto.UserAdminPageDto;
import com.sgq.market.model.dto.UserLoginDto;

import java.util.List;
import java.util.Map;

/**
* @author sgq
* @description 针对表【user】的数据库操作Service
* @createDate 2024-01-28 16:52:33
*/
public interface UserService extends IService<User> {
  SaTokenInfo login(UserLoginDto request);
  
  R<User> getUserInfo();
  

  
  void getLoginCode(String phone);
  
  R<User> getUserInfo(String userId);
  
  void updateUserInfoDetail(UpdateUserInfoDto dto);
  
  Page getUserList(UserAdminPageDto dto);
  
  SaTokenInfo loginPwd(UserLoginDto request);
  
  void updateUserInfo(UpdateUserInfoDto dto);
  
  Map getNum1UserStat();
  
  List<Map> getUserStat();
  
}
