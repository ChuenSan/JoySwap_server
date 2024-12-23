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
 * @author sgq
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
  
  /**
   * 处理用户登出请求的控制器方法
   * 当用户希望退出系统时调用此方法，它将清除用户的登录状态
   *
   * @return 返回一个表示操作结果的响应对象，用于告知客户端操作是否成功
   */
  @GetMapping("/logout")
  public R logout(){
    StpUtil.logout(StpUtil.getLoginId());
    return R.ok();
  }
  /**
   * 获取用户信息
   *
   * 该方法通过GET请求处理获取用户信息的请求它调用UserService的getUserInfo方法来获取用户数据
   * 主要用于需要用户信息的场景，比如个人中心页面的展示等
   *
   * @return R<User> 返回一个封装了用户信息的对象R
   */
  @GetMapping("/getUserInfo")
  public R<User> getUserInfo() {
    return userService.getUserInfo();
  }

  /**
   * 根据用户ID获取用户信息
   *
   * @param userId 用户ID，用于标识和获取特定用户的信息
   * @return 返回一个封装了用户信息的响应对象
   */
  @GetMapping("/getUserInfo/byId")
  public R<User> getUserInfo(String userId) {
    return userService.getUserInfo(userId);
  }

  /**
   * 更新用户信息的接口方法
   * 该方法通过HTTP PUT请求接收用户信息数据，并调用userService进行更新操作
   *
   * @param dto 包含待更新用户信息的DTO对象，通过请求体传递
   * @return 返回一个表示操作结果的R对象，表示更新操作的状态
   */
  @PutMapping
  public R updateUserInfo(@RequestBody UpdateUserInfoDto dto){
    userService.updateUserInfo(dto);
    return R.ok();
  }
  
  /**
   * 更新用户密码
   * <p>
   * 该接口用于更新用户的密码信息它期望接收一个JSON格式的数据，
   * 其中包含了需要更新的用户密码信息以及其他可能的用户详情
   *
   * @param dto 包含用户新密码及其他可能用户信息的数据传输对象
   * @return 返回一个表示操作结果的响应对象，如果操作成功，返回R.ok()
   */
  @PutMapping("/password")
  public R updateUserInfoPass(@RequestBody UpdateUserInfoDto dto) {
    userService.updateUserInfoDetail(dto);
    return R.ok();
  }
}
