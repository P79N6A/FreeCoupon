package com.ks.freecoupon.module.contract.pc;

import android.content.Intent;

import com.ks.basictools.base_interface.IBaseActivity;
import com.ks.basictools.base_interface.IBasePresenter;

/**
 * author：康少
 * date：2019/3/26
 * description：设置头像
 */
public class AvatarActivityContract {
    public interface IAvatarActivity extends IBaseActivity<IAvatarActivityPresenter> {

    }

    public interface IAvatarActivityPresenter extends IBasePresenter {
        void onRightClick();
        void onViewClicked();
        void onActivityResult(int requestCode, int resultCode, Intent data);
    }
}
