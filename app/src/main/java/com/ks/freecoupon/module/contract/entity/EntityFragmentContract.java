package com.ks.freecoupon.module.contract.entity;

import com.ks.basictools.base_interface.IBaseFragment;
import com.ks.basictools.base_interface.IBasePresenter;

/**
 * author：康少
 * date：2019/3/21
 * description：领实物
 */
public class EntityFragmentContract {
    public interface IEntityFragment extends IBaseFragment<IEntityFragmentPresenter> {

    }

    public interface IEntityFragmentPresenter extends IBasePresenter {
        void onViewClicked();
        void onDestroy();
    }
}
