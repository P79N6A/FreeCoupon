package com.ks.freecoupon.module.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;

import com.ks.freecoupon.module.view.coupon.fragment.JingDongCouponFragment;
import com.ks.freecoupon.module.view.coupon.fragment.NearShopFragment;
import com.ks.freecoupon.module.view.coupon.fragment.TaoBaoCouponFragment;

import java.util.List;

/**
 * Date:2018/8/28
 * Author：康少
 * Description：Tab——adapter
 */
public class ViewPagerAdapter extends FragmentPagerAdapter {
    public List<Fragment> viewList;
    FragmentManager fm;

    public ViewPagerAdapter(FragmentManager fm, List<Fragment> viewList){
        super(fm);
        this.fm = fm;
        this.viewList = viewList;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (getItem(position) instanceof NearShopFragment
                || getItem(position) instanceof TaoBaoCouponFragment
                || getItem(position) instanceof JingDongCouponFragment) {
            if (position == 0) {
                return "惠淘宝";
            }
            if (position == 1) {
                return "惠京东";
            }
            if (position == 2) {
                return "找免费";
            }
        }
        return super.getPageTitle(position);
    }

    @Override
    public Fragment getItem(int position) {
        return viewList.get(position);
    }

    @Override public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    public void refreshFragments(List<Fragment> fragments) {
        if(this.viewList != null){
            FragmentTransaction ft = fm.beginTransaction();
            for (Fragment f : this.viewList) {
                ft.remove(f);
            }
            ft.commit();
            fm.executePendingTransactions();
        }
        this.viewList = fragments;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {//返回要滑动的VIew的个数
        return viewList.size();
    }
}
