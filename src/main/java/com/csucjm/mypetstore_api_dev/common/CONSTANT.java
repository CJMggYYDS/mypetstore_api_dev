package com.csucjm.mypetstore_api_dev.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class CONSTANT {

    public static final String LOGIN_USER = "loginUser";

    public static final Integer CATEGORY_ROOT = 0;

    // 排序规则,目前只按价格大小
    public static final String PRODUCT_ORDER_BY_PRICE_ASC = "price_asc";
    public static final String PRODUCT_ORDER_BY_PRICE_DESC = "price_desc";

    @AllArgsConstructor
    @Getter
    public enum ProductStatus {

        ON_SALE(1, "on_sale"),
        TAKE_DOWN(2, "take_down"),
        DELETE(3, "delete");

        private final int code;
        private final String description;
    }

    @AllArgsConstructor
    @Getter
    public enum PayType {
        ALIPAY(1, "支付宝"),
        WECHAT(2, "微信支付"),
        OTHER(3, "其他方式");

        private final int code;
        private final String description;
    }

    @AllArgsConstructor
    @Getter
    public enum OrderStatus {
        CANCEL(1, "已取消"),
        UNPAID(2, "未付款"),
        PAID(3, "已付款"),
        SHIPPED(4, "已发货"),
        SUCCESS(5, "交易成功"),
        CLOSED(6, "订单关闭");

        private final int code;
        private final String description;
    }

    public interface ROLE {
        int CUSTOMER = 1;
        int ADMIN = 0;
    }

    public interface CART_CHECK{
        int CHECKED = 1;
        int UNCHECKED = 2;
    }
}
