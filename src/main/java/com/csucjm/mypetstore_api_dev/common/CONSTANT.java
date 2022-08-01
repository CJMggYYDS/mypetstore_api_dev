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

    public interface ROLE {
        int CUSTOMER = 1;
        int ADMIN = 0;
    }
}
