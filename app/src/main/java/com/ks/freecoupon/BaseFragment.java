package com.ks.freecoupon;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.ks.freecoupon.override.LoadingDialog;

/**
 * author：康少
 * date：2019/3/7
 * description：
 */
public class BaseFragment extends com.ks.basictools.base.BaseFragment{
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*设置状态栏*/
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (this.getActivity() != null) {
                View decorView = this.getActivity().getWindow().getDecorView();
                //状态栏中的文字颜色和图标颜色为深色，需要android系统6.0以上，而且目前只有一种可以修改（一种是深色，一种是浅色即白色）
                decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
                //设置状态栏的颜色
                this.getActivity().getWindow().setStatusBarColor(Color.TRANSPARENT);
            }
        }
    }

    @Override
    protected void init() {

    }

    /**
     * 设置状态栏背景色为透明、满屏展示 要求android6.0及以上
     *
     * @param isWhiteTextColor 文字颜色是否显示为白色
     */
    public void setStatusBarTranAndTextColor(boolean isWhiteTextColor) {
        /*设置状态栏透明*/
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (getActivity() != null) {
                View decorView = getActivity().getWindow().getDecorView();
                if (isWhiteTextColor) {
                    decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);//透明状态栏时，系统默认为白色字体
                } else {
                    decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                }
                getActivity().getWindow().setStatusBarColor(Color.TRANSPARENT);
            }
        }
    }
}
