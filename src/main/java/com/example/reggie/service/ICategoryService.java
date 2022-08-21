package com.example.reggie.service;

import com.example.reggie.entity.Category;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 菜品及套餐分类 服务类
 * </p>
 *
 * @author baomidou
 * @since 2022-08-06
 */
public interface ICategoryService extends IService<Category> {
    public void remove(Long id);
}
