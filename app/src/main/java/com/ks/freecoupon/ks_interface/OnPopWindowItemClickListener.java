package com.ks.freecoupon.ks_interface;

import android.widget.PopupWindow;

/**
 * author：康少
 * date：2018/10/13
 * description：PopWindow的Item的点击事件
 */
public interface OnPopWindowItemClickListener {
    /**
     * @param popWindow 自定义的PopWindow对象
     * @param index 点击的某一行的下标，0：表示点击了第一行
     */
    void onClick(PopupWindow popWindow, String index);
}
