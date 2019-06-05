package com.ks.freecoupon.utils;

import android.content.Context;
import android.content.Intent;

import com.ks.basictools.base_interface.StringCallBack;
import com.ks.basictools.utils.JSONUtil;
import com.ks.basictools.utils.ToastUtil;
import com.ks.freecoupon.R;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.builder.PostFormBuilder;
import com.zhy.http.okhttp.callback.StringCallback;
import com.zhy.http.okhttp.request.RequestCall;

import java.util.List;
import java.util.Map;

import okhttp3.Call;

/**
 * author：康少
 * date：2019/3/21
 * description：Http网络请求工具
 */
public class OkHttpUtil {
    private StringCallBack stringCallBack;

    public OkHttpUtil(StringCallBack stringCallBack) {
        this.stringCallBack = stringCallBack;
    }

    public void requestCall(final Context context, String method, List<Map<String, Object>> list) {
        RequestCall requestCall;
        PostFormBuilder postFormBuilder;
        postFormBuilder = OkHttpUtils.post()
                .url(context.getString(R.string.http_url));
        postFormBuilder.addParams("method", method);
        postFormBuilder.addParams("platform", "1");
        //循环添加参数
        if (list != null) {
            for (Map<String, Object> map : list) {
                for (String k : map.keySet()) {
                    postFormBuilder.addParams(k, map.get(k).toString());
                }
            }
        }
        requestCall = postFormBuilder
                .tag(context)
                .build();
        requestCall.execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                stringCallBack.onError(call, e, id);
            }

            @Override
            public void onResponse(String response, int id) {
                stringCallBack.onResponse(response, id);
            }
        });
    }
}
