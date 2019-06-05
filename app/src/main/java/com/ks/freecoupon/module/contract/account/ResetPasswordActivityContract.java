package com.ks.freecoupon.module.contract.account;

import com.ks.basictools.base_interface.IBaseActivity;
import com.ks.basictools.base_interface.IBasePresenter;

/**
 * author：康少
 * date：2019/3/15
 * description：重设密码
 */
public class ResetPasswordActivityContract {
    public interface IResetPasswordActivity extends IBaseActivity<IResetPasswordActivityPresenter> {

    }
    public interface IResetPasswordActivityPresenter extends IBasePresenter {
        void onViewClicked();
    }
}
