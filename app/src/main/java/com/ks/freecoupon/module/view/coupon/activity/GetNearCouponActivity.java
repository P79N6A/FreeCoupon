package com.ks.freecoupon.module.view.coupon.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.ks.basictools.publicView.TitleVarView;
import com.ks.freecoupon.R;
import com.ks.freecoupon.module.contract.coupon.GetNearCouponActivityContract;
import com.ks.freecoupon.module.view.coupon.presenter.GetNearCouponActivityPresenter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bingoogolapple.bgabanner.BGABanner;

/**
 * Date:2019/4/12
 * Author：康少
 * Description：领取附近优惠券——视图层
 */
public class GetNearCouponActivity extends TitleVarView implements GetNearCouponActivityContract.IGetNearCouponActivity, View.OnClickListener {
    private GetNearCouponActivityContract.IGetNearCouponActivityPresenter mIActivityPresenter;

    private BGABanner banner;
    private TextView tvCouponTitle;
    private TextView tvStime;
    private TextView tvEtime;
    private TextView tvAddress;
    private TextView tvCouponContent;
    private TextView tvYuanPrice;
    private TextView tvPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_near_coupon);
        setTitleBar(true, "详情", "");
        findId();
        new GetNearCouponActivityPresenter(this);
        mIActivityPresenter.start();
    }

    private void findId() {
        tvCouponContent = findViewById(R.id.tv_coupon_content);
        tvYuanPrice = findViewById(R.id.tv_yuanPrice);
        banner = findViewById(R.id.banner);
        tvCouponTitle = findViewById(R.id.tv_coupon_title);
        tvStime = findViewById(R.id.tv_stime);
        tvEtime = findViewById(R.id.tv_etime);
        tvAddress = findViewById(R.id.tv_address);
        tvPhone = findViewById(R.id.tv_phone);
        findViewById(R.id.tv_copy_address).setOnClickListener(this);
        findViewById(R.id.tv_get_coupon).setOnClickListener(this);
        findViewById(R.id.tv_call).setOnClickListener(this);
    }

    /**
     * Description：宿主方法
     */
    public BGABanner getBanner() {
        return banner;
    }
    public TextView getTvCouponTitle() {
        return tvCouponTitle;
    }
    public TextView getTvStime() {
        return tvStime;
    }
    public TextView getTvEtime() {
        return tvEtime;
    }
    public TextView getTvAddress() {
        return tvAddress;
    }
    public TextView getTvCouponContent() {
        return tvCouponContent;
    }
    public TextView getTvYuanPrice() {
        return tvYuanPrice;
    }
    public TextView getTvPhone() {
        return tvPhone;
    }

    @Override
    public void setPresenter(GetNearCouponActivityContract.IGetNearCouponActivityPresenter mIActivityPresenter) {
        this.mIActivityPresenter = mIActivityPresenter;
    }

    @Override
    public void onClick(View v) {
        mIActivityPresenter.onViewClicked(v);
    }
}
