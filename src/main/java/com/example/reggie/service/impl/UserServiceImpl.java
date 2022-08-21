package com.example.reggie.service.impl;

import com.example.reggie.entity.User;
import com.example.reggie.mapper.UserMapper;
import com.example.reggie.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户信息 服务实现类
 * </p>
 *
 * @author baomidou
 * @since 2022-08-06
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

}
