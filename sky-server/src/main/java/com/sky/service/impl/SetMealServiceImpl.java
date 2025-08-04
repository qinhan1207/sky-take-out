package com.sky.service.impl;

import com.sky.dto.SetmealDTO;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.mapper.SetMealDishMapper;
import com.sky.mapper.SetMealMapper;
import com.sky.service.SetMealService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
public class SetMealServiceImpl implements SetMealService {

    private final SetMealMapper setmealMapper;
    private final SetMealDishMapper setMealDishMapper;

    public SetMealServiceImpl(SetMealMapper setmealMapper, SetMealDishMapper setMealDishMapper) {
        this.setmealMapper = setmealMapper;
        this.setMealDishMapper = setMealDishMapper;
    }

    /**
     * 添加套餐及其套餐包含的菜品
     */
    @Override
    @Transactional
    public void saveWithDish(SetmealDTO setmealDTO) {
        // 添加套餐基本信息
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        setmealMapper.addSetMeal(setmeal);
        Long setmealId = setmeal.getId();
        // 添加套餐对应的菜品
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        if (!CollectionUtils.isEmpty(setmealDishes)) {
            setmealDishes.forEach(setmealDish -> setmealDish.setSetmealId(setmealId));
            setMealDishMapper.addWithDish(setmealDishes);
        }
    }
}
