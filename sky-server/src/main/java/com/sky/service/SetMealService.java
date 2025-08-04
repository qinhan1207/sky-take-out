package com.sky.service;

import com.sky.dto.SetmealDTO;

public interface SetMealService {

    /**
     * 添加套餐及其套餐包含的菜品
     */
    void saveWithDish(SetmealDTO setmealDTO);
}
