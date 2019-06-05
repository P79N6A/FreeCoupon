package com.ks.freecoupon.utils;

import android.content.Context;

/**
 * author：康少
 * date：2019/3/12
 * description：尺寸工具
 */
public class MeasureUtils {
    /**
     * px像素转dp
     */
    public static int px2dp(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;//像素比
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * dp转px像素
     */
    public static int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;//像素比
        return (int) (dpValue * scale + 0.5f);
    }
}
