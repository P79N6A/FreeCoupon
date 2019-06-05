package com.ks.freecoupon.ks_interface;

/**
 * author：康少
 * date：2019/3/29
 * description：领卡券选中监听
 */
public interface OnCouponCheckedListener {
    void onChecked(int type);//type：0淘宝；1京东；2附近；
}
