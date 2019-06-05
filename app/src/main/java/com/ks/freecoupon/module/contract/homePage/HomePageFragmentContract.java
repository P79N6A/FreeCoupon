package com.ks.freecoupon.module.contract.homePage;

import android.support.annotation.NonNull;

import com.ks.basictools.base_interface.IBaseFragment;
import com.ks.basictools.base_interface.IBasePresenter;

/**
 * author：康少
 * date：2019/3/12
 * description：首页Contract
 */
public class HomePageFragmentContract {
    public interface IHomePageFragment extends IBaseFragment<IHomePageFragmentPresenter> {

    }
    public interface IHomePageFragmentPresenter extends IBasePresenter {
        void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults);
        void onDestroy();
    }
}
