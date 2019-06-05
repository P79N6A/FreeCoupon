package com.ks.freecoupon.module.view;

import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.ks.basictools.AppManager;
import com.ks.basictools.utils.ToastUtil;
import com.ks.freecoupon.R;
import com.ks.freecoupon.ks_interface.OnCouponFragmentCompleteListener;
import com.ks.freecoupon.module.contract.MainActivityContract;
import com.ks.freecoupon.module.view.coupon.fragment.CouponFragment;
import com.ks.freecoupon.module.view.entity.fragment.EntityFragment;
import com.ks.freecoupon.module.view.homePage.fragment.HomePageFragment;
import com.ks.freecoupon.module.view.pc.fragment.PCFragment;
import com.ks.freecoupon.module.view.search.fragment.SearchFragment;

/**
 * author：康少
 * date：2019/3/7
 * description：主要Activity——逻辑层
 */
public class MainActivityPresenter implements MainActivityContract.IMainActivityPresenter, RadioGroup.OnCheckedChangeListener {
    private MainActivity mMainActivity;

    /*控件对象*/
    private RadioGroup tab_menu;
    private RadioButton rb_homePage;
    private RadioButton rb_mine;
    private RadioButton rb_coupon;
    private RadioButton rbSearch;
    private RadioButton rbEntity;

    /*fragment管理者*/
    private FragmentManager fManager;
    private FragmentTransaction fTransaction;

    /*参数*/
    private HomePageFragment mHomePageFragment;//首页
    private CouponFragment mCouponFragment;//领券中心
    private SearchFragment mSearchFragment;//搜索
    private EntityFragment mEntityFragment;//搜索
    private PCFragment mPCFragment;//个人中心
    private boolean isFirst = true;//是否 第一次加载CouponFragment

    public MainActivityPresenter(MainActivity mMainActivity) {
        this.mMainActivity = mMainActivity;
        mMainActivity.setPresenter(this);
    }

    @Override
    public void start() {
        init();
        listener();
    }

    private void init() {
        tab_menu = mMainActivity.getTab_menu();
        rb_homePage = mMainActivity.getRb_homePage();
        rb_mine = mMainActivity.getRb_mine();
        rb_coupon = mMainActivity.getRb_coupon();
        rbSearch = mMainActivity.getRbSearch();
        rbEntity = mMainActivity.getRbEntity();

        fManager = mMainActivity.getSupportFragmentManager();
    }

    private void listener() {
        tab_menu.setOnCheckedChangeListener(this);
        rb_homePage.setChecked(true);//默认选中首页
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        fTransaction = fManager.beginTransaction();
        if (rb_homePage.isChecked()) {
            if (mHomePageFragment == null) {
                mHomePageFragment = new HomePageFragment();
                fTransaction.add(R.id.fl_container, mHomePageFragment, "mHomePageFragment");
            } else {
                fTransaction.show(mHomePageFragment);
            }
            if (mCouponFragment != null)
                fTransaction.hide(mCouponFragment);
            if (mSearchFragment != null)
                fTransaction.hide(mSearchFragment);
            if (mEntityFragment != null)
                fTransaction.hide(mEntityFragment);
            if (mPCFragment != null)
                fTransaction.hide(mPCFragment);
        } else if (rb_coupon.isChecked()) {
            if (mCouponFragment == null) {
                mCouponFragment = new CouponFragment();
                fTransaction.add(R.id.fl_container, mCouponFragment, "mCouponFragment");
            } else {
                fTransaction.show(mCouponFragment);
            }
            if (mHomePageFragment != null)
                fTransaction.hide(mHomePageFragment);
            if (mSearchFragment != null)
                fTransaction.hide(mSearchFragment);
            if (mEntityFragment != null)
                fTransaction.hide(mEntityFragment);
            if (mPCFragment != null) {
                fTransaction.hide(mPCFragment);
            }
        } else if (rbSearch.isChecked()) {
            if (mSearchFragment == null) {
                mSearchFragment = new SearchFragment();
                fTransaction.add(R.id.fl_container, mSearchFragment, "mSearchFragment");
            } else {
                fTransaction.show(mSearchFragment);
            }
            if (mHomePageFragment != null)
                fTransaction.hide(mHomePageFragment);
            if (mCouponFragment != null)
                fTransaction.hide(mCouponFragment);
            if (mEntityFragment != null)
                fTransaction.hide(mEntityFragment);
            if (mPCFragment != null)
                fTransaction.hide(mPCFragment);
        } else if (rbEntity.isChecked()) {
            if (mEntityFragment == null) {
                mEntityFragment = new EntityFragment();
                fTransaction.add(R.id.fl_container, mEntityFragment, "mEntityFragment");
            } else {
                fTransaction.show(mEntityFragment);
            }
            if (mHomePageFragment != null)
                fTransaction.hide(mHomePageFragment);
            if (mCouponFragment != null)
                fTransaction.hide(mCouponFragment);
            if (mSearchFragment != null)
                fTransaction.hide(mSearchFragment);
            if (mPCFragment != null)
                fTransaction.hide(mPCFragment);
        } else if (rb_mine.isChecked()) {
            if (mPCFragment == null) {
                mPCFragment = new PCFragment();
                fTransaction.add(R.id.fl_container, mPCFragment, "mPCFragment");
            } else {
                fTransaction.show(mPCFragment);
            }
            if (mHomePageFragment != null)
                fTransaction.hide(mHomePageFragment);
            if (mCouponFragment != null)
                fTransaction.hide(mCouponFragment);
            if (mSearchFragment != null)
                fTransaction.hide(mSearchFragment);
            if (mEntityFragment != null)
                fTransaction.hide(mEntityFragment);
        }
        fTransaction.commit();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (mHomePageFragment != null) {
            mHomePageFragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    //再按一次退出
    private long startTime = 0;
    /**
     * 设置再按一次退出功能
     */
    @Override
    public void onBackPressed() {
        long currentTime = System.currentTimeMillis();
        if ((currentTime - startTime) >= 2000) {
            ToastUtil.show(mMainActivity, "再按一次退出");
            startTime = currentTime;
        } else {
            AppManager.getAppManager().AppExit(mMainActivity);
        }
    }

    @Override
    public void onChecked(final int type) {
        rb_coupon.setChecked(true);
        if (mCouponFragment != null) {
            //isFirst：true，添加加载完成监听，避免报错；false，不是第一次加载不需要监听
            if (isFirst) {
                isFirst = false;
                mCouponFragment.setOnCouponFragmentCompleteListener(new OnCouponFragmentCompleteListener() {
                    @Override
                    public void onComplete() {
                        mCouponFragment.onChecked(type);
                    }
                });
            } else {
                mCouponFragment.onChecked(type);
            }
        }
    }
}
