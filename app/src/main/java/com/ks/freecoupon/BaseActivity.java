package com.ks.freecoupon;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.ks.basictools.ActivityCollector;
import com.ks.basictools.AppManager;
import com.ks.freecoupon.override.LoadingDialog;

/**
 * author：康少
 * date：2019/3/7
 * description：
 */
public class BaseActivity extends com.ks.basictools.base.BaseActivity {
    /*全局加载中Dialog*/
    private LoadingDialog mLoadingDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //添加到Activity管理
        ActivityCollector.addActivity(this, getClass());
        //将Activity实例添加到AppManager的堆栈
        AppManager.getAppManager().addActivity(this);
        //全局加载中···
        mLoadingDialog = LoadingDialog.getInstance();
    }

    /**
     * Description：显示加载中。。。
     */
    public void showLoading() {
        if (!AppManager.getAppManager().currentActivity().isFinishing()) {
            mLoadingDialog.show();
        }
    }

    /**
     * Description：关掉加载中。。。
     */
    public void dismissLoading() {
        if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
        }
    }

    /**
     * 设置状态栏透明
     * @param isTrans 是否透明
     * @param isUIBlack 文字和图标颜色是否为深色
     */
    public void setStatusBarTrans(boolean isTrans,boolean isUIBlack) {
        if (isTrans) {
            /*设置状态栏*/
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                View decorView = getWindow().getDecorView();
                //View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR：设置状态栏中的文字颜色和图标颜色为深色，不设置默认为白色，需要android系统6.0以上。
                if (isUIBlack) {
                    decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
                } else {
                    decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
                }
                //设置状态栏的颜色
                getWindow().setStatusBarColor(Color.TRANSPARENT);
            }
        } else {
            /*设置状态栏*/
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                View decorView = getWindow().getDecorView();
                //状态栏中的文字颜色和图标颜色为深色，需要android系统6.0以上，而且目前只有一种可以修改（一种是深色，一种是浅色即白色）
                decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                //设置状态栏的颜色
                getWindow().setStatusBarColor(getResources().getColor(R.color.white));
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dismissLoading();
    }
}
