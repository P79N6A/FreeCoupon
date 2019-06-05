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
import com.ks.freecoupon.module.contract.account.RegisterActivityContract;
import com.ks.freecoupon.module.view.MainActivity;
import com.ks.freecoupon.module.view.account.activity.ProtocolActivity;
import com.ks.freecoupon.module.view.account.activity.RegisterActivity;
import com.ks.freecoupon.override.MyCountDownTimer;
import com.ks.freecoupon.override.ToastKs;
import com.ks.freecoupon.utils.OkHttpUtil;
import com.ks.freecoupon.utils.SharedPreferencesUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

/**
 * author：康少
 * date：2019/3/15
 * description：注册——逻辑层
 */
public class RegisterActivityPresenter implements RegisterActivityContract.IRegisterActivityPresenter {
    private RegisterActivity mRegisterActivity;
    /*控件对象*/
    private EditText etPhone;
    private EditText etYZM;
    private EditText etPassword;
    private EditText etConformPassword;
    private TextView btnGetYZM;

    //new倒计时对象,总共的时间,每隔多少秒更新一次时间
    private MyCountDownTimer myCountDownTimer;

    public RegisterActivityPresenter(RegisterActivity mRegisterActivity) {
        this.mRegisterActivity = mRegisterActivity;
        mRegisterActivity.setPresenter(this);
    }

    @Override
    public void start() {
        initObject();
    }

    private void initObject() {
        etPhone = mRegisterActivity.getEtPhone();
        etYZM = mRegisterActivity.getEtYZM();
        btnGetYZM = mRegisterActivity.getBtnGetYZM();
        etPassword = mRegisterActivity.getEtPassword();
        etConformPassword = mRegisterActivity.getEtConformPassword();

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
     * Description：发送验证码前的验证
     *
     * @param mobile
     */
    private void authSendYZM(String mobile) {
        if (mobile.equals("")) {
            ToastKs.show(mRegisterActivity, "请输入手机号码");
            return;
        }
        if (mobile.length() != 11) {
            ToastKs.show(mRegisterActivity, "请输入正确的手机号码");
            return;
        }
        //发送验证码短信
        sendYZM();
    }

    /**
     * Description：发送验证码
     */
    private void sendYZM() {
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        map.put("mobile", etPhone.getText().toString());
        map.put("type", "reg");
        list.add(map);
        OkHttpUtil okHttpUtil = new OkHttpUtil(new StringCallBack() {
            @Override
            public void onResponse(String response, int id) {
                boolean status = JSONUtil.getBoolean(response, "status", false);
                if (status) {
                    ToastKs.show(mRegisterActivity, "发送成功！");
                    //获取验证码倒计时
                    myCountDownTimer.start();
                } else {
                    ToastUtil.show(mRegisterActivity, JSONUtil.getString(response, "msg", ""));
                }
            }

            @Override
            public void onError(Call call, Exception e, int id) {

            }
        });
        okHttpUtil.requestCall(mRegisterActivity, "user.sms", list);
    }

    /**
     * Description：注册前验证
     */
    private void authRegister(String username, String yzm, String password, String conformPassword) {
        if (username.length() != 11) {
            ToastKs.show(mRegisterActivity, "请输入正确的手机号码");
            return;
        }
        if (yzm.equals("")) {
            ToastKs.show(mRegisterActivity, "请输入验证码");
            return;
        }
        if (password.equals("")) {
            ToastKs.show(mRegisterActivity, "请输入密码");
            return;
        }
        if (conformPassword.equals("")) {
            ToastKs.show(mRegisterActivity, "请输入确认密码");
            return;
        }
        if (!conformPassword.equals(password)) {
            ToastKs.show(mRegisterActivity, "两次密码输入不一致");
            return;
        }
        //注册
        register(username, password, yzm);
    }

    /**
     * Description：注册
     *
     * @param username
     * @param password
     * @param yzm
     */
    private void register(String username, String password, String yzm) {
        LogUtils.d("请求注册接口：username:" + username);
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        map.put("mobile", username);
        map.put("password", password);
        map.put("code", yzm);
        list.add(map);
        OkHttpUtil okHttpUtil = new OkHttpUtil(new StringCallBack() {
            @Override
            public void onResponse(String response, int id) {
                boolean status = JSONUtil.getBoolean(response, "status", false);
                if (status) {
                    String data = JSONUtil.getString(response, "data", "");
                    String token = JSONUtil.getString(data, "token", "");
                    ToastKs.show(mRegisterActivity, "注册成功！");
                    SharedPreferencesUtils.setParam(mRegisterActivity, "token", token);
                    SharedPreferencesUtils.setParam(mRegisterActivity, "isLogin", true);
                    Intent intent = new Intent(mRegisterActivity, MainActivity.class);
                    mRegisterActivity.startActivity(intent);
                } else {
                    ToastUtil.show(mRegisterActivity, JSONUtil.getString(response, "msg", ""));
                }
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                LogUtils.e(e);
            }
        });
        okHttpUtil.requestCall(mRegisterActivity, "user.reg", list);
    }

    @Override
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_protocol:
                // 跳转用户协议界面
                Intent i = new Intent(mRegisterActivity, ProtocolActivity.class);
                mRegisterActivity.startActivity(i);
                break;
            case R.id.btn_getYZM:
                //发送验证码前的验证
                authSendYZM(etPhone.getText().toString());
                break;
            case R.id.btn_register_conform:
                // 注册前的校验
                String username = String.valueOf(etPhone.getText());
                String yzm = String.valueOf(etYZM.getText());
                String password = String.valueOf(etPassword.getText());
                String conformPassword = String.valueOf(etConformPassword.getText());
                authRegister(username, yzm, password, conformPassword);
                break;
        }
    }
}
