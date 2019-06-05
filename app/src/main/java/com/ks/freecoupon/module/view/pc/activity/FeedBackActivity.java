package com.ks.freecoupon.module.view.pc.activity;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.ks.basictools.base_interface.StringCallBack;
import com.ks.basictools.publicView.TitleVarView;
import com.ks.basictools.utils.JSONUtil;
import com.ks.basictools.utils.LogUtils;
import com.ks.basictools.utils.ToastUtil;
import com.ks.freecoupon.R;
import com.ks.freecoupon.module.bean.ScoreGoods;
import com.ks.freecoupon.override.ToastKs;
import com.ks.freecoupon.utils.OkHttpUtil;
import com.ks.freecoupon.utils.SharedPreferencesUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

/**
 * Date:2019/3/27
 * Author：康少
 * Description：意见反馈
 */
public class FeedBackActivity extends TitleVarView implements TitleVarView.TitleBarOnRightClickListener {
    private Context mContext;
    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_back);
        mContext = this;
        editText = findViewById(R.id.et);
        setTitleBar(true, "意见反馈", "提交");
        setTitleBarRightTextColor(Color.parseColor("#00BFFF"));
        setRightTitleBarOnClickListener(this);
    }

    @Override
    public void onRightClick() {
        // 请求接口，提交反馈
        String content = editText.getText().toString();
        commitFeedBack(content);
    }

    private void commitFeedBack(String content) {
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        map.put("token", SharedPreferencesUtils.getParam(mContext, "token", ""));
        map.put("content", content);
        list.add(map);
        OkHttpUtil okHttpUtil = new OkHttpUtil(new StringCallBack() {
            @Override
            public void onResponse(String response, int id) {
                boolean status = JSONUtil.getBoolean(response, "status", false);
                if (!status) {
                    ToastUtil.show(mContext, JSONUtil.getString(response, "msg", ""));
                } else {
                    ToastKs.show(FeedBackActivity.this,"感谢您的宝贵意见，我们将努力做得更好！");
                    finish();
                }
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                LogUtils.e(e.getMessage());
            }
        });
        okHttpUtil.requestCall(mContext, "user.feedback", list);
    }
}
