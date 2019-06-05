package com.ks.freecoupon.module.contract.pc;

import com.ks.basictools.base_interface.IBaseActivity;
import com.ks.basictools.base_interface.IBaseFragment;
import com.ks.basictools.base_interface.IBasePresenter;

/**
 * author：康少
 * date：2019/3/13
 * description：我的优惠券——Contract
 */
public class DiscountCouponActivityContract {
    public interface IDiscountCouponActivity extends IBaseActivity<IDiscountCouponActivityPresenter> {

    }

    public interface IDiscountCouponActivityPresenter extends IBasePresenter {
        void onDestroy();
    }
}
