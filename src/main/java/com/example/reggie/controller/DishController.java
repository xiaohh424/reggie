package com.example.reggie.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.reggie.common.R;
import com.example.reggie.dto.DishDto;
import com.example.reggie.entity.*;
import com.example.reggie.service.DishFlavorService;
import com.example.reggie.service.ICategoryService;
import com.example.reggie.service.IDishService;
import com.example.reggie.service.SetmealDishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * <p>
 * 菜品管理 前端控制器
 * </p>
 *
 * @author baomidou
 * @since 2022-08-06
 */
@RestController
@RequestMapping("/dish")
public class DishController {
    @Autowired
    private IDishService dishService;

    @Autowired
    private ICategoryService categoryService;

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private SetmealDishService setmealDishService;

    @Autowired
    private RedisTemplate redisTemplate;

    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto){

        dishService.saveWithFlavor(dishDto);
        //清理某个分类下面的菜品缓存数据
        String key = "dish_" + dishDto.getCategoryId() + "_1";
        redisTemplate.delete(key);
        return R.success("新增菜品成功");
    }


    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name){



        //构造分页构造器对象
        Page<Dish> pageInfo = new Page<>(page,pageSize);
        Page<DishDto> dishDtoPage = new Page<>();

        //条件构造器
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        //添加过滤条件
        queryWrapper.like(name != null,Dish::getName,name);
        //添加排序条件
        queryWrapper.orderByDesc(Dish::getUpdateTime);

        //执行分页查询
        dishService.page(pageInfo,queryWrapper);

        //对象拷贝，除了页面内容不拷贝，因为pageInfo没有菜品总类属性
        BeanUtils.copyProperties(pageInfo,dishDtoPage,"records");

        List<DishDto> dishDtos = new ArrayList<>();
        List<Dish> records = pageInfo.getRecords();
        for(Dish record: records){
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(record,dishDto);
            Long cid = record.getCategoryId();
            Category category = categoryService.getById(cid);
            if(category!=null){
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
                dishDtos.add(dishDto);
            }

        }

        dishDtoPage.setRecords(dishDtos);

        return R.success(dishDtoPage);
    }

    @GetMapping("/{id}")
    public R<DishDto> get(@PathVariable Long id){

        DishDto dishDto = dishService.getByIdWithFlavor(id);

        return R.success(dishDto);
    }



    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto){

        dishService.updateWithFlavor(dishDto);
        //清理某个分类下面的菜品缓存数据
        String key = "dish_" + dishDto.getCategoryId() + "_1";
        redisTemplate.delete(key);

        return R.success("修改菜品成功");
    }

    @PostMapping("/status/{status}")
    public R<String> setStatus(@PathVariable Integer status,Long[] ids){
        for(Long id: ids){
            Dish dish = dishService.getById(id);
            dish.setStatus(status);
            dishService.updateById(dish);
        }
        return R.success("修改成功！");
    }

    @GetMapping("/list")
    public R<List<DishDto>> list(Dish dish){


        List<DishDto> dishDtoList = null;

        //动态构造key
        String key = "dish_" + dish.getCategoryId() + "_" + dish.getStatus();//dish_1397844391040167938_1

        //先从redis中获取缓存数据
        dishDtoList = (List<DishDto>) redisTemplate.opsForValue().get(key);

        if(dishDtoList != null){
            //如果存在，直接返回，无需查询数据库
            return R.success(dishDtoList);
        }

        //构造查询条件
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(dish.getCategoryId() != null ,Dish::getCategoryId,dish.getCategoryId());
        //添加条件，查询状态为1（起售状态）的菜品
        queryWrapper.eq(Dish::getStatus,1);

        //添加排序条件
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);

        List<Dish> list = dishService.list(queryWrapper);

        dishDtoList = list.stream().map((item) -> {
            DishDto dishDto = new DishDto();

            BeanUtils.copyProperties(item,dishDto);

            Long categoryId = item.getCategoryId();//分类id
            //根据id查询分类对象
            Category category = categoryService.getById(categoryId);

            if(category != null){
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }

            //当前菜品的id
            Long dishId = item.getId();
            LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(DishFlavor::getDishId,dishId);
            //SQL:select * from dish_flavor where dish_id = ?
            List<DishFlavor> dishFlavorList = dishFlavorService.list(lambdaQueryWrapper);
            dishDto.setFlavors(dishFlavorList);
            return dishDto;
        }).collect(Collectors.toList());

        //如果不存在，需要查询数据库，将查询到的菜品数据缓存到Redis
        redisTemplate.opsForValue().set(key,dishDtoList,60, TimeUnit.MINUTES);

        return R.success(dishDtoList);
    }


    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids){

        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.in(Dish::getId,ids);
        queryWrapper.eq(Dish::getStatus,1);
        int count = dishService.count(queryWrapper);
        if(count > 0){
            return  R.error("菜品正在售卖中，不能删除");
        }
        dishService.removeByIds(ids);

        LambdaQueryWrapper<SetmealDish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(SetmealDish::getDishId,ids); //in方法可批量删除
        setmealDishService.remove(lambdaQueryWrapper);

        return R.success("菜品删除成功");
    }




}
