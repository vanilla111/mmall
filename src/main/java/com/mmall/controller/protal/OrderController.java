package com.mmall.controller.protal;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.demo.trade.config.Configs;
import com.google.common.collect.Maps;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IOrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by wang on 2017/5/23.
 */
@Controller
@RequestMapping("/order/")
public class OrderController {

    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private IOrderService iOrderService;

    @RequestMapping("create.do")
    @ResponseBody
    public ServerResponse create(HttpSession session, Integer shippingId) {
        User user =(User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }

        return iOrderService.createOrder(user.getId(), shippingId);
    }

    @RequestMapping("cancel.do")
    @ResponseBody
    public ServerResponse cancel(HttpSession session, Long orderNo) {
        User user =(User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }

        return iOrderService.cancelOrder(user.getId(), orderNo);
    }

    @RequestMapping("get_order_cart_product.do")
    @ResponseBody
    public ServerResponse getOrderCartProductChecked(HttpSession session, Long orderNo) {
        User user =(User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }

        //todo 获取历史订单详情是否可以复用
        return iOrderService.getOrderCartProduct(user.getId());
    }

    @RequestMapping("detail.do")
    @ResponseBody
    public ServerResponse detail(HttpSession session, Long orderNo) {
        User user =(User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }

        return iOrderService.getOrderDetail(user.getId(), orderNo);
    }

    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse list(HttpSession session, Long orderNo,
                               @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                               @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        User user =(User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }

        return iOrderService.getOrderList(user.getId(), pageNum, pageSize);
    }

    @RequestMapping("pay.do")
    @ResponseBody
    public ServerResponse pay(HttpSession session, Long orderNo, HttpServletRequest request) {
        User user =(User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        String path = request.getSession().getServletContext().getRealPath("upload");

        return iOrderService.pay(user.getId(), path, orderNo);
    }

    @RequestMapping("alipay_callback.do")
    @ResponseBody
    public Object alipayCallback(HttpServletRequest request) {
        Map<String, String> params = Maps.newHashMap();
        Map requestmap = request.getParameterMap();
        Iterator iter = requestmap.keySet().iterator();
        for (;iter.hasNext();) {
            String name = (String) iter.next();
            String[] value = (String[]) requestmap.get(name);
            String valueStr = "";
            for (int i = 0; i < value.length; i++) {
                valueStr = (i == value.length - 1) ? valueStr + value[i] : valueStr + value[i] + ",";
            }
            params.put(name, valueStr);
        }
        logger.info("支付宝回调接口,sign:{},trade_status:{},param:{}", params.get("sign"),
                params.get("trade_status"), params.toString());
        //验证回调的正确性,避免重复通知
        params.remove("sign_type");
        try {
            boolean alipayRSAChecked = AlipaySignature.rsaCheckV2(params, Configs.getAlipayPublicKey(), "utf-8", Configs.getSignType());
            if (!alipayRSAChecked) {
                return ServerResponse.createByErrorMessage("非法请求, 敢动歪点子, 打死打死你！！");
            }
        } catch (AlipayApiException e) {
            logger.error("回调异常：", e);
            e.printStackTrace();
        }
        //todo 验证各种数据

        //
        ServerResponse serverResponse = iOrderService.alipayCallback(params);
        if (serverResponse.isSuccess())
            return Const.AlipayCallBack.RESPONSE_SUCCESS;

        return Const.AlipayCallBack.RESPONSE_FAILED;
    }

    @RequestMapping("query_order_pay_status.do")
    @ResponseBody
    public ServerResponse<Boolean> queryOrderPayStatus(HttpSession session, Long orderNo) {
        User user =(User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }

        ServerResponse serverResponse = iOrderService.queryOrderPayStatus(user.getId(), orderNo);
        if (serverResponse.isSuccess())
            return ServerResponse.createBySuccess(true);

        return ServerResponse.createBySuccess(false);
    }
}

