package com.example.reggie.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.reggie.common.R;
import com.example.reggie.entity.Employee;
import com.example.reggie.mapper.EmployeeMapper;
import com.example.reggie.service.IEmployeeService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import org.springframework.stereotype.Controller;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * 员工信息 前端控制器
 * </p>
 *
 * @author baomidou
 * @since 2022-08-06
 */
@RestController
@RequestMapping("/employee")
public class EmployeeController {
    @Autowired
    private IEmployeeService iEmployeeService;

    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee){

        //1、将页面提交的密码password进行md5加密处理
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        //2、根据页面提交的用户名username查询数据库
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername,employee.getUsername());
        Employee emp = iEmployeeService.getOne(queryWrapper);

        //3、如果没有查询到则返回登录失败结果
        if(emp == null){
            return R.error("登录失败");
        }

        //4、密码比对，如果不一致则返回登录失败结果
        if(!emp.getPassword().equals(password)){
            return R.error("登录失败");
        }

        //5、查看员工状态，如果为已禁用状态，则返回员工已禁用结果
        if(emp.getStatus() == 0){
            return R.error("账号已禁用");
        }

        //6、登录成功，将员工id存入Session并返回登录成功结果
        request.getSession().setAttribute("employee",emp.getId());
        return R.success(emp);

        //小结：面向对象编程，查对象，方便操作

    }

    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request){
           request.getSession().removeAttribute("employee");
           return R.success("退出成功！");
    }

    @GetMapping("/page")
    public R<Page> getList(Integer page,Integer pageSize,String name){
        Page pageInfo = new Page(page,pageSize);;
        //加入条件，name不为空时查找
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.like(StringUtils.isNotEmpty(name),Employee::getName,name);
        //按跟新时间排序
        queryWrapper.orderByDesc(Employee::getUpdateTime);
        //结果放在pageInfo里面
        iEmployeeService.page(pageInfo,queryWrapper);
        //返回page对象
        return R.success(pageInfo);

    }



    @PostMapping
    public R<String> add(@RequestBody Employee employee){
        //设置初始密码123456，需要进行md5加密处理
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
        iEmployeeService.save(employee);
        return R.success("新增员工成功");
    }

    @PutMapping
    public R<String> update(@RequestBody Employee employee){
        iEmployeeService.updateById(employee);
        return R.success("员工信息修改成功");
    }

    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id){
        Employee employee = iEmployeeService.getById(id);
        if(employee != null){
            return R.success(employee);
        }
        return R.error("没有查询到对应员工信息");
    }













}
