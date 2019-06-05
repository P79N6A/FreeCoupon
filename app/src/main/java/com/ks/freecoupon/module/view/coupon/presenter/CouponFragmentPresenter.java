package com.ks.freecoupon.module.view.coupon.presenter;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;

import com.ks.basictools.utils.LogUtils;
import com.ks.basictools.utils.ToastUtil;
import com.ks.freecoupon.module.adapter.ViewPagerAdapter;
import com.ks.freecoupon.module.contract.coupon.CouponFragmentContract;
import com.ks.freecoupon.module.view.coupon.fragment.CouponFragment;
import com.ks.freecoupon.module.view.coupon.fragment.JingDongCouponFragment;
import com.ks.freecoupon.module.view.coupon.fragment.NearShopFragment;
import com.ks.freecoupon.module.view.coupon.fragment.TaoBaoCouponFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * author：康少
 * date：2019/3/7
 * description：领券——逻辑层
 */
public class CouponFragmentPresenter implements CouponFragmentContract.ICouponFragmentPresenter {
    private CouponFragment mCouponFragment;
    /*控件对象*/
    private TabLayout tab;
    private ViewPager viewpager;
    /*参数*/
    private List<Fragment> viewList;
    private NearShopFragment mNearShopFragment;
    private TaoBaoCouponFragment mTaoBaoCouponFragment;
    private JingDongCouponFragment mJingDongCouponFragment;

    public CouponFragmentPresenter(CouponFragment mCouponFragment) {
        this.mCouponFragment = mCouponFragment;
        mCouponFragment.setPresenter(this);
    }

    @Override
    public void start() {
        initObject();
        initView();
    }

    private void initObject() {
        tab = mCouponFragment.getTab();
        viewpager = mCouponFragment.getViewpager();
        viewList = new ArrayList<>();
        mNearShopFragment = new NearShopFragment();
        mTaoBaoCouponFragment = new TaoBaoCouponFragment();
        mJingDongCouponFragment = new JingDongCouponFragment();
    }

    private void initView() {
        viewList.add(mTaoBaoCouponFragment);
        viewList.add(mJingDongCouponFragment);
        viewList.add(mNearShopFragment);
        if (mCouponFragment.getActivity() != null) {
            /*适配器部分*/
            ViewPagerAdapter pagerAdapter = new ViewPagerAdapter(mCouponFragment.getActivity().getSupportFragmentManager(), viewList);
            //给ViewPager设置适配器
            viewpager.setAdapter(pagerAdapter);
            //当前页面两侧缓存/预加载的页面数目。当页面切换时，当前页面相邻两侧的numbers页面不会被销毁
            viewpager.setOffscreenPageLimit(2);
            //将TabLayout和ViewPager关联起来。
            tab.setupWithViewPager(viewpager);
            //设置可以滑动
            tab.setTabMode(TabLayout.MODE_SCROLLABLE);
            //充满整个屏幕
            tab.setTabMode(TabLayout.MODE_FIXED);
        } else {
            LogUtils.e("页面错误");
            ToastUtil.show(mCouponFragment.getContext(), "页面错误");
        }
    }

    @Override
    public void onChecked(int type) {
        if (tab.getTabCount() >= 3) {
            TabLayout.Tab tabAt = tab.getTabAt(type);
            if (tabAt != null) {
                tabAt.select();
            }
        }
    }
}
