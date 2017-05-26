package com.mmall.service;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Shipping;

/**
 * Created by wang on 2017/5/22.
 */
public interface IShippingServive {
    ServerResponse addShipping(Integer userId, Shipping shipping);

    ServerResponse<String> delShipping(Integer userId, Integer shippingId);

    ServerResponse<String> updateShipping(Integer userId, Shipping shipping);

    ServerResponse<Shipping> selectShipping(Integer userId, Integer shippingId);

    ServerResponse<PageInfo> list(int userId, int pageNum, int pageSize);
}
