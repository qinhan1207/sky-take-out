package com.sky.service.impl;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.WorkspaceService;
import com.sky.vo.BusinessDataVO;
import com.sky.vo.OrderOverViewVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class WorkspaceServiceImpl implements WorkspaceService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private UserMapper userMapper;


    /**
     * 查询今日运营数据
     *
     * @return
     */
    @Override
    public BusinessDataVO businessData() {
        // 定义一个LocalDateTime类型的begin和end分别表示今天一天中最早的时间和最晚的时间
        LocalDateTime begin = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime end = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);

        Map map = new HashMap();
        map.put("begin", begin);
        map.put("end", end);

        Double turnover = 0.0;

        Integer totalOrderCount = 0;

        Double unitPrice = 0.0;

        // 总订单数
        totalOrderCount = orderMapper.countOrderByMap(map);

        // 新增用户数
        Integer newUsers = userMapper.countByMap(map);

        map.put("status", Orders.COMPLETED);

        // 营业额
        turnover = orderMapper.sumByMap(map);

        // 有效订单数
        Integer validOrderCount = orderMapper.countOrderByMap(map);

        // 订单完成率
        Double orderCompletionRate = 0.0;
        if (totalOrderCount != 0) {
            orderCompletionRate = Double.valueOf(validOrderCount) / totalOrderCount;
        }

        // 平均客户单价
        if (validOrderCount != 0) {
            unitPrice = turnover / validOrderCount;
        }

        return BusinessDataVO
                .builder()
                .turnover(turnover)
                .validOrderCount(validOrderCount)
                .orderCompletionRate(orderCompletionRate)
                .unitPrice(unitPrice)
                .newUsers(newUsers)
                .build();

    }

    /**
     * 查询今日订单管理数据
     * @return
     */
    @Override
    public OrderOverViewVO overviewOrders() {

        LocalDateTime begin = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime end = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);

        Map map = new HashMap();
        map.put("begin",begin);
        map.put("end",end);
        // 全部订单
        Integer allOrders = orderMapper.countOrderByMap(map);
        // 待接单
        map.put("status",Orders.TO_BE_CONFIRMED);
        Integer waitingOrders = orderMapper.countOrderByMap(map);

        // 待派送
        map.put("status",Orders.CONFIRMED);
        Integer deliveredOrders = orderMapper.countOrderByMap(map);

        // 已完成
        map.put("status",Orders.COMPLETED);
        Integer completedOrders = orderMapper.countOrderByMap(map);

        // 以取消
        map.put("status",Orders.CANCELLED);
        Integer cancelledOrders = orderMapper.countOrderByMap(map);

        return OrderOverViewVO
                .builder()
                .waitingOrders(waitingOrders)
                .deliveredOrders(deliveredOrders)
                .completedOrders(completedOrders)
                .cancelledOrders(cancelledOrders)
                .allOrders(allOrders)
                .build();
    }
}
