package com.ks.freecoupon.module.contract.entity;

import com.ks.basictools.base_interface.IBaseActivity;
import com.ks.basictools.base_interface.IBasePresenter;

/**
 * Date:2019/3/25
 * Author：康少
 * Description：收货地址
 */
public class AddressContract {
    public interface IAddressActivity extends IBaseActivity<IAddressActivityPresenter> {

    }

    public interface IAddressActivityPresenter extends IBasePresenter {
        void onDestroy();
    }
}
