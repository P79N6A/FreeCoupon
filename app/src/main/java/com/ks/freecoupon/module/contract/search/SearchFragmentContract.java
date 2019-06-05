package com.ks.freecoupon.module.contract.search;

import android.view.View;

import com.ks.basictools.base_interface.IBaseFragment;
import com.ks.basictools.base_interface.IBasePresenter;

/**
 * author：康少
 * date：2019/3/21
 * description：搜素
 */
public class SearchFragmentContract {
    public interface ISearchFragment extends IBaseFragment<ISearchFragmentPresenter> {

    }

    public interface ISearchFragmentPresenter extends IBasePresenter {
        void onDestroyView();
        void onViewClicked(View view);
    }
}
