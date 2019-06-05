package com.ks.freecoupon.module.view.homePage.fragment;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ks.freecoupon.BaseFragment;
import com.ks.freecoupon.R;
import com.ks.freecoupon.module.contract.homePage.HomePageFragmentContract;
import com.ks.freecoupon.module.view.homePage.presenter.HomePageFragmentPresenter;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Date:2019/3/12
 * Author：康少
 * Description：首页Fragment——视图层
 */
public class HomePageFragment extends BaseFragment implements HomePageFragmentContract.IHomePageFragment{
    private HomePageFragmentContract.IHomePageFragmentPresenter mIFragmentPresenter;
    private Context mContext;

    @BindView(R.id.rv_list)
    RecyclerView rv_list;
//    @BindView(R.id.srl_refresh)
//    SwipeRefreshLayout srl_refresh;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;//解决fragment.getContext有时为空的问题，触发条件是不断快速切换fragment
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_home_page, container, false);
        ButterKnife.bind(this, inflate);
        new HomePageFragmentPresenter(this);
        mIFragmentPresenter.start();
        return inflate;
    }

    /**
     * Description：宿主方法
     */
    public RecyclerView getRv_list() {
        return rv_list;
    }
    public Context getmContext() {
        return mContext;
    }
//    public SwipeRefreshLayout getSrl_refresh() {
//        return srl_refresh;
//    }

    @Override
    public void setPresenter(HomePageFragmentContract.IHomePageFragmentPresenter mIFragmentPresenter) {
        this.mIFragmentPresenter = mIFragmentPresenter;
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mIFragmentPresenter.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mIFragmentPresenter.onDestroy();
    }
}
