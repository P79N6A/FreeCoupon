package com.ks.freecoupon.module.view.coupon.fragment;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ks.freecoupon.BaseFragment;
import com.ks.freecoupon.R;
import com.ks.freecoupon.module.contract.coupon.JingDongCouponFragmentContract;
import com.ks.freecoupon.module.view.coupon.presenter.JingDongCouponFragmentPresenter;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Date:2019/3/27
 * Author：康少
 * Description：京东领券——视图层
 */
public class JingDongCouponFragment extends BaseFragment implements JingDongCouponFragmentContract.IJingDongCouponFragment {
    private JingDongCouponFragmentContract.IJingDongCouponFragmentPresenter mIFragmentPresenter;
    private Context mContext;

    @BindView(R.id.tv_tip)
    TextView tvTip;
    @BindView(R.id.rl_empty)
    RelativeLayout rlEmpty;
    @BindView(R.id.rv_list)
    RecyclerView rvList;
    @BindView(R.id.srl_refresh)
    SwipeRefreshLayout srl_refresh;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    /**
     * Description：宿主方法
     */
    public Context getmContext() {
        return mContext;
    }
    public RecyclerView getRvList() {
        return rvList;
    }
    public RelativeLayout getRlEmpty() {
        return rlEmpty;
    }
    public SwipeRefreshLayout getSrl_refresh() {
        return srl_refresh;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_jing_dong_coupon, container, false);
        ButterKnife.bind(this, inflate);
        setTypeface();//设置字体样式
        new JingDongCouponFragmentPresenter(this);
        mIFragmentPresenter.start();
        return inflate;
    }

    /**
     * Description：设置字体样式
     */
    private void setTypeface() {
        //设置字体
        Typeface typeface = Typeface.createFromAsset(mContext.getAssets(), "fangzheng.ttf");
        tvTip.setTypeface(typeface);
    }

    @Override
    public void setPresenter(JingDongCouponFragmentContract.IJingDongCouponFragmentPresenter mIFragmentPresenter) {
        this.mIFragmentPresenter = mIFragmentPresenter;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mIFragmentPresenter.onDestroy();
    }
}
