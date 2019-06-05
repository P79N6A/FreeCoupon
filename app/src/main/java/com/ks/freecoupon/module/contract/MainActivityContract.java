package com.ks.freecoupon.module.contract;

import android.support.annotation.NonNull;
import android.view.View;

import com.ks.basictools.base_interface.IBaseActivity;
import com.ks.basictools.base_interface.IBasePresenter;

/**
 * author：康少
 * date：2019/3/7
 * description：主要Activity Contract
 */
public class MainActivityContract {
    public interface IMainActivity extends IBaseActivity<IMainActivityPresenter> {
    }
    public interface IMainActivityPresenter extends IBasePresenter {
        void onBackPressed();
        void onChecked(int type);
        void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults);
    }
}
