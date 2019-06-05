package com.ks.freecoupon.module.view.pc.activity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ks.basictools.base_interface.StringCallBack;
import com.ks.basictools.publicView.TitleVarView;
import com.ks.basictools.utils.JSONUtil;
import com.ks.basictools.utils.LogUtils;
import com.ks.basictools.utils.ToastUtil;
import com.ks.freecoupon.R;
import com.ks.freecoupon.module.bean.Entity;
import com.ks.freecoupon.module.bean.User;
import com.ks.freecoupon.module.view.account.activity.LoginActivity;
import com.ks.freecoupon.module.view.entity.activity.AddressActivity;
import com.ks.freecoupon.override.RoundTransform;
import com.ks.freecoupon.override.ToastKs;
import com.ks.freecoupon.utils.BaseUtils;
import com.ks.freecoupon.utils.GetJsonDataUtil;
import com.ks.freecoupon.utils.OkHttpUtil;
import com.ks.freecoupon.utils.SharedPreferencesUtils;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

/**
 * Date:2019/4/9
 * Author：康少
 * Description：确认收货订单
 */
public class ConformOrderActivity extends TitleVarView implements View.OnClickListener {

    private TextView tv_name;//收货人姓名
    private TextView tv_phone;//联系电话
    private TextView tv_address;//收货地址
    private ImageView iv_goods_image;//商品图片
    private TextView tv_title;//商品Title
    private LinearLayout ll_has_address;//有地址
    private LinearLayout ll_no_address;//无地址
    //参数
    private Entity entity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conform_order);
        setTitleBar(true, "确认订单", "");
        //注册EventBus
        EventBus.getDefault().register(this);
        findId();
        start();
    }

    private void findId() {
        tv_name = findViewById(R.id.tv_name);
        tv_phone = findViewById(R.id.tv_phone);
        tv_address = findViewById(R.id.tv_address);
        iv_goods_image = findViewById(R.id.iv_goods_image);
        tv_title = findViewById(R.id.tvTitle);
        ll_has_address = findViewById(R.id.ll_has_address);
        ll_no_address = findViewById(R.id.ll_no_address);
        findViewById(R.id.ll_choose_address).setOnClickListener(this);
        findViewById(R.id.tv_conform).setOnClickListener(this);
    }

    private void start() {
        entity = (Entity) getIntent().getSerializableExtra("entity");
        initView();
    }

    @SuppressLint("SetTextI18n")
    private void initView() {
        if (User.mInstance.getShip_area().equals("")) {
            toggle(false);
        } else {
            toggle(true);
            tv_name.setText(User.getInstance().getShip_name());
            tv_phone.setText(User.getInstance().getShip_mobile());
            tv_address.setText(User.getInstance().getShip_area() + User.getInstance().getShip_address());
        }
        tv_title.setText(entity.getTitle());
        List<String> imageList = entity.getImageList();
        if (imageList != null && imageList.size() > 0) {
            String image = imageList.get(0);
            if (!image.equals("")) {
                Picasso.with(this)    //context
                        .load(image)     //图片加载地址
                        .transform(new RoundTransform(10))
                        .error(android.R.drawable.ic_menu_delete)   //图片记载失败时显示的页面
                        .fit()      //智能展示图片，对于图片的大小和imageview的尺寸进行了测量，计算出最佳的大小和最佳的质量显示出来
                        .into(iv_goods_image);    //需要加载图片的控件
            }
        }
    }

    /**
     * Description：更新地址视图
     * 由 AddressActivityPresenter 发出通知——更新收货人信息
     */
    @SuppressLint("SetTextI18n")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateView(User user) {
        if (!User.mInstance.getShip_area().equals("")) {
            toggle(true);
            tv_name.setText(user.getShip_name());
            tv_phone.setText(user.getShip_mobile());
            tv_address.setText(user.getShip_area() + user.getShip_address());
        }
    }

    /**
     * Description：切换视图
     */
    private void toggle(boolean hasAddress) {
        if (hasAddress) {
            ll_has_address.setVisibility(View.VISIBLE);
            ll_no_address.setVisibility(View.GONE);
        } else {
            ll_has_address.setVisibility(View.GONE);
            ll_no_address.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_choose_address://添加、修改地址
                Intent intent = new Intent(this, AddressActivity.class);
                this.startActivity(intent);
                break;
            case R.id.tv_conform://提交订单
                AlertDialog dialog = new AlertDialog.Builder(this)
                        .setTitle("温馨提示")
                        .setMessage("当月有且仅有一次领取机会，确认领取吗？")
                        .setPositiveButton("确定",new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                conformOrder();
                            }
                        })
                        .setNegativeButton("取消",null)
                        .create();
                dialog.show();
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorPrimary));
                dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorPrimary));
                break;
        }
    }

    /**
     * Description：提交订单
     */
    private void conformOrder() {
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        map.put("token", SharedPreferencesUtils.getParam(ConformOrderActivity.this, "token", ""));
        list.add(map);
        OkHttpUtil okHttpUtil = new OkHttpUtil(new StringCallBack() {
            @Override
            public void onResponse(String response, int id) {
                boolean status = JSONUtil.getBoolean(response, "status", false);
                if (status) {
                    ToastKs.show(ConformOrderActivity.this, "兑换成功，当月剩余兑换次数：0");
                    finish();
                } else {
                    ToastUtil.show(ConformOrderActivity.this, JSONUtil.getString(response, "msg", ""));
                }
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                LogUtils.e(e.getMessage());
            }
        });
        okHttpUtil.requestCall(ConformOrderActivity.this, "goods.receivefreegoods", list);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //解除EventBus注册
        if(EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }
}
