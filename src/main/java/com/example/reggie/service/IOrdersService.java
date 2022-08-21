package com.example.reggie.service;

import com.example.reggie.entity.Orders;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 订单表 服务类
 * </p>
 *
 * @author baomidou
 * @since 2022-08-06
 */
public interface IOrdersService extends IService<Orders> {

    /**
     * 用户下单
     * @param orders
     */
    public void submit(Orders orders);
}
