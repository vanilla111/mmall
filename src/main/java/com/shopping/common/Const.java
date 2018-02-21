package com.shopping.common;

import com.google.common.collect.Sets;

import java.util.Set;

/**
 * Created by wang on 2017/5/12.
 */
public class Const {
    public static final String TOKEN_PREFIX = "token_";
    public static final String CURRENT_USER = "currentUser";
    public static final String EMAIL = "email";
    public static final String USERNAME = "username";

    public interface productListOrderBy {
        Set<String> PRICE_DESC_ASC = Sets.newHashSet("price_desc", "price_asc");
    }

    public interface RedisCacheExtime {
        int REDIS_SESSION_EXTIME = 60 * 30;
    }

    public interface cart {
        int CHECKED = 1;
        int UN_CHECKED = 0;

        String CART_LIMIT_FAIL = "CART_LIMIT_FAIl";
        String CART_LIMIT_SUCCESS = "CART_LIMIT_SUCCESS";
    }

    public interface Role {
        int ROLE_CUSTOMER = 0;  //普通用户
        int ROLE_ADMIN = 1; //管理员
    }

    public enum ProductStatusEnum {
        ON_SALE(0, "在线");

        private int code;
        private String status;

        ProductStatusEnum(int code, String status) {
            this.status = status;
            this.code = code;
        }

        public String getStatus() {
            return status;
        }

        public int getCode() {
            return code;
        }

    }

    public enum OrderStatusEnum {
        CANCELED(0, "已取消"),
        NO_PAY(10, "未支付"),
        PAID(20, "已支付"),
        SHIPPED(40, "已发货"),
        ORDER_SUCCESS(50, "订单已完成"),
        ORDER_CLOSED(60, "订单关闭")
        ;

        private int code;
        private String status;

        OrderStatusEnum(int code, String status) {
            this.status = status;
            this.code = code;
        }

        public String getStatus() {
            return status;
        }

        public int getCode() {
            return code;
        }

        public static OrderStatusEnum codeOf(int code) {
            for (OrderStatusEnum orderStatusEnum : values()) {
                if (code == orderStatusEnum.getCode()) {
                    return orderStatusEnum;
                }
            }
            throw new RuntimeException("未找到对应枚举");
        }
    }

    public interface AlipayCallBack {
        String WAIT_BUYER_PAY = "WAIT_BUYER_PAY";
        String TRADE_SUCCESS = "TRADE_SUCCESS";
        String RESPONSE_SUCCESS = "success";
        String RESPONSE_FAILED = "failed";
    }

    public enum PayPlatformEnum {
        ALIPAY(1, "支付宝");

        private int code;
        private String value;

        PayPlatformEnum(int code, String status) {
            this.value = status;
            this.code = code;
        }

        public String getValue() {
            return value;
        }

        public int getCode() {
            return code;
        }
    }

    public enum PaymentTypeEnum {
        ONLINE_PAY(1, "在线支付");

        private int code;
        private String value;

        PaymentTypeEnum(int code, String value) {
            this.value = value;
            this.code = code;
        }

        public String getValue() {
            return value;
        }

        public int getCode() {
            return code;
        }

        public static PaymentTypeEnum codeOf(int code) {
            for (PaymentTypeEnum typeEnum : values()) {
                if (code == typeEnum.getCode()) {
                    return typeEnum;
                }
            }
            throw new RuntimeException("未找到对应枚举");
        }
    }
}
