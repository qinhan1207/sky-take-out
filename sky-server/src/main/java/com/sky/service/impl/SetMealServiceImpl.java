package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.mapper.SetMealDishMapper;
import com.sky.mapper.SetMealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetMealService;
import com.sky.vo.SetmealVO;
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

    /**
     * 套餐分页查询
     * @param setmealPageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO) {
        PageHelper.startPage(setmealPageQueryDTO.getPage(),setmealPageQueryDTO.getPageSize());

        Page<SetmealVO> page = setmealMapper.pageQuery(setmealPageQueryDTO);

        return new PageResult(page.getTotal(),page.getResult());
    }

    /**
     * 根据id查询套餐及其对应的菜品
     * @param id
     * @return
     */
    @Override
    public SetmealVO queryByIdWithDishes(Long id) {
        // 根据id查询套餐的基本信息
        Setmeal setmeal = setmealMapper.getById(id);
        // 根据套餐id查询对应的菜品信息
        List<SetmealDish> dishes = setMealDishMapper.getDishesBySetMealId(id);
        // 封装VO
        SetmealVO setmealVO = new SetmealVO();
        BeanUtils.copyProperties(setmeal,setmealVO);
        setmealVO.setSetmealDishes(dishes);
        return setmealVO;
    }
}
