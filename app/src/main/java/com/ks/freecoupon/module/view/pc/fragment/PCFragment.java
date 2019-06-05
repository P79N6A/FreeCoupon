package com.ks.freecoupon.module.view.pc.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ks.freecoupon.BaseFragment;
import com.ks.freecoupon.R;
import com.ks.freecoupon.module.contract.pc.PCFragmentContract;
import com.ks.freecoupon.module.view.pc.presenter.PCFragmentPresenter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Date:2019/3/7
 * Author：康少
 * Description：个人中心——视图层
 */
public class PCFragment extends BaseFragment implements PCFragmentContract.IPCFragment {
    private PCFragmentContract.IPCFragmentPresenter mIFragmentPresenter;

    private Context mContext;

    @BindView(R.id.iv_avatar)
    ImageView ivAvatar;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.after_login)
    LinearLayout afterLogin;
    @BindView(R.id.before_login)
    LinearLayout beforeLogin;
    @BindView(R.id.tv_integral)
    TextView tvIntegral;
    @BindView(R.id.tv_coupon)
    TextView tvCoupon;
    @BindView(R.id.iv_wenhao)
    ImageView iv_wenhao;
    @BindView(R.id.srl_refresh)
    SwipeRefreshLayout srl_refresh;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;//解决fragment.getContext有时为空的问题，触发条件是不断快速切换fragment
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_pc, container, false);
        ButterKnife.bind(this, inflate);
        new PCFragmentPresenter(this);
        mIFragmentPresenter.start();
        return inflate;
    }

    /**
     * Description：宿主方法
     */
    public Context getmContext() {
        return mContext;
    }
    public ImageView getIvAvatar() {
        return ivAvatar;
    }
    public TextView getTvName() {
        return tvName;
    }
    public LinearLayout getAfterLogin() {
        return afterLogin;
    }
    public LinearLayout getBeforeLogin() {
        return beforeLogin;
    }
    public TextView getTvIntegral() {
        return tvIntegral;
    }
    public TextView getTvCoupon() {
        return tvCoupon;
    }
    public ImageView getIv_wenhao() {
        return iv_wenhao;
    }
    public SwipeRefreshLayout getSrl_refresh() {
        return srl_refresh;
    }

    @Override
    public void setPresenter(PCFragmentContract.IPCFragmentPresenter mIFragmentPresenter) {
        this.mIFragmentPresenter = mIFragmentPresenter;
    }

    @OnClick({R.id.tv_login, R.id.tv_register, R.id.ll_integral, R.id.ll_coupon, R.id.rl_connect_us, R.id.rl_feedback, R.id.rl_exchange, R.id.rl_about, R.id.rl_set})
    public void onViewClicked(View view) {
        mIFragmentPresenter.onClick(view);
    }

    @Override
    public void onResume() {
        super.onResume();
        mIFragmentPresenter.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mIFragmentPresenter.onDestroy();
    }
}
