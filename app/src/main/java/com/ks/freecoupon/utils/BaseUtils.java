package com.ks.freecoupon.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AlertDialog;
import android.view.View;

import com.ks.freecoupon.R;
import com.ks.freecoupon.module.bean.User;
import com.ks.freecoupon.module.view.account.activity.LoginActivity;
import com.ks.freecoupon.override.ShadowDrawable;

import java.sql.Date;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

/**
 * author：康少
 * date：2019/3/21
 * description：
 */
public class BaseUtils {
    /**
     * 保留两位小数，四舍五入的一个老土的方法
     * @param v
     * @return
     */
    public static String doubleFormatBy2(double v) {
        return doubleFormatByScale(v, 2);
    }

    /**
     * 将double格式化为指定小数位的String，不足小数位用0补全
     * @param v     需要格式化的数字
     * @param scale 小数点后保留几位
     * @return
     */
    public static String doubleFormatByScale(double v, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException(
                    "The   scale   must   be   a   positive   integer   or   zero");
        }
        if (scale == 0) {
            return new DecimalFormat("0").format(v);
        }
        String formatStr = "0.";
        for (int i = 0; i < scale; i++) {
            formatStr = formatStr + "0";
        }
        return new DecimalFormat(formatStr).format(v);
    }

    /**
     * 设置控件阴影效果
     * @param view 视图View
     * @param bgColor 控件背景颜色
     * @param shapeRadius 控件边缘圆角值
     * @param shadowColor 阴影颜色 通常使用透明度 #44000000
     * @param shadowRadius 阴影半径
     * @param offsetX 阴影相对于控件的横向偏移量 左正右负
     * @param offsetY 阴影相对于控件的纵向偏移量 下正上负
     */
    public static void setShadowDrawable(View view, int bgColor, int shapeRadius, int shadowColor, int shadowRadius, int offsetX, int offsetY) {
        ShadowDrawable drawable = new ShadowDrawable.Builder()
                .setBgColor(bgColor)
                .setShapeRadius(shapeRadius)
                .setShadowColor(shadowColor)
                .setShadowRadius(shadowRadius)
                .setOffsetX(offsetX)
                .setOffsetY(offsetY)
                .builder();
        view.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        ViewCompat.setBackground(view, drawable);
    }

    /**
     * Description：温馨提示——登录
     */
    public static void promptLogin(final Context context) {
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle("温馨提示")
                .setMessage("注册登录后开启此功能")
                .setPositiveButton("确定",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent i = new Intent(context, LoginActivity.class);
                        context.startActivity(i);
                    }
                })
                .setNegativeButton("取消",null)
                .create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(context.getResources().getColor(R.color.colorPrimary));
        dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(context.getResources().getColor(R.color.colorPrimary));
    }

    /**
     * Description：温馨提示——退出登录
     */
    public static void promptLogout(final Context context) {
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle("温馨提示")
                .setMessage("是否确认退出登录")
                .setPositiveButton("确定",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferencesUtils.setParam(context, "isLogin", false);
                        SharedPreferencesUtils.removeKey(context, "token");
                        User.getInstance().emptyUser();//清空User数据
                        Intent i = new Intent(context, LoginActivity.class);
                        i.putExtra("isLogout", true);
                        context.startActivity(i);
                    }
                })
                .setNegativeButton("取消",null)
                .create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(context.getResources().getColor(R.color.colorPrimary));
        dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(context.getResources().getColor(R.color.colorPrimary));
    }

    /**
     * 时间戳转换成日期格式字符串
     *
     * @param seconds 精确到秒的字符串
     * @param format
     * @return
     */
    public static String timeStamp2Date(String seconds, String format) {
        if (seconds == null || seconds.isEmpty() || seconds.equals("null")) {
            return "";
        }
        if (format == null || format.isEmpty()) {
            format = "yyyy-MM-dd HH:mm:ss";
        }
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(new Date(Long.valueOf(seconds)));
    }
}
