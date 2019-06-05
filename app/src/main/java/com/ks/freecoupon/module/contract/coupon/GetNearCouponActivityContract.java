package com.ks.freecoupon.module.contract.coupon;

import android.view.View;

import com.ks.basictools.base_interface.IBaseActivity;
import com.ks.basictools.base_interface.IBasePresenter;

/**
 * author：康少
 * date：2019/4/12
 * description：领取附近优惠券
 */
public class GetNearCouponActivityContract {
    public interface IGetNearCouponActivity extends IBaseActivity<IGetNearCouponActivityPresenter> {

    }

    public interface IGetNearCouponActivityPresenter extends IBasePresenter {
        void onViewClicked(View view);
    }
}
