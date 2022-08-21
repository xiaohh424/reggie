package com.example.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.reggie.common.BaseContext;
import com.example.reggie.common.R;
import com.example.reggie.dto.OrderDetailDto;
import com.example.reggie.dto.SetmealDto;
import com.example.reggie.entity.*;
import com.example.reggie.service.IDishService;
import com.example.reggie.service.IOrdersService;
import com.example.reggie.service.IUserService;
import com.example.reggie.service.OrderDetailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 订单
 */
@Slf4j
@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private IOrdersService orderService;

    @Autowired
    private OrderDetailService orderDetailService;

    @Autowired
    private IUserService userService;

    /**
     * 用户下单
     * @param orders
     * @return
     */
    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders){
        log.info("订单数据：{}",orders);
        orderService.submit(orders);
        return R.success("下单成功");
    }

    @GetMapping("/userPage")
    public R<Page> page(int page,int pageSize){
        //分页构造器对象
        Page<Orders> pageInfo = new Page<>(page,pageSize);
        Page<OrderDetailDto> dtoPage = new Page<>();

        orderService.page(pageInfo);

        //对象拷贝
        BeanUtils.copyProperties(pageInfo,dtoPage,"records");
        List<Orders> records = pageInfo.getRecords();
        List<OrderDetailDto> list = new ArrayList<>();
        for(Orders record : records){
            OrderDetailDto orderDetailDto = new OrderDetailDto();
            BeanUtils.copyProperties(record, orderDetailDto);
            LambdaQueryWrapper<OrderDetail> queryWrapper1 = new LambdaQueryWrapper();
            queryWrapper1.eq(OrderDetail::getOrderId,record.getId());
            orderDetailDto.setOrderDetails(orderDetailService.list(queryWrapper1));
            LambdaQueryWrapper<User> queryWrapper2 = new LambdaQueryWrapper();
            queryWrapper2.eq(User::getId,record.getUserId());
            orderDetailDto.setUserName(userService.getOne(queryWrapper2).getName());
            list.add(orderDetailDto);
        }

        dtoPage.setRecords(list);
        return R.success(dtoPage);
    }


    @PutMapping
    public R<String> setStatus(@RequestBody Orders orders){
        orderService.updateById(orders);
        return R.success("修改成功！");
    }

}