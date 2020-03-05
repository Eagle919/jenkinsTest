package com.caih.cloud.iscs.charge.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.caih.cloud.iscs.charge.mapper.EDeviceMapper;
import com.caih.cloud.iscs.charge.mapper.UserLoginMapper;
import com.caih.cloud.iscs.charge.model.entity.UserLogin;
import com.caih.cloud.iscs.charge.service.UserLoginService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class UserLoginServiceImpl extends ServiceImpl<UserLoginMapper, UserLogin> implements UserLoginService {

    @Resource
    private UserLoginMapper mapper;


}
