package com.mmall.dao;

import com.mmall.pojo.Order;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface OrderMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Order record);

    int insertSelective(Order record);

    Order selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Order record);

    int updateByPrimaryKey(Order record);

    Order selectByUseridAndOrderno(@Param("userid") Integer userid, @Param("orderno") Long orderno);

    Order selectByOrderno(Long orderNo);

    List<Order> selectOrderListByUserId(@Param("userid") Integer userid);

    List<Order> selectAllOrder();

    Order searchByOrderNo(String orderNo);

}