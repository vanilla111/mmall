package com.mmall.dao;

import com.mmall.pojo.Cart;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CartMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Cart record);

    int insertSelective(Cart record);

    Cart selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Cart record);

    int updateByPrimaryKey(Cart record);

    Cart selectCartByUserIdProductId(@Param("userId") Integer userId, @Param("productId") Integer productId);

    List<Cart> selectCartByUserId(Integer userid);

    int selectCartProductCheckedStatus(Integer userId);

    int deleteByUseridAndProductIds(@Param("userid") Integer userid, @Param("productidList") List<String> productidList);

    int checkedOrUncheckedProduct(@Param("userid") Integer userid, @Param("checked") Integer checked, @Param("productId") Integer productId);

    int selectCartProductCount(Integer userid);

    List<Cart> selectCheckedCartByUserid(Integer userid);
}