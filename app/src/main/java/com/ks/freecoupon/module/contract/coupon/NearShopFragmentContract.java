package com.ks.freecoupon.module.contract.coupon;

import android.support.annotation.NonNull;

import com.ks.basictools.base_interface.IBaseFragment;
import com.ks.basictools.base_interface.IBasePresenter;

/**
 * author：康少
 * date：2019/3/7
 * description：附近店铺
 */
public class NearShopFragmentContract {
    public interface INearShopFragment extends IBaseFragment<INearShopFragmentPresenter> {

    }

    public interface INearShopFragmentPresenter extends IBasePresenter {
        void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults);
        void onDestroy();
    }
}
