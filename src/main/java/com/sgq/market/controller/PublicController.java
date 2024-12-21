package com.sgq.market.controller;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import com.sgq.market.model.R;
import com.sgq.market.model.dto.UserLoginDto;
import com.sgq.market.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

/**
 * @author zhangyihua
 * @version 1.0
 * @description TODO
 * @date 2024/2/19 21:00
 */
@RestController
@RequestMapping("/public")
public class PublicController {
  @Autowired
  private UserService userService;
  @PostMapping("/user/login")
  public R<SaTokenInfo> login(@RequestBody UserLoginDto request) {
    SaTokenInfo loginToken = userService.login(request);
    return R.ok(loginToken);
  }
  
  
  @PostMapping("/user/login/pwd")
  public R<SaTokenInfo> loginPwd(@RequestBody UserLoginDto request) {
    SaTokenInfo loginToken = userService.loginPwd(request);
    return R.ok(loginToken);
  }
  @GetMapping("/getCheckCode")
  public R<String> getLoginCode(String phone) {
    userService.getLoginCode(phone);
    return R.ok();
  }
}
