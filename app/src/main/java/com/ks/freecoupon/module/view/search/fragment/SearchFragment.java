package com.ks.freecoupon.module.view.search.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.ks.freecoupon.BaseFragment;
import com.ks.freecoupon.R;
import com.ks.freecoupon.module.contract.search.SearchFragmentContract;
import com.ks.freecoupon.module.view.search.presenter.SearchFragmentPresenter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Date:2019/3/21
 * Author：康少
 * Description：搜索Fragment——视图层
 */
public class SearchFragment extends BaseFragment implements SearchFragmentContract.ISearchFragment {
    private SearchFragmentContract.ISearchFragmentPresenter mIFragmentPresenter;
    private Context mContext;

    @BindView(R.id.rv_hotSearch)
    RecyclerView rvHotSearch;
    @BindView(R.id.rv_searchHistory)
    RecyclerView rvSearchHistory;
    @BindView(R.id.et_search_clazz)
    EditText etSearchClazz;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_search, container, false);
        ButterKnife.bind(this, inflate);
        new SearchFragmentPresenter(this);
        mIFragmentPresenter.start();
        return inflate;
    }

    /**
     * Description：宿主方法
     */
    public EditText getEtSearchClazz() {
        return etSearchClazz;
    }
    public RecyclerView getRvHotSearch() {
        return rvHotSearch;
    }
    public RecyclerView getRvSearchHistory() {
        return rvSearchHistory;
    }
    public Context getmContext() {
        return mContext;
    }

    @Override
    public void setPresenter(SearchFragmentContract.ISearchFragmentPresenter mIFragmentPresenter) {
        this.mIFragmentPresenter = mIFragmentPresenter;
    }

    @OnClick({R.id.tv_search, R.id.tv_clearHistory})
    public void onViewClicked(View view) {
        mIFragmentPresenter.onViewClicked(view);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
