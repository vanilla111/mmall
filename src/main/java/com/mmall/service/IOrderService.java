package com.mmall.service;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ServerResponse;
import com.mmall.vo.OrderVo;

import java.util.Map;

/**
 * Created by wang on 2017/5/23.
 */
public interface IOrderService {
    ServerResponse<Map> pay(Integer userId, String path, Long orderNo);

    ServerResponse alipayCallback(Map<String, String> params);

    ServerResponse queryOrderPayStatus(Integer userId,Long orderNo);

    ServerResponse createOrder(Integer userId, Integer shippingId);

    ServerResponse<String> cancelOrder(Integer userId, Long orderNo);

    ServerResponse getOrderCartProduct(Integer userId);

    ServerResponse<OrderVo> getOrderDetail(Integer userId, Long orderNo);

    ServerResponse<PageInfo> getOrderList(Integer userId, Integer pageNum, Integer pageSize);

    //backend
    ServerResponse<PageInfo> manageOrderList(Integer pageNum, Integer pageSize);

    ServerResponse<OrderVo> manageOrderDetail(Long orderNo);

    ServerResponse<PageInfo> manageOrderSearch(Long orderNo, Integer pageNum, Integer pageSize);

    ServerResponse<String> manageSendGoods(Long orderNo);
}
