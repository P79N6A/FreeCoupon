package com.ks.freecoupon.module.view.account.presenter;

import android.content.Intent;
import android.widget.EditText;

import com.ks.basictools.base_interface.StringCallBack;
import com.ks.basictools.utils.JSONUtil;
import com.ks.basictools.utils.LogUtils;
import com.ks.basictools.utils.ToastUtil;
import com.ks.freecoupon.module.contract.account.ResetPasswordActivityContract;
import com.ks.freecoupon.module.view.account.activity.ResetPasswordActivity;
import com.ks.freecoupon.override.ToastKs;
import com.ks.freecoupon.utils.OkHttpUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

import static android.app.Activity.RESULT_OK;

/**
 * author：康少
 * date：2019/3/15
 * description：重设密码——逻辑层
 */
public class ResetPasswordActivityPresenter implements ResetPasswordActivityContract.IResetPasswordActivityPresenter {
    private ResetPasswordActivity mResetPasswordActivity;
    /*控件对象*/
    private EditText etNewPassword;
    private EditText etConformPassword;

    public ResetPasswordActivityPresenter(ResetPasswordActivity mResetPasswordActivity) {
        this.mResetPasswordActivity = mResetPasswordActivity;
        mResetPasswordActivity.setPresenter(this);
    }

    @Override
    public void start() {
        initObject();
    }

    private void initObject() {
        etNewPassword = mResetPasswordActivity.getEtNewPassword();
        etConformPassword = mResetPasswordActivity.getEtConformPassword();
    }

    @Override
    public void onViewClicked() {
        // 修改密码完成
        final String mobile = mResetPasswordActivity.getIntent().getStringExtra("mobile");
        String newPassword = etNewPassword.getText().toString();
        String conformPassword = etConformPassword.getText().toString();
        if (newPassword.equals("")) {
            ToastKs.show(mResetPasswordActivity, "请输入新密码");
            return;
        }
        if (!conformPassword.equals(newPassword)) {
            ToastKs.show(mResetPasswordActivity, "两次密码输入不一致");
            return;
        }
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        map.put("mobile", mobile);
        map.put("password", conformPassword);
        list.add(map);
        OkHttpUtil okHttpUtil = new OkHttpUtil(new StringCallBack() {
            @Override
            public void onResponse(String response, int id) {
                boolean status = JSONUtil.getBoolean(response, "status", false);
                if (status) {
                    ToastKs.show(mResetPasswordActivity, "密码重置成功");
                    //数据是使用Intent返回
                    Intent intent = new Intent();
                    //把返回数据存入Intent
                    intent.putExtra("reset_success", true);
                    //设置返回数据
                    mResetPasswordActivity.setResult(RESULT_OK, intent);
                    mResetPasswordActivity.finish();
                } else {
                    String msg = JSONUtil.getString(response, "msg", "");
                    ToastUtil.show(mResetPasswordActivity, msg);
                }
            }
            @Override
            public void onError(Call call, Exception e, int id) {
                LogUtils.e(e);
            }
        });
        okHttpUtil.requestCall(mResetPasswordActivity, "user.resetpassword", list);
    }
}
