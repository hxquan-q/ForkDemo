package com.hxq.product.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hxq.product.entity.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * 商品Mapper接口
 */
@Mapper
public interface ProductMapper extends BaseMapper<Product> {

    /**
     * 扣减库存
     *
     * @param id       商品ID
     * @param quantity 扣减数量
     * @return 影响行数
     */
    @Update("UPDATE t_product SET stock = stock - #{quantity} WHERE id = #{id} AND stock >= #{quantity}")
    int deductStock(@Param("id") Long id, @Param("quantity") Integer quantity);

    /**
     * 恢复库存
     *
     * @param id       商品ID
     * @param quantity 恢复数量
     * @return 影响行数
     */
    @Update("UPDATE t_product SET stock = stock + #{quantity} WHERE id = #{id}")
    int restoreStock(@Param("id") Long id, @Param("quantity") Integer quantity);

    /**
     * 更新商品秒杀状态
     *
     * @param id     商品ID
     * @param status 状态
     * @return 影响行数
     */
    @Update("UPDATE t_product SET status = #{status} WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);
}