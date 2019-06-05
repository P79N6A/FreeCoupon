package com.ks.freecoupon.module.contract.coupon;

import com.ks.basictools.base_interface.IBaseFragment;
import com.ks.basictools.base_interface.IBasePresenter;

/**
 * author：康少
 * date：2019/3/7
 * description：领券Fragment
 */
public class CouponFragmentContract {
    public interface ICouponFragment extends IBaseFragment<ICouponFragmentPresenter> {

    }

    public interface ICouponFragmentPresenter extends IBasePresenter {
        void onChecked(int type);
    }
}
