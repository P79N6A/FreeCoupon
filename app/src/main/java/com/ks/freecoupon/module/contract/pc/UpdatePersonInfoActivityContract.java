package com.ks.freecoupon.module.contract.pc;

import com.ks.basictools.base_interface.IBaseActivity;
import com.ks.basictools.base_interface.IBasePresenter;
import com.ks.freecoupon.module.view.pc.activity.UpdatePersonInfoActivity;

/**
 * author：康少
 * date：2019/3/26
 * description：修改个人信息
 */
public class UpdatePersonInfoActivityContract {
    public interface IUpdatePersonInfoActivity extends IBaseActivity<IUpdatePersonInfoActivityPresenter> {

    }

    public interface IUpdatePersonInfoActivityPresenter extends IBasePresenter {
    }
}
