package com.sky.controller.user;

import com.sky.constant.StatusConstant;
import com.sky.entity.Setmeal;
import com.sky.result.Result;
import com.sky.service.SetMealService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("userSetMealController")
@RequestMapping("/user/setmeal")
@Api(tags = "C端-套餐相关接口")
@Slf4j
public class SetMealController {

    @Autowired
    private SetMealService setMealService;
    /**
     * 根据categoryId查询起售中的套餐
     * @param categoryId
     * @return
     */
    @GetMapping("/list")
    @ApiOperation("根据categoryId查询起售中的套餐")
    public Result<List<Setmeal>> list(Long categoryId){
        log.info("根据categoryId查询起售中的套餐:{}",categoryId);
        Setmeal setmeal = new Setmeal();
        setmeal.setCategoryId(categoryId);
        setmeal.setStatus(StatusConstant.ENABLE);
        List<Setmeal> setmeals = setMealService.list(setmeal);
        return Result.success(setmeals);
    }
}
