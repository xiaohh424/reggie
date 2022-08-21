package com.example.reggie.dto;

import com.example.reggie.entity.OrderDetail;
import com.example.reggie.entity.Orders;
import lombok.Data;

import java.util.List;

@Data
public class OrderDetailDto extends Orders {
    private List<OrderDetail> orderDetails;
    private String userName;
}
