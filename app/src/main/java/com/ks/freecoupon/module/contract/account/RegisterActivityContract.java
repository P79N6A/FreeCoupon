package com.ks.freecoupon.module.contract.account;

import android.view.View;

import com.ks.basictools.base_interface.IBaseActivity;
import com.ks.basictools.base_interface.IBasePresenter;

/**
 * author：康少
 * date：2019/3/15
 * description：注册
 */
public class RegisterActivityContract {
    public interface IRegisterActivity extends IBaseActivity<IRegisterActivityPresenter> {

    }

    public interface IRegisterActivityPresenter extends IBasePresenter {
        void onViewClicked(View view);
    }
}
