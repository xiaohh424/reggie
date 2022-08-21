package com.example.reggie.service.impl;

import com.example.reggie.entity.Employee;
import com.example.reggie.mapper.EmployeeMapper;
import com.example.reggie.service.IEmployeeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 员工信息 服务实现类
 * </p>
 *
 * @author baomidou
 * @since 2022-08-06
 */
@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements IEmployeeService {

}
