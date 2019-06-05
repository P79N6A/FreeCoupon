package com.ks.freecoupon.module.view.coupon.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.ks.freecoupon.BaseFragment;
import com.ks.freecoupon.R;
import com.ks.freecoupon.ks_interface.OnCouponFragmentCompleteListener;
import com.ks.freecoupon.module.contract.coupon.CouponFragmentContract;
import com.ks.freecoupon.module.view.MainActivity;
import com.ks.freecoupon.module.view.coupon.presenter.CouponFragmentPresenter;
import com.ks.freecoupon.override.ToastKs;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Date:2019/3/7
 * Author：康少
 * Description：领券——视图层
 */
public class CouponFragment extends BaseFragment implements CouponFragmentContract.ICouponFragment {
    private CouponFragmentContract.ICouponFragmentPresenter mIFragmentPresenter;
    private Context mContext;
    private OnCouponFragmentCompleteListener onCouponFragmentCompleteListener;

    public void setOnCouponFragmentCompleteListener(OnCouponFragmentCompleteListener onCouponFragmentCompleteListener) {
        this.onCouponFragmentCompleteListener = onCouponFragmentCompleteListener;
    }

    @BindView(R.id.ll_container)
    LinearLayout llContainer;
    @BindView(R.id.tab)
    TabLayout tab;
    @BindView(R.id.viewpager)
    ViewPager viewpager;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_coupon, container, false);
        ButterKnife.bind(this, inflate);
        new CouponFragmentPresenter(this);
        mIFragmentPresenter.start();
        if (onCouponFragmentCompleteListener != null) {
            onCouponFragmentCompleteListener.onComplete();
        }
        return inflate;
    }

    /**
     * 宿主方法
     */
    public TabLayout getTab() {
        return tab;
    }
    public ViewPager getViewpager() {
        return viewpager;
    }

    public void onChecked(int type) {
        if (mIFragmentPresenter != null) {
            mIFragmentPresenter.onChecked(type);
        } else {
            ToastKs.show((MainActivity) mContext, "尚未加载完成，请稍后" + R.string.string_ddd);
        }
    }

    @Override
    public void setPresenter(CouponFragmentContract.ICouponFragmentPresenter mIFragmentPresenter) {
        this.mIFragmentPresenter = mIFragmentPresenter;
    }
}
