package com.ks.freecoupon.module.view.account.presenter;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.ks.basictools.base_interface.StringCallBack;
import com.ks.basictools.utils.JSONUtil;
import com.ks.basictools.utils.LogUtils;
import com.ks.basictools.utils.ToastUtil;
import com.ks.freecoupon.R;
import com.ks.freecoupon.ks_interface.OnCountDownTimerListener;
import com.ks.freecoupon.module.contract.account.AccountAuthActivityContract;
import com.ks.freecoupon.module.view.account.activity.AccountAuthActivity;
import com.ks.freecoupon.module.view.account.activity.ResetPasswordActivity;
import com.ks.freecoupon.override.MyCountDownTimer;
import com.ks.freecoupon.override.ToastKs;
import com.ks.freecoupon.utils.OkHttpUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import okhttp3.Call;

import static android.app.Activity.RESULT_OK;

/**
 * author：康少
 * date：2019/3/15
 * description：账户认证——逻辑层
 */
public class AccountAuthActivityPresenter implements AccountAuthActivityContract.IAccountAuthActivityPresenter {
    private AccountAuthActivity mAccountAuthActivity;
    /*控件对象*/
    private EditText etPhoneNumber;
    private TextView btnGetYZM;
    private EditText etYZM;
    /*参数*/
    private String mobile = "";

    //new倒计时对象,总共的时间,每隔多少秒更新一次时间
    private MyCountDownTimer myCountDownTimer;
    private final int RESET_PASSWORD = 0X02;//重设密码的回调

    public AccountAuthActivityPresenter(AccountAuthActivity mAccountAuthActivity) {
        this.mAccountAuthActivity = mAccountAuthActivity;
        mAccountAuthActivity.setPresenter(this);
    }

    @Override
    public void start() {
        initObject();
    }

    private void initObject() {
        etPhoneNumber = mAccountAuthActivity.getEtPhoneNumber();
        btnGetYZM = mAccountAuthActivity.getBtnGetYZM();
        etYZM = mAccountAuthActivity.getEtYZM();

        myCountDownTimer = new MyCountDownTimer(60000, 1000);
        myCountDownTimer.setOnCountDownTimerListener(new OnCountDownTimerListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onTick(long l) {
                //防止计时过程中重复点击
                btnGetYZM.setClickable(false);
                btnGetYZM.setText("重新发送" + l / 1000 + "s");
            }
            @Override
            public void onFinish() {
                //重新给Button设置文字
                btnGetYZM.setText("重新获取验证码");
                //设置可点击
                btnGetYZM.setClickable(true);
            }
        });
    }

    /**
     * Description：发送验证码
     * @param mobile
     */
    private void sendYZM(String mobile) {
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        map.put("mobile", mobile);
        map.put("type", "reset_password");
        list.add(map);
        OkHttpUtil okHttpUtil = new OkHttpUtil(new StringCallBack() {
            @Override
            public void onResponse(String response, int id) {
                boolean status = JSONUtil.getBoolean(response, "status", false);
                if (status) {
                    ToastKs.show(mAccountAuthActivity, "发送成功！");
                    //获取验证码倒计时
                    myCountDownTimer.start();
                }else {
                    String msg = JSONUtil.getString(response, "msg", "");
                    ToastUtil.show(mAccountAuthActivity, msg);
                }
            }
            @Override
            public void onError(Call call, Exception e, int id) {
                LogUtils.e(e);
            }
        });
        okHttpUtil.requestCall(mAccountAuthActivity, "user.sms", list);
    }

    /**
     * Description：下一步前的验证
     */
    private void authYZM(final String mobile, String yzm) {
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        map.put("mobile", mobile);
        map.put("code", yzm);
        map.put("type", "reset_password");
        list.add(map);
        OkHttpUtil okHttpUtil = new OkHttpUtil(new StringCallBack() {
            @Override
            public void onResponse(String response, int id) {
                boolean status = JSONUtil.getBoolean(response, "status", false);
                if (status) {
                    Intent intent = new Intent(mAccountAuthActivity, ResetPasswordActivity.class);
                    intent.putExtra("mobile", mobile);
                    mAccountAuthActivity.startActivityForResult(intent, RESET_PASSWORD);
                }else {
                    String msg = JSONUtil.getString(response, "msg", "");
                    ToastUtil.show(mAccountAuthActivity, msg);
                }
            }
            @Override
            public void onError(Call call, Exception e, int id) {
                LogUtils.e(e);
            }
        });
        okHttpUtil.requestCall(mAccountAuthActivity, "user.checkcode", list);
    }

    @Override
    public void onViewClicked(View view) {
        mobile = etPhoneNumber.getText().toString();
        String yzm = etYZM.getText().toString();
        switch (view.getId()) {
            case R.id.btn_getYZM:
                if (mobile.equals("")) {
                    ToastKs.show(mAccountAuthActivity, "请输入手机号码");
                    return;
                }
                if (mobile.length() != 11) {
                    ToastKs.show(mAccountAuthActivity, "请输入正确的手机号码");
                    return;
                }
                //发送验证码
                sendYZM(mobile);
                break;
            case R.id.btn_next:
                if (yzm.equals("")) {
                    ToastKs.show(mAccountAuthActivity, "请输入验证码");
                    return;
                }
                //下一步前的验证
                authYZM(mobile, yzm);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case RESET_PASSWORD:
                    boolean success;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {// Android 4.4及以上版本适用
                        success = Objects.requireNonNull(data.getExtras()).getBoolean("reset_success");//得到新Activity 关闭后返回的数据
                    } else {
                        ToastUtil.show(mAccountAuthActivity, "您的Android系统版本过低，请升级系统至Android4.4及以上版本后使用此功能");
                        return;
                    }
                    if (success) {
                        //数据是使用Intent返回
                        Intent intent = new Intent();
                        //把返回数据存入Intent
                        intent.putExtra("mobile", mobile);
                        //设置返回数据
                        mAccountAuthActivity.setResult(RESULT_OK, intent);
                        mAccountAuthActivity.finish();
                    }
                    break;
            }
        }
    }
}
