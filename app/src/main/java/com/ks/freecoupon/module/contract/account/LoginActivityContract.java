package com.ks.freecoupon.module.contract.account;

import android.content.Intent;
import android.view.View;

import com.ks.basictools.base_interface.IBaseActivity;
import com.ks.basictools.base_interface.IBasePresenter;

/**
 * author：康少
 * date：2019/3/14
 * description：登录
 */
public class LoginActivityContract {
    public interface ILoginActivity extends IBaseActivity<ILoginActivityPresenter> {

    }

    public interface ILoginActivityPresenter extends IBasePresenter {
        void onClick(View view);
        void onActivityResult(int requestCode, int resultCode, Intent data);
    }
}
