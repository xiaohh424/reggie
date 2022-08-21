package com.example.reggie.dto;


import com.example.reggie.entity.Dish;
import com.example.reggie.entity.DishFlavor;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;


//此类为接收前端数据类，因为前端的数据包含两个表的内容，无法用requestbody接收,因此扩展三个三个属性
@Data
public class DishDto extends Dish {

    //菜品对应的口味数据
    private List<DishFlavor> flavors = new ArrayList<>();

    private String categoryName;

    private Integer copies;
}
