package com.ks.freecoupon.module.view.pc.presenter;

import android.content.Intent;
import android.widget.EditText;

import com.ks.basictools.base_interface.StringCallBack;
import com.ks.basictools.publicView.TitleVarView;
import com.ks.basictools.utils.JSONUtil;
import com.ks.basictools.utils.ToastUtil;
import com.ks.freecoupon.module.bean.User;
import com.ks.freecoupon.module.contract.pc.UpdatePersonInfoActivityContract;
import com.ks.freecoupon.module.view.pc.activity.UpdatePersonInfoActivity;
import com.ks.freecoupon.override.ToastKs;
import com.ks.freecoupon.utils.OkHttpUtil;
import com.ks.freecoupon.utils.SharedPreferencesUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

import static android.app.Activity.RESULT_OK;

/**
 * author：康少
 * date：2019/3/26
 * description：修改个人信息——逻辑层
 */
public class UpdatePersonInfoActivityPresenter implements UpdatePersonInfoActivityContract.IUpdatePersonInfoActivityPresenter, TitleVarView.TitleBarOnRightClickListener {
    private UpdatePersonInfoActivity mUpdatePersonInfoActivity;
    private EditText editText;
    /*参数*/
    private String type;

    public UpdatePersonInfoActivityPresenter(UpdatePersonInfoActivity mUpdatePersonInfoActivity) {
        this.mUpdatePersonInfoActivity = mUpdatePersonInfoActivity;
        mUpdatePersonInfoActivity.setPresenter(this);
    }

    @Override
    public void start() {
        initObject();
        mUpdatePersonInfoActivity.setRightTitleBarOnClickListener(this);
    }

    private void initObject() {
        editText = mUpdatePersonInfoActivity.getEt_content();
        type = mUpdatePersonInfoActivity.getIntent().getStringExtra("type");
    }

    @Override
    public void onRightClick() {
        final String textValue = editText.getText().toString();
        if (textValue.equals("")) {
            ToastUtil.show(mUpdatePersonInfoActivity,"内容为空！");
            return;
        }
        // 请求接口，保存数据。成功后返回
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        map.put("token", SharedPreferencesUtils.getParam(mUpdatePersonInfoActivity, "token", ""));
        switch (type) {
            case "nickname":
                map.put("nickname", textValue);
                break;
        }
        map.put("ship_name", User.getInstance().getShip_name());
        map.put("ship_mobile", User.getInstance().getShip_mobile());
        map.put("ship_area", User.getInstance().getShip_area());
        map.put("ship_address", User.getInstance().getShip_address());
        list.add(map);
        OkHttpUtil okHttpUtil = new OkHttpUtil(new StringCallBack() {
            @Override
            public void onResponse(String response, int id) {
                boolean status = JSONUtil.getBoolean(response, "status", false);
                if (status) {
                    switch (type) {
                        case "nickname":
                            User.getInstance().setNickname(textValue);
                            //PCFragmentPresenter 更新 用户名
                            EventBus.getDefault().post(User.getInstance());
                            //设置返回数据
                            Intent intent = new Intent();
                            intent.putExtra("update_data", textValue);
                            intent.putExtra("type", type);
                            mUpdatePersonInfoActivity.setResult(RESULT_OK, intent);
                            mUpdatePersonInfoActivity.finish();
                            break;
                    }
                }
            }

            @Override
            public void onError(Call call, Exception e, int id) {
            }
        });
        okHttpUtil.requestCall(mUpdatePersonInfoActivity, "user.editinfo", list);
    }
}
