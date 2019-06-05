package com.ks.freecoupon.module.contract.coupon;

import com.ks.basictools.base_interface.IBaseFragment;
import com.ks.basictools.base_interface.IBasePresenter;

/**
 * author：康少
 * date：2019/3/7
 * description：线上优惠券
 */
public class TaoBaoCouponFragmentContract {
    public interface ITicketFragment extends IBaseFragment<ITaoBaoCouponFragmentPresenter> {

    }

    public interface ITaoBaoCouponFragmentPresenter extends IBasePresenter {
        void onDestroy();
    }
}
