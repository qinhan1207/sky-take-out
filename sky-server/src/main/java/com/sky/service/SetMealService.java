package com.sky.service;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.vo.SetmealVO;

public interface SetMealService {

    /**
     * 添加套餐及其套餐包含的菜品
     */
    void saveWithDish(SetmealDTO setmealDTO);

    /**
     * 套餐分页查询
     * @param setmealPageQueryDTO
     * @return
     */
    PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO);

    /**
     * 根据id查询套餐及其对应的菜品
     * @param id
     * @return
     */
    SetmealVO queryByIdWithDishes(Long id);

    /**
     * 套餐的起售停售
     * @param status
     * @param id
     */
    void startOrStop(Integer status, Long id);

    /**
     * 修改套餐信息
     * @param setmealDTO
     */
    void updateWithDishes(SetmealDTO setmealDTO);
}
