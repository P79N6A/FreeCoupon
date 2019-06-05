package com.ks.freecoupon.ks_interface;

/**
 * author：康少
 * date：2018/11/7
 * description：计时监听器
 */
public interface OnCountDownTimerListener {
    void onTick(long l);//计时过程
    void onFinish();//计时结束
}
