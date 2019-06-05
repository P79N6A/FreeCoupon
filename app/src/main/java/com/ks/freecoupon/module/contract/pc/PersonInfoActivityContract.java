package com.ks.freecoupon.module.contract.pc;

import android.content.Intent;
import android.view.View;

import com.ks.basictools.base_interface.IBaseActivity;
import com.ks.basictools.base_interface.IBasePresenter;
import com.ks.freecoupon.module.bean.User;

/**
 * author：康少
 * date：2019/3/23
 * description：个人信息
 */
public class PersonInfoActivityContract {
    public interface IPersonInfoActivity extends IBaseActivity<IPersonInfoActivityPresenter> {

    }

    public interface IPersonInfoActivityPresenter extends IBasePresenter {
        void updateAvatar(User user);
        void onActivityResult(int requestCode, int resultCode, Intent data);
        void onViewClicked(View view);
    }
}
