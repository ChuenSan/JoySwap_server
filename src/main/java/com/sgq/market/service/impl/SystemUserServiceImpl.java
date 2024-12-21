package com.sgq.market.service.impl;

import cn.dev33.satoken.secure.SaSecureUtil;
import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sgq.market.constants.ResultCode;
import com.sgq.market.entity.SystemRole;
import com.sgq.market.entity.SystemUser;
import com.sgq.market.exception.ServiceException;
import com.sgq.market.mapper.SystemUserMapper;

import com.sgq.market.model.R;
import com.sgq.market.model.dto.SystemUserCreateDto;
import com.sgq.market.model.dto.SystemUserLoginDto;
import com.sgq.market.model.dto.SystemUserPageDto;
import com.sgq.market.model.dto.SystemUserUpdateDto;
import com.sgq.market.service.SystemRoleService;
import com.sgq.market.service.SystemUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @author zhangyihua
 * @version 1.0
 * @description TODO
 * @date 2024/2/18 21:50
 */
@Service
public class SystemUserServiceImpl extends ServiceImpl<SystemUserMapper, SystemUser> implements SystemUserService {
  @Autowired
  private SystemRoleService systemRoleService;
  
  @Override
  public SaTokenInfo login(SystemUserLoginDto request) {
    SystemUser systemUser = lambdaQuery().eq(SystemUser::getUsername, request.getUsername()).one();
    if (null == systemUser) {
      throw new ServiceException(ResultCode.NotFindError);
    }
    String password = systemUser.getPassword();
    String md5 = SaSecureUtil.md5(request.getPassword());
    if (!md5.equals(password)) throw new ServiceException(ResultCode.Fail, "账户名或密码错误");
    systemUser.setUpdateTime(new Date().getTime());
    updateById(systemUser);
    StpUtil.login(systemUser.getId());
    return StpUtil.getTokenInfo();
  }
  
  @Override
  public R<SystemUser> getUserInfo() {
    Object loginId = StpUtil.getLoginId();
    SystemUser user = lambdaQuery().eq(SystemUser::getId, loginId).one();
    if (user == null) {
      throw new ServiceException(ResultCode.NotFindError, "用户不存在");
    }
    return R.ok(user);
  }
  
  @Override
  public Page getUserPageList(SystemUserPageDto userPageDto) {
    Page<SystemUser> page = lambdaQuery()
      .like(StrUtil.isNotEmpty(userPageDto.getKey()), SystemUser::getUsername, userPageDto.getKey()).or()
      .like(StrUtil.isNotEmpty(userPageDto.getKey()), SystemUser::getName, userPageDto.getKey())
      .orderByDesc(SystemUser::getCreateTime)
      .page(new Page<>(userPageDto.getPageNumber(), userPageDto.getPageSize()));
    return page;
    
  }
  
  @Override
  public void create(SystemUserCreateDto dto) {
    SystemRole role = systemRoleService.lambdaQuery().eq(SystemRole::getRoleCode, dto.getRoleCode()).one();
    if (ObjectUtil.isEmpty(role)) throw new ServiceException(ResultCode.ValidateError);
    Long count = lambdaQuery().eq(SystemUser::getUsername, dto.getUsername()).count();
    if (count > 1) throw new ServiceException(ResultCode.SaveError, "用户名存在");
    SystemUser systemUser = BeanUtil.toBean(dto, SystemUser.class);
    systemUser.setCreateTime(new Date().getTime());
    systemUser.setUpdateTime(new Date().getTime());
    systemUser.setRoleId(role.getId());
    systemUser.setRoleCode(role.getRoleCode());
    systemUser.setRoleName(role.getRoleName());
    systemUser.setPassword(SaSecureUtil.md5(dto.getPassword()));
    boolean save = save(systemUser);
    if (!save) throw new ServiceException(ResultCode.SaveError);
  }
  
  @Override
  public void edit(SystemUserUpdateDto dto) {
    if (StrUtil.isEmpty(dto.getId())) throw new ServiceException(ResultCode.ValidateError);
    SystemUser systemUser = getById(dto.getId());
    SystemRole role = systemRoleService.lambdaQuery().eq(SystemRole::getRoleCode, dto.getRoleCode()).one();
    systemUser.setUsername(dto.getUsername());
    systemUser.setRoleId(role.getId());
    systemUser.setRoleName(role.getRoleName());
    systemUser.setName(dto.getName());
    if (StrUtil.isNotEmpty(dto.getPassword())) {
      String md5 = SaSecureUtil.md5(dto.getPassword());
      systemUser.setPassword(md5);
    }
    systemUser.setUpdateTime(new Date().getTime());
    boolean update = updateById(systemUser);
    if (!update)
      throw new ServiceException(ResultCode.UpdateError);
  }
}
