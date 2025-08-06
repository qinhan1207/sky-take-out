package com.sky.service.impl;

import com.github.pagehelper.Constant;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.entity.Setmeal;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetMealDishMapper;
import com.sky.mapper.SetMealMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class DishServiceImpl implements DishService {

    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private DishFlavorMapper dishFlavorMapper;
    @Autowired
    private SetMealDishMapper setMealDishMapper;
    @Autowired
    private SetMealMapper setMealMapper;

    /**
     * 新增菜品和对应的口味
     *
     * @param dishDTO
     */
    @Override
    @Transactional
    public void saveWithFlavor(DishDTO dishDTO) {

        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);

        // 向菜品表添加一条数据
        dishMapper.insert(dish);

        // 获取insert语句生成的主键值
        Long dishId = dish.getId();

        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors != null && !flavors.isEmpty()) {
            flavors.forEach((flavor) -> flavor.setDishId(dishId));
            // 向口味表插入n条数据
            dishFlavorMapper.insertBatch(flavors);
        }

    }

    /**
     * 菜品分页查询
     *
     * @param dishPageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());
        Page<DishVO> dishes = dishMapper.pageQuery(dishPageQueryDTO);
        return new PageResult(dishes.getTotal(), dishes.getResult());
    }

    /**
     * 菜品批量删除
     *
     * @param ids
     */
    @Override
    @Transactional
    public void deleteBatch(List<Long> ids) {
        // 判断当前菜品是否能够删除--是否存在起售中的菜品？
        for (Long id : ids) {
            Dish dish = dishMapper.getById(id);
            if (dish.getStatus() == StatusConstant.ENABLE) {
                // 当前菜品处于起售中，不能删除
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        }

        // 判断当前菜品是否能够删除--是否被套餐关联？
        List<Long> setMealIds = setMealDishMapper.getSetMealIdsByDishIds(ids);
        if (setMealIds != null && !setMealIds.isEmpty()) {
            // 当前菜品被套餐关联了，不能删除
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }

        // 根据菜品id集合批量删除菜品数据
        dishMapper.deleteByIds(ids);
        // 根据菜品id集合批量删除关联的口味数据
        dishFlavorMapper.deleteByDishIds(ids);

    }

    /**
     * 根据id查询菜品和对应的口味
     *
     * @param id
     * @return
     */
    @Override
    public DishVO getByIdWithFlavor(Long id) {
        // 根据id查询菜品数据
        Dish dish = dishMapper.getById(id);

        // 根据dish_id查询口味数据
        List<DishFlavor> dishFlavors = dishFlavorMapper.getByDishId(id);

        // 将查询到的数据封装到dishVo
        DishVO dishVO = new DishVO();
        BeanUtils.copyProperties(dish, dishVO);
        dishVO.setFlavors(dishFlavors);

        return dishVO;
    }

    /**
     * 根据id修改菜品基本信息和口味信息
     *
     * @param dishDTO
     */
    @Override
    public void updateWithFlavor(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);

        // 修改菜品表基本信息
        dishMapper.update(dish);

        // 删除原有的口味数据
        List<Long> dishIds = new ArrayList<>();
        dishIds.add(dishDTO.getId());
        dishFlavorMapper.deleteByDishIds(dishIds);
        // 重新插入口味数据
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors != null && !flavors.isEmpty()) {
            flavors.forEach((flavor) -> {
                flavor.setDishId(dishDTO.getId());
            });
            dishFlavorMapper.insertBatch(flavors);
        }
    }

    /**
     * 根据分类id查询菜品
     *
     * @param categoryId
     * @return
     */
    @Override
    public List<Dish> queryByCategoryId(Long categoryId) {
        return dishMapper.queryByCategoryId(categoryId);
    }

    /**
     * 菜品的起售停售
     * 起售
     * 停售：如果执行停售操作，则包含此菜品的套餐也需要停售
     *
     * @param id
     * @param status
     */
    @Override
    @Transactional
    public void startOrStop(Long id, Integer status) {
        // 创建Dish对象
        Dish dish = Dish.builder()
                .status(status)
                .id(id)
                .build();
        dishMapper.update(dish);
        // 进行判断，如果是起售直接进行需改，如果为停售，则需要停售包含该菜品的套餐
        if (status.equals(StatusConstant.DISABLE)){
            // 判断哪些套餐包含了该菜品，对套餐进行停售，查询套餐菜品表，根据dishId查询setMealId
            List<Long> dishIds = new ArrayList<>();
            dishIds.add(id);
            // 获得包含该菜品的套餐id
            List<Long> setMealIds = setMealDishMapper.getSetMealIdsByDishIds(dishIds);
            // 停掉套餐
            if(!CollectionUtils.isEmpty(setMealIds)){
                for (Long setMealId:setMealIds){
                    Setmeal setmeal = Setmeal.builder()
                            .id(setMealId)
                            .status(StatusConstant.DISABLE)
                            .build();
                    setMealMapper.update(setmeal);
                }
            }
        }


    }

    /**
     * C端-查询所有起售的菜品及其对应的口味
     * @param dish
     * @return
     */
    @Override
    public List<DishVO> listWithFlavors(Dish dish) {
        // 根据category查询所有的菜品
        List<Dish> dishes = dishMapper.queryByCategoryId(dish.getCategoryId());
        List<DishVO> dishVOList = new ArrayList<>();
        // 封装成dishVO
        for (Dish dish1 : dishes) {
            DishVO dishVO = new DishVO();
            BeanUtils.copyProperties(dish1,dishVO);

            // 根据菜品id查询所有对应的口味
            List<DishFlavor> flavors = dishFlavorMapper.getByDishId(dish1.getId());
            dishVO.setFlavors(flavors);
            dishVOList.add(dishVO);
        }
        return dishVOList;
    }

}
