package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.exception.SetmealEnableFailedException;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetMealDishMapper;
import com.sky.mapper.SetMealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetMealService;
import com.sky.vo.SetmealVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class SetMealServiceImpl implements SetMealService {

    @Autowired
    private SetMealDishMapper setMealDishMapper;
    @Autowired
    private SetMealMapper setMealMapper;
    @Autowired
    private DishMapper dishMapper;


    /**
     * 添加套餐及其套餐包含的菜品
     */
    @Override
    @Transactional
    public void saveWithDish(SetmealDTO setmealDTO) {
        // 添加套餐基本信息
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        setMealMapper.addSetMeal(setmeal);
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
     *
     * @param setmealPageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO) {
        PageHelper.startPage(setmealPageQueryDTO.getPage(), setmealPageQueryDTO.getPageSize());

        Page<SetmealVO> page = setMealMapper.pageQuery(setmealPageQueryDTO);

        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 根据id查询套餐及其对应的菜品
     *
     * @param id
     * @return
     */
    @Override
    public SetmealVO queryByIdWithDishes(Long id) {
        // 根据id查询套餐的基本信息
        Setmeal setmeal = setMealMapper.getById(id);
        // 根据套餐id查询对应的菜品信息
        List<SetmealDish> dishes = setMealDishMapper.getDishesBySetMealId(id);
        // 封装VO
        SetmealVO setmealVO = new SetmealVO();
        BeanUtils.copyProperties(setmeal, setmealVO);
        setmealVO.setSetmealDishes(dishes);
        return setmealVO;
    }

    /**
     * 套餐的起售停售
     *
     * @param status
     * @param id
     */
    @Override
    public void startOrStop(Integer status, Long id) {

        // 进行判断，如果套餐包含的菜品处于停售状态则不能起售
        if (status.equals(StatusConstant.ENABLE)) {
            List<Dish> dishes = dishMapper.getDishesBySetMealId(id);
            dishes.forEach((dish) -> {
                if (dish.getStatus().equals(StatusConstant.DISABLE)) {
                    throw new SetmealEnableFailedException(MessageConstant.SETMEAL_ENABLE_FAILED);
                }
            });
        }
        Setmeal setmeal = Setmeal.builder()
                .id(id)
                .status(status)
                .build();
        setMealMapper.update(setmeal);
    }

    /**
     * 修改套餐信息
     * 1.修改套餐基本信息
     * 2.修改套餐对应的菜品
     *
     * @param setmealDTO
     */
    @Override
    @Transactional
    public void updateWithDishes(SetmealDTO setmealDTO) {
        // 1.修改套餐的基本信息
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        setMealMapper.update(setmeal);
        // 2.修改套餐对应的菜品信息，采用先删除，后增加的方式
        Long setMealId = setmealDTO.getId();
        List<Long> setMealIds = new ArrayList<>();
        setMealIds.add(setMealId);
        // 根据套餐id删除套餐表所的菜品
        setMealDishMapper.deleteBySetMealId(setMealIds);
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        if (!CollectionUtils.isEmpty(setmealDishes)) {
            setmealDishes.forEach((setmealDish) -> {
                setmealDish.setSetmealId(setMealId);
            });
            setMealDishMapper.addWithDish(setmealDishes);
        }
    }

    /**
     * 批量删除套餐及其对应的菜品，起售中的套餐不能删除
     * @param ids
     */
    @Override
    @Transactional
    public void deleteBatch(List<Long> ids) {
        // 起售的套餐不允许删除
        ids.forEach((id)->{
            Setmeal setmeal = setMealMapper.getById(id);
            if (setmeal.getStatus().equals(StatusConstant.ENABLE)){
                throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ON_SALE);
            }
        });
        // 删除套餐基本信息
        setMealMapper.deleteById(ids);
        // 删除与套餐所绑定的菜品
        setMealDishMapper.deleteBySetMealId(ids);
    }

    /**
     * C端-根据categoryId查询起售中的套餐
     * @param setmeal
     * @return
     */
    @Override
    public List<Setmeal> list(Setmeal setmeal) {
        return setMealMapper.list(setmeal);
    }
}
