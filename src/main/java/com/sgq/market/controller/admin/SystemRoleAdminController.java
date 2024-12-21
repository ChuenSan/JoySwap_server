package com.sgq.market.controller.admin;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.annotation.SaMode;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sgq.market.constants.ResultCode;
import com.sgq.market.entity.SystemRole;
import com.sgq.market.entity.SystemUser;
import com.sgq.market.exception.ServiceException;
import com.sgq.market.model.R;
import com.sgq.market.model.dto.SystemRoleCreateDto;
import com.sgq.market.model.dto.SystemRolePageDto;
import com.sgq.market.model.dto.SystemRoleUpdateDto;
import com.sgq.market.service.SystemRoleService;
import com.sgq.market.service.SystemUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * @author zhangyihua
 * @version 1.0
 * @description TODO
 * @date 2024/2/19 20:20
 */
@RestController
@RequestMapping("/admin/role")
@SaCheckLogin
@SaCheckRole(value = {"system"})
public class SystemRoleAdminController {
  @Autowired
  private SystemRoleService systemRoleService;
  @Autowired
  private SystemUserService systemUserService;
  @PostMapping
  public R create(@RequestBody SystemRoleCreateDto dto) {
    if (StrUtil.isEmpty(dto.getRoleName()) || StrUtil.isEmpty(dto.getRoleCode()))
      throw new ServiceException(ResultCode.ValidateError);
    SystemRole systemRole = BeanUtil.toBean(dto, SystemRole.class);
    systemRole.setCreateTime(new Date().getTime());
    systemRole.setUpdateTime(new Date().getTime());
    boolean save = systemRoleService.save(systemRole);
    if (!save)
      throw new ServiceException(ResultCode.SaveError);
    return R.ok();
  }
  
  @DeleteMapping
  public R del(String id) {
    Long count = systemUserService.lambdaQuery().eq(SystemUser::getRoleId, id).count();
    if(count >0) throw new ServiceException(ResultCode.DeleteError,"存在此角色用户，不可删除");
    boolean result = systemRoleService.removeById(id);
    if (!result) throw new ServiceException(ResultCode.DeleteError);
    return R.ok();
  }
  @PutMapping
  public R updateRole(@RequestBody SystemRoleUpdateDto dto){
    if(StrUtil.isEmpty(dto.getId())) throw new ServiceException(ResultCode.ValidateError);
    SystemRole systemRole = BeanUtil.toBean(dto, SystemRole.class);
    boolean update = systemRoleService.updateById(systemRole);
    if (!update) throw new ServiceException(ResultCode.UpdateError);
    return R.ok();
  }
  @GetMapping("/page")
  public R<Page> getRolePageList(SystemRolePageDto rolePageDto) {
    Page page = systemRoleService.getRolePageList(rolePageDto);
    return R.ok(page);
  }
  @GetMapping("/list")
  public R<List<SystemRole>> getRoleList(){
    List<SystemRole> list = systemRoleService.lambdaQuery().list();
    return R.ok(list);
  }
}
