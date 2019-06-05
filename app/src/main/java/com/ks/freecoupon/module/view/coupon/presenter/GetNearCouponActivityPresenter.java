package com.ks.freecoupon.module.view.coupon.presenter;

import android.annotation.SuppressLint;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ks.basictools.base_interface.StringCallBack;
import com.ks.basictools.utils.JSONUtil;
import com.ks.basictools.utils.ToastUtil;
import com.ks.freecoupon.R;
import com.ks.freecoupon.module.bean.NearGoods;
import com.ks.freecoupon.module.bean.User;
import com.ks.freecoupon.module.contract.coupon.GetNearCouponActivityContract;
import com.ks.freecoupon.module.view.coupon.activity.GetNearCouponActivity;
import com.ks.freecoupon.override.RoundTransform;
import com.ks.freecoupon.override.ToastKs;
import com.ks.freecoupon.utils.OkHttpUtil;
import com.ks.freecoupon.utils.ScreenUtils;
import com.ks.freecoupon.utils.SharedPreferencesUtils;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bingoogolapple.bgabanner.BGABanner;
import okhttp3.Call;

/**
 * author：康少
 * date：2019/4/12
 * description：附近领券——逻辑层
 */
public class GetNearCouponActivityPresenter implements GetNearCouponActivityContract.IGetNearCouponActivityPresenter {
    private GetNearCouponActivity mGetNearCouponActivity;
    /*控件对象*/
    private BGABanner banner;
    private TextView tvCouponTitle;
    private TextView tvStime;
    private TextView tvEtime;
    private TextView tvAddress;
    private TextView tvCouponContent;
    private TextView tvYuanPrice;
    private TextView tvPhone;

    //参数
    private NearGoods nearGoods;

    public GetNearCouponActivityPresenter(GetNearCouponActivity mGetNearCouponActivity) {
        this.mGetNearCouponActivity = mGetNearCouponActivity;
        mGetNearCouponActivity.setPresenter(this);
    }

    @Override
    public void start() {
        initObject();
        applyView();
    }

    private void initObject() {
        banner = mGetNearCouponActivity.getBanner();
        tvCouponTitle = mGetNearCouponActivity.getTvCouponTitle();
        tvStime = mGetNearCouponActivity.getTvStime();
        tvEtime = mGetNearCouponActivity.getTvEtime();
        tvAddress = mGetNearCouponActivity.getTvAddress();
        tvCouponContent = mGetNearCouponActivity.getTvCouponContent();
        tvYuanPrice = mGetNearCouponActivity.getTvYuanPrice();
        tvPhone = mGetNearCouponActivity.getTvPhone();

        /*设置banner宽高比为16:9*/
        int height16_9 = ScreenUtils.getHeight16_9(mGetNearCouponActivity);
        ViewGroup.LayoutParams layoutParams = banner.getLayoutParams();
        layoutParams.height = height16_9;
        banner.setLayoutParams(layoutParams);

        nearGoods = (NearGoods) mGetNearCouponActivity.getIntent().getSerializableExtra("nearGoods");
    }

    @SuppressLint("SetTextI18n")
    private void applyView() {
        /*加载banner图*/
        List<String> imageList = new ArrayList<>();
        imageList.add(nearGoods.getImg());
        banner.setAdapter(new BGABanner.Adapter<ImageView, String>() {
            @Override
            public void fillBannerItem(BGABanner banner, ImageView itemView, String model, int position) {
                if (model != null && !model.equals("")) {
                    Picasso.with(mGetNearCouponActivity)    //context
                            .load(model)     //图片加载地址
                            .transform(new RoundTransform(10))
                            .error(android.R.drawable.ic_menu_delete)   //图片记载失败时显示的页面
                            .fit()      //智能展示图片，对于图片的大小和imageview的尺寸进行了测量，计算出最佳的大小和最佳的质量显示出来
                            .into(itemView);    //需要加载图片的控件
                }
            }
        });
        banner.setData(imageList, Collections.singletonList(""));
        banner.setAutoPlayAble(false);//不自动播放

        /*文本内容*/
        tvCouponTitle.setText(nearGoods.getName());
        tvCouponContent.setText(nearGoods.getCoupon_content());
        tvYuanPrice.setText("满" + nearGoods.getMkt_price() + "元可用");
        tvStime.setText(nearGoods.getStime());
        tvEtime.setText(nearGoods.getEtime());
        tvAddress.setText(nearGoods.getAddress());
        tvPhone.setText(nearGoods.getTel());
    }

    /**
     * Description：获取优惠券
     */
    private void getCoupon() {
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        map.put("token", SharedPreferencesUtils.getParam(mGetNearCouponActivity, "token", ""));
        map.put("promotion_id", nearGoods.getId());
        list.add(map);
        OkHttpUtil okHttpUtil = new OkHttpUtil(new StringCallBack() {
            @Override
            public void onResponse(String response, int id) {
                boolean status = JSONUtil.getBoolean(response, "status", false);
                if (status) {
                    ToastKs.show(mGetNearCouponActivity, "领取成功，已放到我的卡券");
                    // 更新卡券数量
                    User.getInstance().setCoupon(User.getInstance().getCoupon() + 1);
                    //PCFragmentPresenter 更新 卡券数量
                    EventBus.getDefault().post(User.getInstance());
                } else {
                    ToastUtil.show(mGetNearCouponActivity, JSONUtil.getString(response, "msg", ""));
                }
            }
            @Override
            public void onError(Call call, Exception e, int id) {
                ToastUtil.show(mGetNearCouponActivity, "请求错误，请稍后再试。错误信息：user.getcoupon");
            }
        });
        okHttpUtil.requestCall(mGetNearCouponActivity, "user.getcoupon", list);
    }

    @Override
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_copy_address:
                ClipboardManager cmb = (ClipboardManager) mGetNearCouponActivity.getSystemService(Context.CLIPBOARD_SERVICE);
                if (cmb != null) {
                    cmb.setText(nearGoods.getAddress());
                    ToastKs.show(mGetNearCouponActivity, "复制成功");
                }
                break;
            case R.id.tv_get_coupon:
                getCoupon();
                break;
            case R.id.tv_call://拨号
                String phone_number = tvPhone.getText().toString();
                //删除字符串首部和尾部的空格
                phone_number = phone_number.trim();
                if (!phone_number.equals("")) {
                    //调用系统的拨号服务实现电话拨打功能
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone_number));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mGetNearCouponActivity.startActivity(intent);
                } else {
                    ToastKs.show(mGetNearCouponActivity,"暂时没有商家电话哟");
                }
                break;
        }
    }
}
