package com.example.reggie.service;

import com.example.reggie.dto.DishDto;
import com.example.reggie.entity.Dish;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 菜品管理 服务类
 * </p>
 *
 * @author baomidou
 * @since 2022-08-06
 */
public interface IDishService extends IService<Dish> {

   void saveWithFlavor(DishDto dishDto);

    void updateWithFlavor(DishDto dishDto);

    DishDto getByIdWithFlavor(Long id);
}
