package com.mmall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.mmall.common.ServerResponse;
import com.mmall.dao.ShippingMapper;
import com.mmall.pojo.Shipping;
import com.mmall.service.IShippingServive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Created by wang on 2017/5/22.
 */
@Service("iShippingService")
public class ShippingServiceImpl implements IShippingServive {

    @Autowired
    private ShippingMapper shippingMapper;

    public ServerResponse addShipping(Integer userId, Shipping shipping) {
        shipping.setUserId(userId);
        int rowCount = shippingMapper.insert(shipping);

        if (rowCount > 0 ) {
            Map result = Maps.newHashMap();
            result.put("shippingId", shipping.getId());
            return ServerResponse.createBySuccess("success to add a new shipping", result);
        }

        return ServerResponse.createByErrorMessage("failed to add a new shipping");
    }

    public ServerResponse<String> delShipping(Integer userId, Integer shippingId) {
        int rowCount = shippingMapper.deleteByUseridShippingid(userId, shippingId);

        if (rowCount > 0 ) {
            return ServerResponse.createBySuccess("success to delete a shipping");
        }

        return ServerResponse.createByErrorMessage("failed to delete a shipping");
    }

    public ServerResponse<String> updateShipping(Integer userId, Shipping shipping) {
        shipping.setUserId(userId);
        int rowCount = shippingMapper.updateShipping(shipping);

        if (rowCount > 0 ) {
            return ServerResponse.createBySuccess("success to update shipping");
        }

        return ServerResponse.createByErrorMessage("failed to update shipping");
    }

    public ServerResponse<Shipping> selectShipping(Integer userId, Integer shippingId) {
        if (shippingId == null)
            return ServerResponse.createByErrorMessage("param wrong");

        Shipping shipping = shippingMapper.selectByUseridShippingid(userId, shippingId);

        if (shipping == null)
            return ServerResponse.createByErrorMessage("not found the shipping");

        return ServerResponse.createBySuccess("success", shipping);
    }

    public ServerResponse<PageInfo> list(int userId, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Shipping> shippingList = shippingMapper.selectListByUserid(userId);
        PageInfo pageInfo = new PageInfo(shippingList);

        return ServerResponse.createBySuccess(pageInfo);
    }
}
