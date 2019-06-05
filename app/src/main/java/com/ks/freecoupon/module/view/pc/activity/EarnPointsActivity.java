package com.ks.freecoupon.module.view.pc.activity;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.EditText;

import com.ks.basictools.base_interface.StringCallBack;
import com.ks.basictools.publicView.TitleVarView;
import com.ks.basictools.utils.JSONUtil;
import com.ks.basictools.utils.LogUtils;
import com.ks.basictools.utils.ToastUtil;
import com.ks.freecoupon.R;
import com.ks.freecoupon.module.bean.User;
import com.ks.freecoupon.override.ToastKs;
import com.ks.freecoupon.utils.OkHttpUtil;
import com.ks.freecoupon.utils.SharedPreferencesUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

/**
 * Date:2019/4/26
 * Author：康少
 * Description：赚取积分
 */
public class EarnPointsActivity extends TitleVarView {

    private Context mContext;
    private EditText etContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_earn_points);
        mContext = this;
        setTitleBar(true, "赚取积分", "确定");
        setTitleBarRightTextColor(Color.parseColor("#00BFFF"));
        setRightTitleBarOnClickListener(new TitleBarOnRightClickListener() {
            @Override
            public void onRightClick() {
                if (etContent.getText().toString().equals("")) {
                    ToastUtil.show(mContext,"请输入订单号");
                    return;
                }
                recordCouponOrder(etContent.getText().toString());
            }
        });
        findId();
        start();
    }

    private void findId() {
        etContent = findViewById(R.id.et_content);

    }

    private void start() {
        etContent.setHint("请输入淘宝/京东订单号");
    }

    /**
     * Description：赚取积分提交
     */
    private void recordCouponOrder(String orderId) {
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        map.put("token", SharedPreferencesUtils.getParam(mContext, "token", ""));
        map.put("order_id", orderId);
        list.add(map);
        OkHttpUtil okHttpUtil = new OkHttpUtil(new StringCallBack() {
            @Override
            public void onResponse(String response, int id) {
                boolean status = JSONUtil.getBoolean(response, "status", false);
                if (status) {
                    // PCFragment 更新积分数
                    int score = JSONUtil.getInt(response, "data", 0);
                    User.getInstance().setPoint(User.getInstance().getPoint() + score);
                    EventBus.getDefault().post(User.getInstance());
                    ToastKs.show(EarnPointsActivity.this, "恭喜您，成功赚取了" + score + "个积分");
                    finish();
                } else {
                    ToastUtil.show(mContext, JSONUtil.getString(response, "msg", ""));
                }
            }
            @Override
            public void onError(Call call, Exception e, int id) {
                LogUtils.e(e.getMessage());
            }
        });
        okHttpUtil.requestCall(mContext, "user.recordcouponorder", list);
    }
}
