package com.ks.freecoupon.module.contract.coupon;

import com.ks.basictools.base_interface.IBaseFragment;
import com.ks.basictools.base_interface.IBasePresenter;

/**
 * author：康少
 * date：2019/3/27
 * description：京东优惠券
 */
public class JingDongCouponFragmentContract {
    public interface IJingDongCouponFragment extends IBaseFragment<IJingDongCouponFragmentPresenter> {

    }

    public interface IJingDongCouponFragmentPresenter extends IBasePresenter {
        void onDestroy();
    }
}
