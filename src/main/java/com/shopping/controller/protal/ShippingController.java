package com.shopping.controller.protal;

import com.github.pagehelper.PageInfo;
import com.shopping.common.ResponseCode;
import com.shopping.common.ServerResponse;
import com.shopping.pojo.Shipping;
import com.shopping.pojo.User;
import com.shopping.service.IShippingServive;
import com.shopping.util.CookieUtil;
import com.shopping.util.JsonUtil;
import com.shopping.util.RedisShardedPoolUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by wang on 2017/5/22.
 */
@Controller
@RequestMapping("/shipping/")
public class ShippingController {

    @Autowired
    private IShippingServive iShippingServive;

    @RequestMapping("add.do")
    @ResponseBody
    public ServerResponse add(HttpServletRequest request, Shipping shipping) {
        String loginToken = CookieUtil.readLoginToken(request);
        User user = null;
        if (StringUtils.isNotEmpty(loginToken))
            user = JsonUtil.stringToObj(RedisShardedPoolUtil.get(loginToken), User.class);
        if (user == null)
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());

        return iShippingServive.addShipping(user.getId(), shipping);
    }

    @RequestMapping("del.do")
    @ResponseBody
    public ServerResponse<String> del(HttpServletRequest request, Integer shippingId) {
        String loginToken = CookieUtil.readLoginToken(request);
        User user = null;
        if (StringUtils.isNotEmpty(loginToken))
            user = JsonUtil.stringToObj(RedisShardedPoolUtil.get(loginToken), User.class);
        if (user == null)
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());

        return iShippingServive.delShipping(user.getId(), shippingId);
    }

    @RequestMapping("update.do")
    @ResponseBody
    public ServerResponse update(HttpServletRequest request, Shipping shipping) {
        String loginToken = CookieUtil.readLoginToken(request);
        User user = null;
        if (StringUtils.isNotEmpty(loginToken))
            user = JsonUtil.stringToObj(RedisShardedPoolUtil.get(loginToken), User.class);
        if (user == null)
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());

        return iShippingServive.updateShipping(user.getId(), shipping);
    }

    @RequestMapping("select.do")
    @ResponseBody
    public ServerResponse<Shipping> select(HttpServletRequest request, Integer shippingId) {
        String loginToken = CookieUtil.readLoginToken(request);
        User user = null;
        if (StringUtils.isNotEmpty(loginToken))
            user = JsonUtil.stringToObj(RedisShardedPoolUtil.get(loginToken), User.class);
        if (user == null)
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());

        return iShippingServive.selectShipping(user.getId(), shippingId);
    }

    public ServerResponse<PageInfo> list(@RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                                         @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
                                         HttpServletRequest request) {
        String loginToken = CookieUtil.readLoginToken(request);
        User user = null;
        if (StringUtils.isNotEmpty(loginToken))
            user = JsonUtil.stringToObj(RedisShardedPoolUtil.get(loginToken), User.class);
        if (user == null)
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());

        return iShippingServive.list(user.getId(), pageNum, pageSize);
    }
}
