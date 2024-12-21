package com.sgq.market.controller.admin;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sgq.market.constants.ResultCode;
import com.sgq.market.entity.User;
import com.sgq.market.exception.ServiceException;
import com.sgq.market.model.R;
import com.sgq.market.model.dto.UserAdminPageDto;
import com.sgq.market.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping("/admin/web/user")
@SaCheckLogin
@SaCheckRole(value = {"admin"})
public class UserAdminController {
  @Autowired
  private UserService userService;
  
  @GetMapping("/page")
  public R<Page> getUserList(UserAdminPageDto dto) {
    
    Page page = userService.getUserList(dto);
    return R.ok(page);
  }
  
  @PutMapping("/able/{id}")
  public R ableUser(@PathVariable("id") String userId) {
    User user = userService.getById(userId);
    if (BeanUtil.isEmpty(user)) throw new ServiceException(ResultCode.NotFindError);
    user.setStatus(9);
    user.setUpdateTime(new Date().getTime());
    boolean update = userService.updateById(user);
    if (!update) throw new ServiceException(ResultCode.UpdateError);
    return R.ok();
  }
  
  @PutMapping("/disable/{id}")
  public R disableUser(@PathVariable("id") String userId) {
    User user = userService.getById(userId);
    if (BeanUtil.isEmpty(user)) throw new ServiceException(ResultCode.NotFindError);
    user.setStatus(0);
    user.setUpdateTime(new Date().getTime());
    boolean update = userService.updateById(user);
    if (!update) throw new ServiceException(ResultCode.UpdateError);
    StpUtil.logout(userId);
    return R.ok();
  }
  
  @PutMapping("/check/success/{id}")
  public R success(@PathVariable("id") String userId) {
    User user = userService.getById(userId);
    if (BeanUtil.isEmpty(user)) throw new ServiceException(ResultCode.NotFindError);
    if(StrUtil.isNotEmpty(user.getCheckAvatar())) user.setAvatar(user.getCheckAvatar());
    if(StrUtil.isNotEmpty(user.getCheckNickName())) user.setNickName(user.getCheckNickName());
    if(StrUtil.isNotEmpty(user.getCheckIntro())) user.setIntro(user.getCheckIntro());
    user.setCheckAvatar("");
    user.setCheckNickName("");
    user.setCheckIntro("");
    user.setUpdateTime(new Date().getTime());
    user.setCheckStatus(9);
    boolean update = userService.updateById(user);
    if (!update) throw new ServiceException(ResultCode.UpdateError);
    return R.ok();
  }
  
  @PutMapping("/check/fail/{id}")
  public R fail(@PathVariable("id") String userId) {
    User user = userService.getById(userId);
    user.setUpdateTime(new Date().getTime());
    user.setCheckStatus(7);
    boolean update = userService.updateById(user);
    if (!update) throw new ServiceException(ResultCode.UpdateError);
    return R.ok();
  }
}
