package com.sgq.market.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sgq.market.entity.SystemRole;
import com.sgq.market.model.dto.SystemRolePageDto;

/**
 * @author sgq
 * @version 1.0
 * @description TODO
 * @date 2024/2/19 20:21
 */
public interface SystemRoleService extends IService<SystemRole> {
  Page getRolePageList(SystemRolePageDto rolePageDto);
}
