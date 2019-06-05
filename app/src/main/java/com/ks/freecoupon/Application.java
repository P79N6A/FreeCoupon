package com.ks.freecoupon;

import com.ks.basictools.BaseApplication;

/**
 * author：康少
 * date：2019/3/7
 * description：主应用方法
 */
public class Application extends BaseApplication {
    public static Application application = null;
    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
    }
}
