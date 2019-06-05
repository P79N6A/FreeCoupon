package com.ks.freecoupon.module.view.coupon.fragment;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ks.freecoupon.BaseFragment;
import com.ks.freecoupon.R;
import com.ks.freecoupon.module.contract.coupon.NearShopFragmentContract;
import com.ks.freecoupon.module.view.coupon.presenter.NearShopFragmentPresenter;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Date:2019/3/7
 * Author：康少
 * Description：附近优惠券——视图层
 */
public class NearShopFragment extends BaseFragment implements NearShopFragmentContract.INearShopFragment {
    private NearShopFragmentContract.INearShopFragmentPresenter mIFragmentPresenter;
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_near_shop, container, false);
        ButterKnife.bind(this, view);
        setTypeface();//设置字体样式
        new NearShopFragmentPresenter(this);
        mIFragmentPresenter.start();
        return view;
    }

    /**
     * Description：设置字体样式
     */
    private void setTypeface() {
        //设置字体
        Typeface typeface = Typeface.createFromAsset(mContext.getAssets(), "fangzheng.ttf");
        tvTip.setTypeface(typeface);
    }

    /**
     * Description：宿主方法
     */
    public Context getmContext() {
        return mContext;
    }

    public RelativeLayout getRlEmpty() {
        return rlEmpty;
    }

    public SwipeRefreshLayout getSrl_refresh() {
        return srl_refresh;
    }

    public RecyclerView getRvList() {
        return rvList;
    }

    @Override
    public void setPresenter(NearShopFragmentContract.INearShopFragmentPresenter mIFragmentPresenter) {
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
