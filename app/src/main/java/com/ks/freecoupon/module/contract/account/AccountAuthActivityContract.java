package com.ks.freecoupon.module.contract.account;

import android.content.Intent;
import android.view.View;

import com.ks.basictools.base_interface.IBaseActivity;
import com.ks.basictools.base_interface.IBasePresenter;

/**
 * author：康少
 * date：2019/3/15
 * description：账户认证
 */
public class AccountAuthActivityContract {
    public interface IAccountAuthActivity extends IBaseActivity<IAccountAuthActivityPresenter> {

    }

    public interface IAccountAuthActivityPresenter extends IBasePresenter {
        void onViewClicked(View view);
        void onActivityResult(int requestCode, int resultCode, Intent data);
    }
}
