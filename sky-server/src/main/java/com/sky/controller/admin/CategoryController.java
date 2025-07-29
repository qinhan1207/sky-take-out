package com.sky.controller.admin;

import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.CategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/category")
@Api(tags = "分类相关接口")
@Slf4j
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 分页查询
     * @param categoryPageQueryDTO
     * @return
     */
    @GetMapping("/page")
    @ApiOperation("分类分页查询")
    public Result<PageResult> page(CategoryPageQueryDTO categoryPageQueryDTO){
        log.info("分页查询,{}",categoryPageQueryDTO);
        PageResult pageResult = categoryService.pageQuery(categoryPageQueryDTO);
        return Result.success(pageResult);
    }


    /**
     * 新增分类
     */
    @PostMapping
    @ApiOperation("新增分类")
    public Result addCategory(@RequestBody CategoryDTO categoryDTO){
        log.info("添加分类:{}",categoryDTO);
        categoryService.addCategory(categoryDTO);
        return Result.success();
    }

    /**
     * 启用禁用
     */
    @PostMapping("status/{status}")
    public Result startOrStop(@PathVariable Integer status,Long id){
        log.info("启用或禁用分类id:{},status:{}",id,status);
        categoryService.startOrStop(status,id);
        return Result.success();
    }

    /**
     * 修改分类
     */
    @PutMapping
    public Result edit(@RequestBody CategoryDTO categoryDTO){
        log.info("修改分类信息:{}",categoryDTO);
        categoryService.edit(categoryDTO);
        return Result.success();
    }

    /**
     * 分解id删除分类
     */
    @DeleteMapping
    @ApiOperation("根据id删除分类")
    public Result remove(Long id){
        log.info("根据id删除分类：{}",id);
        categoryService.remove(id);
        return Result.success();
    }

    /**
     * 根据类型查询分类
     * @param type
     */
    @GetMapping("/list")
    @ApiOperation("根据类型查询分类")
    public Result<List<Category>> queryByType(Integer type){
        log.info("根据类型查询分类:{}",type);
        List<Category> list = categoryService.queryByType(type);
        return Result.success(list);
    }
}
