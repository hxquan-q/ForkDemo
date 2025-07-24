package com.hxq.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hxq.order.entity.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 订单Mapper接口
 */
@Mapper
public interface OrderMapper extends BaseMapper<Order> {

    /**
     * 查询用户订单列表
     *
     * @param userId 用户ID
     * @return 订单列表
     */
    @Select("SELECT * FROM t_order WHERE user_id = #{userId} ORDER BY create_time DESC")
    List<Order> selectByUserId(@Param("userId") Long userId);

    /**
     * 更新订单状态
     *
     * @param id     订单ID
     * @param status 状态
     * @return 影响行数
     */
    @Update("UPDATE t_order SET status = #{status} WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);

    /**
     * 查询是否存在用户购买过的商品
     *
     * @param userId    用户ID
     * @param productId 商品ID
     * @return 订单数量
     */
    @Select("SELECT COUNT(*) FROM t_order WHERE user_id = #{userId} AND product_id = #{productId}")
    int countUserProduct(@Param("userId") Long userId, @Param("productId") Long productId);

    /**
     * 查询超时未支付的订单
     *
     * @param status     订单状态
     * @param timeoutMin 超时分钟数
     * @return 订单列表
     */
    @Select("SELECT * FROM t_order WHERE status = #{status} AND DATE_ADD(create_time, INTERVAL #{timeoutMin} MINUTE) < NOW()")
    List<Order> selectTimeoutOrders(@Param("status") Integer status, @Param("timeoutMin") Integer timeoutMin);
}