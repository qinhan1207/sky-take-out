package com.sky.service;

import com.sky.vo.BusinessDataVO;
import com.sky.vo.DishOverViewVO;
import com.sky.vo.OrderOverViewVO;
import com.sky.vo.SetmealOverViewVO;

public interface WorkspaceService {

    /**
     * 查询今日运行数据
     * @return
     */
    BusinessDataVO businessData();

    /**
     * 查询今日订单管理数据
     * @return
     */
    OrderOverViewVO overviewOrders();

    /**
     * 查询套餐总览
     * @return
     */
    SetmealOverViewVO overviewSetmeals();

    /**
     * 查询菜品总览
     * @return
     */
    DishOverViewVO overviewDishes();
}
