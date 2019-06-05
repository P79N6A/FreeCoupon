package com.ks.freecoupon.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Build;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.WindowManager;

import com.ks.freecoupon.R;

/**
 * Date:2019/3/12
 * Author：康少
 * Description：超级好用的屏幕相关工具类！！！
 */
public class ScreenUtils {

    /**
     * Description：获取占满屏幕宽度下w:h=16:9的高
     */
    public static int getHeight16_9(Activity activity) {
        return getHeight16_9(activity, 0);
    }

    /**
     * 获取16:9的高
     *
     * @param activity
     * @param margin_dp 图片左右两边到屏幕的间距和
     * @return
     */
    public static int getHeight16_9(Activity activity, int margin_dp) {
        return (getNormalWH(activity)[isPortrait(activity) ? 0 : 1] - MeasureUtils.dp2px(activity, margin_dp)) * 9 / 16;
    }

    // 是否竖屏
    public static boolean isPortrait(Context context) {
        return context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
    }

    // 是否横屏
    public static boolean isLandscape(Context context) {
        return context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
    }

    // 设置竖屏
    public static void setPortrait(Activity activity) {
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    // 设置横屏
    public static void setLandscape(Activity activity) {
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }

    /**
     * 获取包含状态栏的屏幕宽度和高度
     *
     * @param activity 当前活动Activity
     * @return {宽,高}
     */
    public static int[] getNormalWH(Activity activity) {
        Point point = new Point();
        WindowManager wm = activity.getWindowManager();
        wm.getDefaultDisplay().getSize(point);
        return new int[]{point.x, point.y};
    }

    /**
     * Description：重置状态栏，横屏隐藏状态栏、竖屏显示状态栏
     */
    public static void reSetStatusBar(Activity activity) {
        if (isLandscape(activity)) {
            hideStatusBar(activity);
        } else {
            setDecorVisible(activity);
        }
    }

    /**
     * Description：隐藏状态栏
     */
    public static void hideStatusBar(Activity activity) {
        View decorView = activity.getWindow().getDecorView();
        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);
    }

    /**
     * Description：恢复为不全屏状态
     */
    public static void setDecorVisible(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decorView = activity.getWindow().getDecorView();
            //清除全屏属性
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            //状态栏中的文字颜色和图标颜色为深色，需要android系统6.0以上，而且目前只有一种可以修改（一种是深色，一种是浅色即白色）
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            //设置状态栏的颜色
            activity.getWindow().setStatusBarColor(activity.getResources().getColor(R.color.white));
        }
    }


    /**
     * 设置全屏
     *
     * @param activity
     */
    public static void setFullScreen(Activity activity) {
        if (activity!=null) {
            View decorView = activity.getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        }
    }

    /**
     * 取消全屏
     *
     * @param activity
     */
    public static void cancelFullScreen(Activity activity) {
        if (activity != null) {
            activity.getWindow().clearFlags(
                    WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    /**
     * 设置状态栏文字、图标的颜色
     *
     * @param activity
     * @param isDark  false,白色；true，黑色
     */
    public static void setStatusBarTextDark(Activity activity, boolean isDark) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if (activity != null) {
//                View decorView = activity.getWindow().getDecorView();
//                if (isWhite) {
//                    decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);//透明状态栏时，系统默认为白色字体
//                } else {
//                    decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
//                }
//            }
//        }
        if (activity != null) {
            View decor = activity.getWindow().getDecorView();
            if (isDark) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                }
            } else {
                decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            }
        } else {
            try {
                throw new Exception("activity为空！");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 设置状态栏背景颜色
     *
     * @param activity
     * @param color 默认透明 Color.TRANSPARENT
     */
    public static void setStatusBarColor(Activity activity, int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (color == 0) {
                color = Color.TRANSPARENT;
            }
            activity.getWindow().setStatusBarColor(color);
        }
    }
}
