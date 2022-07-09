package com.reggie.util;

public class RedisConstants {
    public static final String USER_LOGIN_CODE_KEY = "userLogin:code:";
    public static final Long LOGIN_CODE_TTL = 2L;
    public static final String USER_LOGIN_USER_KEY = "userLogin:token:";
    public static final Long LOGIN_USER_TTL = 30L;

    public static final String EMP_LOGIN_TOKEN_KEY = "empLogin:token:";
    public static final Long LOGIN_EMP_TTL = 7L;
    public static final String CACHE_DISH_KEY = "dish:categoryId:";


    public static final Long CACHE_NULL_TTL = 2L;

    public static final Long CACHE_SHOP_TTL = 30L;
    public static final String CACHE_SHOP_KEY = "cache:shop:";
    public static final String CACHE_SHOP_TYPE_KEY = "cache:shop:type";

    public static final String LOCK_SHOP_KEY = "lock:shop:";
    public static final Long LOCK_SHOP_TTL = 10L;

    public static final String SECKILL_STOCK_KEY = "seckill:stock:";
    public static final String BLOG_LIKED_KEY = "blog:liked:";
    public static final String FEED_KEY = "feed:";
    public static final String SHOP_GEO_KEY = "shop:geo:";
    public static final String USER_SIGN_KEY = "sign:";


}
