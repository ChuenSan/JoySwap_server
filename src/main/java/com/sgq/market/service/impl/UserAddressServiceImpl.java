package com.sgq.market.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sgq.market.entity.UserAddress;
import com.sgq.market.mapper.UserAddressMapper;
import com.sgq.market.service.UserAddressService;
import org.springframework.stereotype.Service;

@Service
public class UserAddressServiceImpl extends ServiceImpl<UserAddressMapper, UserAddress> implements UserAddressService {
}
