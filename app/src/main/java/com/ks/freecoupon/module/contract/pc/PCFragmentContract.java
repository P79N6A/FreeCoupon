package com.ks.freecoupon.module.contract.pc;

import android.view.View;

import com.ks.basictools.base_interface.IBaseFragment;
import com.ks.basictools.base_interface.IBasePresenter;

/**
 * author：康少
 * date：2019/3/7
 * description：个人中心Fragment
 */
public class PCFragmentContract {
    public interface IPCFragment extends IBaseFragment<IPCFragmentPresenter> {

    }
    public interface IPCFragmentPresenter extends IBasePresenter {
        void onClick(View view);
        void onResume();
        void onDestroy();
    }
}
