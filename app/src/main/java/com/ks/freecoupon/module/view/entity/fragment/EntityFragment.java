package com.ks.freecoupon.module.view.entity.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.ks.basictools.utils.ToastUtil;
import com.ks.freecoupon.BaseFragment;
import com.ks.freecoupon.R;
import com.ks.freecoupon.module.contract.entity.EntityFragmentContract;
import com.ks.freecoupon.module.view.entity.presenter.EntityFragmentPresenter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bingoogolapple.bgabanner.BGABanner;

/**
 * Date:2019/3/21
 * Author：康少
 * Description：领实物——视图层
 */
public class EntityFragment extends BaseFragment implements EntityFragmentContract.IEntityFragment {
    private EntityFragmentContract.IEntityFragmentPresenter mIFragmentPresenter;
    private Context mContext;
    @BindView(R.id.srl_refresh)
    SwipeRefreshLayout srl_refresh;
    @BindView(R.id.banner)
    BGABanner banner;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.wv_desc)
    WebView wvDesc;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;//解决fragment.getContext有时为空的问题，触发条件是不断快速切换fragment
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_entity, container, false);
        ButterKnife.bind(this, inflate);
        new EntityFragmentPresenter(this);
        mIFragmentPresenter.start();
        return inflate;
    }

    /**
     * Description：宿主方法
     */
    public BGABanner getBanner() {
        return banner;
    }
    public TextView getTvTitle() {
        return tvTitle;
    }
    public WebView getWvDesc() {
        return wvDesc;
    }
    public Context getmContext() {
        return mContext;
    }
    public SwipeRefreshLayout getSrl_refresh() {
        return srl_refresh;
    }

    @Override
    public void setPresenter(EntityFragmentContract.IEntityFragmentPresenter mIFragmentPresenter) {
        this.mIFragmentPresenter = mIFragmentPresenter;
    }

    @OnClick(R.id.btn_getEntity)
    public void onViewClicked() {
        mIFragmentPresenter.onViewClicked();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mIFragmentPresenter.onDestroy();
    }
}
