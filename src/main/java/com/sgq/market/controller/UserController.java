package com.sgq.market.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import com.sgq.market.entity.User;
import com.sgq.market.model.R;

import com.sgq.market.model.dto.UpdateUserInfoDto;
import com.sgq.market.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author zhangyihua
 * @version 1.0
 * @description TODO
 * @date 2024/1/28 15:01
 */
@RestController
@RequestMapping("/user")
@SaCheckLogin
public class UserController {
  @Autowired
  private UserService userService;
  
  @GetMapping("/logout")
  public R logout(){
    StpUtil.logout(StpUtil.getLoginId());
    return R.ok();
  }
  @GetMapping("/getUserInfo")
  public R<User> getUserInfo() {
    return userService.getUserInfo();
  }
  
  @GetMapping("/getUserInfo/byId")
  public R<User> getUserInfo(String userId) {
    return userService.getUserInfo(userId);
  }

  @PutMapping
  public R updateUserInfo(@RequestBody UpdateUserInfoDto dto){
    userService.updateUserInfo(dto);
    return R.ok();
  }
  
  @PutMapping("/password")
  public R updateUserInfoPass(@RequestBody UpdateUserInfoDto dto) {
    userService.updateUserInfoDetail(dto);
    return R.ok();
  }
}
