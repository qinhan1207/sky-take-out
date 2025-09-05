package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.GoodsSalesDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.Orders;
import com.sky.vo.OrderStatisticsVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface OrderMapper {

    /**
     * 插入订单数据
     * @param orders
     */
    void insert(Orders orders);

    /**
     * 分页查询订单数据
     * @param ordersPageQueryDTO
     * @return
     */
    Page<Orders> pageQuery(OrdersPageQueryDTO ordersPageQueryDTO);

    @Select("select * from orders where id = #{id}")
    Orders getById(Long id);

    /**
     * 根据订单号和用户id查询订单
     * @param orderNumber
     * @param userId
     */
    @Select("select * from orders where number = #{orderNumber} and user_id= #{userId}")
    Orders getByNumberAndUserId(String orderNumber, Long userId);


    /**
     * 更新订单
     * @param orders
     */
    void update(Orders orders);

    /**
     * 根据状态统计订单数量
     * @param status
     * @return
     */
    @Select("select count(*) from orders where status = #{status}")
    Integer getNumByStatus(Integer status);

    /**
     * 根据状态和下单时间查询订单
     * @param status
     * @param orderTime
     * @return
     */
    @Select("select * from orders where status = #{status} and order_time < #{orderTime}")
    List<Orders> getByStatusAndOrderTimeLT(Integer status, LocalDateTime orderTime);

    /**
     * 根据动态条件统计营业额数据
     * @param map
     * @return
     */
    Double sumByMap(Map map);

    /**
     * 根据动态条件统计订单数据
     * @param map
     * @return
     */
    Integer countOrderByMap(Map map);

    /**
     * 统计指定时间区间内的销量排名前10
     * @param begin
     * @param end
     * @return
     */
    List<GoodsSalesDTO> getSalesTop10(LocalDateTime begin,LocalDateTime end);
}
