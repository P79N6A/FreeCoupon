package com.ks.freecoupon.module.view.coupon.fragment;

import android.app.Activity;
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
import com.ks.freecoupon.module.contract.coupon.TaoBaoCouponFragmentContract;
import com.ks.freecoupon.module.view.coupon.presenter.TaoBaoCouponFragmentPresenter;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Date:2019/3/7
 * Author：康少
 * Description：淘宝优惠券——视图层
 */
public class TaoBaoCouponFragment extends BaseFragment implements TaoBaoCouponFragmentContract.ITicketFragment {
    private TaoBaoCouponFragmentContract.ITaoBaoCouponFragmentPresenter mIFragmentPresenter;
    private Context mContext;
    private Activity mActivity;

    @BindView(R.id.tv_tip)
    TextView tvTip;
    @BindView(R.id.rl_empty)
    RelativeLayout rlEmpty;
    @BindView(R.id.srl_refresh)
    SwipeRefreshLayout srl_refresh;
    @BindView(R.id.rv_list)
    RecyclerView rv_list;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_ticket, container, false);
        ButterKnife.bind(this, inflate);
        setTypeface();//设置字体样式
        new TaoBaoCouponFragmentPresenter(this);
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

    /**
     * Description：宿主方法
     */
    public Context getmContext() {
        return mContext;
    }

    public RelativeLayout getRlEmpty() {
        return rlEmpty;
    }

    public Activity getmActivity() {
        return mActivity;
    }

    public SwipeRefreshLayout getSrl_refresh() {
        return srl_refresh;
    }

    public RecyclerView getRv_list() {
        return rv_list;
    }

    @Override
    public void setPresenter(TaoBaoCouponFragmentContract.ITaoBaoCouponFragmentPresenter mIFragmentPresenter) {
        this.mIFragmentPresenter = mIFragmentPresenter;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mIFragmentPresenter.onDestroy();
    }
}
