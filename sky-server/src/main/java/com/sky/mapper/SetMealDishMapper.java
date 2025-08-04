package com.sky.mapper;

import com.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetMealDishMapper {

    /**
     * 根据菜品id来查询对应的套餐id
     * @param dishIds
     * @return
     */
    List<Long> getSetMealIdsByDishIds(List<Long> dishIds);

    /**
     * 添加套餐对应的菜品信息
     * @param setmealDishes
     */
    void addWithDish(List<SetmealDish> setmealDishes);

    /**
     * 根据套餐id查询对应的菜品
     */
    @Select("select * from setmeal_dish where setmeal_id = #{setMealId}")
    List<SetmealDish> getDishesBySetMealId(Long setMealId);

    /**
     * 根据套餐id删除对应的菜品
     * @param setMealIds
     */
    void deleteBySetMealId(List<Long> setMealIds);
}
