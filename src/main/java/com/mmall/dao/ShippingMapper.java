package com.mmall.dao;

import com.mmall.pojo.Shipping;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ShippingMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Shipping record);

    int insertSelective(Shipping record);

    Shipping selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Shipping record);

    int updateByPrimaryKey(Shipping record);

    int deleteByUseridShippingid(@Param("userid") Integer userid, @Param("shippingid") Integer shippingid);

    int updateShipping(Shipping record);

    Shipping selectByUseridShippingid(@Param("userid") Integer userid, @Param("shippingid") Integer shippingid);

    List<Shipping> selectListByUserid(Integer userid);
}