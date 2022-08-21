package com.example.reggie.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.reggie.common.R;
import com.example.reggie.entity.Category;
import com.example.reggie.entity.Employee;
import com.example.reggie.service.ICategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.springframework.stereotype.Controller;

import java.util.List;

/**
 * <p>
 * 菜品及套餐分类 前端控制器
 * </p>
 *
 * @author baomidou
 * @since 2022-08-06
 */
@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private ICategoryService iCategoryService;

    @GetMapping("/page")
    public R<Page> getList(Integer page, Integer pageSize){
        Page pageInfo = new Page(page,pageSize);
        //加入条件，name不为空时查找
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper();
        //按跟新时间排序
        queryWrapper.orderByDesc(Category::getUpdateTime);
        //结果放在pageInfo里面
        iCategoryService.page(pageInfo,queryWrapper);
        //返回page对象
        return R.success(pageInfo);

    }

    @GetMapping("/list")
    public R<List<Category>> list(Category category){
        //条件构造器
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        //添加条件
        queryWrapper.eq(category.getType() != null,Category::getType,category.getType());
        //添加排序条件
        queryWrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);

        List<Category> list = iCategoryService.list(queryWrapper);
        return R.success(list);
    }

    @PostMapping
    public R<String> add(@RequestBody Category category){
        iCategoryService.save(category);
        return R.success("新增成功");
    }

    @DeleteMapping
    public R<String> Delete(Long id){
        iCategoryService.remove(id);
        return R.success("删除成功");
    }

    @PutMapping
    public R<String> update(@RequestBody Category category){

        iCategoryService.updateById(category);

        return R.success("修改分类信息成功");
    }

}
