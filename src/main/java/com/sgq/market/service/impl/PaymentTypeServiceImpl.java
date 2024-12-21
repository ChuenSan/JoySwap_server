package com.sgq.market.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sgq.market.entity.PaymentType;
import com.sgq.market.mapper.PaymentTypeMapper;
import com.sgq.market.service.PaymentTypeService;
import org.springframework.stereotype.Service;

@Service
public class PaymentTypeServiceImpl extends ServiceImpl<PaymentTypeMapper, PaymentType> implements PaymentTypeService {
}
