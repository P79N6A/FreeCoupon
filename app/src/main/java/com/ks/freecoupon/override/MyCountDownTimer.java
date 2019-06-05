package com.ks.freecoupon.override;

import android.os.CountDownTimer;

import com.ks.freecoupon.ks_interface.OnCountDownTimerListener;

/**
 * author：康少
 * date：2018/11/7
 * description：倒计时对象
 */
public class MyCountDownTimer extends CountDownTimer {
    private OnCountDownTimerListener onCountDownTimerListener;

    /**
     * 倒计时对象,总共的时间,每隔多少秒更新一次时间
     *
     * @param millisInFuture    倒计时多少时间，毫秒为单位
     * @param countDownInterval 一次减少多少时间 毫秒为单位
     */
    public MyCountDownTimer(long millisInFuture, long countDownInterval) {
        super(millisInFuture, countDownInterval);
    }

    public void setOnCountDownTimerListener(OnCountDownTimerListener onCountDownTimerListener) {
        this.onCountDownTimerListener = onCountDownTimerListener;
    }

    //计时过程
    @Override
    public void onTick(long l) {
        if (onCountDownTimerListener != null) {
            onCountDownTimerListener.onTick(l);
        }
    }

    //计时完毕的方法
    @Override
    public void onFinish() {
        if (onCountDownTimerListener != null) {
            onCountDownTimerListener.onFinish();
        }
    }
}
