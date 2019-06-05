package com.ks.freecoupon.module.view.pc.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ks.basictools.ActivityCollector;
import com.ks.basictools.AppManager;
import com.ks.basictools.base_interface.StringCallBack;
import com.ks.basictools.publicView.TitleVarView;
import com.ks.basictools.utils.JSONUtil;
import com.ks.basictools.utils.LogUtils;
import com.ks.basictools.utils.ToastUtil;
import com.ks.freecoupon.BaseActivity;
import com.ks.freecoupon.R;
import com.ks.freecoupon.module.bean.GiftGoods;
import com.ks.freecoupon.module.bean.GiftGoodsList;
import com.ks.freecoupon.module.bean.User;
import com.ks.freecoupon.override.PieView;
import com.ks.freecoupon.utils.OkHttpUtil;
import com.ks.freecoupon.utils.SharedPreferencesUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import okhttp3.Call;

/**
 * Date:2019/4/4
 * Author：康少
 * Description：积分商城——抽奖Activity
 */
public class ChouJiangActivity extends BaseActivity implements View.OnClickListener {

    private PieView pv;

    private boolean isRunning = false;
    private int[] key = new int[GiftGoodsList.getInstance().getGiftGoodsList().size()];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chou_jiang);
        /*设置状态栏*/
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decorView = getWindow().getDecorView();
            //状态栏中的文字颜色和图标颜色为深色，需要android系统6.0以上，而且目前只有一种可以修改（一种是深色，一种是浅色即白色）
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            //设置状态栏的颜色
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        findId();
        initView();
    }

    private void findId() {
        pv = findViewById(R.id.pv);
        findViewById(R.id.image).setOnClickListener(this);
        findViewById(R.id.iv_back).setOnClickListener(this);
        findViewById(R.id.tv_record).setOnClickListener(this);
    }

    /**
     * Description：获取抽奖结果
     */
    private void getResult() {
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        map.put("token", SharedPreferencesUtils.getParam(ChouJiangActivity.this, "token", ""));
        list.add(map);
        OkHttpUtil okHttpUtil = new OkHttpUtil(new StringCallBack() {
            @Override
            public void onResponse(String response, int id) {
                boolean status = JSONUtil.getBoolean(response, "status", false);
                if (status) {
                    int data = JSONUtil.getInt(response, "data", -1);
                    List<GiftGoods> giftGoodsList = GiftGoodsList.getInstance().getGiftGoodsList();
                    if (giftGoodsList != null && giftGoodsList.size() > 0) {
                        for (int i = 0;i<giftGoodsList.size();i++) {
                            key[i] = i;
                        }
                        for (int i = 0;i<giftGoodsList.size();i++) {
                            if (giftGoodsList.get(i).getId().equals(data + "")) {
                                pv.rotate(i + 2);
                                break;
                            }
                        }
                    }
                } else {
                    String msg = JSONUtil.getString(response, "msg", "");
                    ToastUtil.show(ChouJiangActivity.this, msg);
                }
            }
            @Override
            public void onError(Call call, Exception e, int id) {
                LogUtils.e(e.getMessage());
            }
        });
        okHttpUtil.requestCall(ChouJiangActivity.this, "user.getlotterygift", list);
    }

    @SuppressLint("HandlerLeak")
    private void initView() {
//        pv.setValue(GiftGoods.getInstance().getName());
        pv.setListener(new PieView.RotateListener() {
            @Override
            public void value(String s) {
                isRunning = false;
                if (ActivityCollector.isActivityExist(ChouJiangActivity.class)) {
                    AlertDialog alertDialog = new AlertDialog.Builder(ChouJiangActivity.this)
                            .setTitle("鹿死谁手呢？")
                            .setMessage(s)
                            .setIcon(R.drawable.f015)
                            .setNegativeButton("确定", null)
                            .show();
                    alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorPrimary));
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.pv:
                break;
            case R.id.image:
                if (!isRunning) {
//                    pv.rotate(2);
                    getResult();
//                    Random random = new Random();
//                    pv.rotate(i[random.nextInt(4)]);
//                    pv.rotate(1);
                }
                isRunning = true;
                break;
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_record:
                Intent intent = new Intent(this, ChouJiangRecordActivity.class);
                startActivity(intent);
                break;

        }
    }
}
