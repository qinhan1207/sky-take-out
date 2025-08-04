package com.sky.controller.admin;

import com.github.pagehelper.Page;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SetMealService;
import com.sky.vo.SetmealVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/setmeal")
@Slf4j
@Api(tags = "套餐相关接口")
public class SetMealController {

    @Autowired
    private SetMealService setMealService;

    @PostMapping
    @ApiOperation("添加套餐")
    public Result save(@RequestBody SetmealDTO setmealDTO){
        log.info("添加套餐及其对应的菜品:{}",setmealDTO);
        setMealService.saveWithDish(setmealDTO);
        return Result.success();
    }

    /**
     * 套餐分页查询
     * @param setmealPageQueryDTO
     * @return
     */
    @GetMapping("/page")
    @ApiOperation("套餐分页查询")
    public Result<PageResult> page(SetmealPageQueryDTO setmealPageQueryDTO){
        log.info("套餐分页查询：{}",setmealPageQueryDTO);
        PageResult pageResult = setMealService.pageQuery(setmealPageQueryDTO);
        return Result.success(pageResult);
    }

    /***
     * 根据id查询套餐及其对应的菜品信息
     * @return
     */
    @GetMapping("/{id}")
    @ApiOperation("根据id查询套餐及其对应的菜品信息")
    public Result<SetmealVO> queryById(@PathVariable Long id){
        log.info("根据id查询套餐及其对应的菜品信息:{}",id);
        SetmealVO setmealVO = setMealService.queryByIdWithDishes(id);
        return Result.success(setmealVO);
    }

    /**
     * 套餐的起售停售
     * @param status
     * @param id
     * @return
     */
    @PostMapping("/status/{status}")
    @ApiOperation("套餐的起售停售")
    public Result startOrStop(@PathVariable Integer status,Long id){
        log.info("套餐的起售停售id:{},status:{}",id,status);
        setMealService.startOrStop(status,id);
        return Result.success();
    }
}
