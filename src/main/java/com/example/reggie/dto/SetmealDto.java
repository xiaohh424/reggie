package com.example.reggie.dto;


import com.example.reggie.entity.Setmeal;
import com.example.reggie.entity.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;  //套餐内菜品

    private String categoryName;  //套餐种类名

}
