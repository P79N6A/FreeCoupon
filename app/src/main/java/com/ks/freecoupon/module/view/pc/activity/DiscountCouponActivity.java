package com.ks.freecoupon.module.view.pc.activity;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ks.basictools.publicView.TitleVarView;
import com.ks.freecoupon.R;
import com.ks.freecoupon.module.contract.pc.DiscountCouponActivityContract;
import com.ks.freecoupon.module.view.pc.presenter.DiscountCouponActivityPresenter;

/**
 * Date:2019/3/13
 * Author：康少
 * Description：我的优惠券Activity——视图层
 */
public class DiscountCouponActivity extends TitleVarView implements DiscountCouponActivityContract.IDiscountCouponActivity {
    private DiscountCouponActivityContract.IDiscountCouponActivityPresenter mIActivityPresenter;

    private RecyclerView rv_RecyclerView;
    private RelativeLayout rl_empty;
    private TextView tvTip;
    private SwipeRefreshLayout srl_refresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discount_coupon);
        rv_RecyclerView = findViewById(R.id.rv_RecyclerView);
        rl_empty = findViewById(R.id.rl_empty);
        srl_refresh = findViewById(R.id.srl_refresh);
        tvTip = findViewById(R.id.tv_tip);
        setTypeface();//设置字体样式
        setTitleBar(true, "我的优惠券", "");
        new DiscountCouponActivityPresenter(this);
        mIActivityPresenter.start();
    }

    /**
     * Description：宿主方法
     */
    public RecyclerView getRv_RecyclerView() {
        return rv_RecyclerView;
    }
    public RelativeLayout getRl_empty() {
        return rl_empty;
    }
    public SwipeRefreshLayout getSrl_refresh() {
        return srl_refresh;
    }

    /**
     * Description：设置字体样式
     */
    private void setTypeface() {
        //设置字体
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fangzheng.ttf");
        tvTip.setTypeface(typeface);
    }
    @Override
    public void setPresenter(DiscountCouponActivityContract.IDiscountCouponActivityPresenter mIActivityPresenter) {
        this.mIActivityPresenter = mIActivityPresenter;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mIActivityPresenter.onDestroy();
    }
}
