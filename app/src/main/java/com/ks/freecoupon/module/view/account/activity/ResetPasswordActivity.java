package com.ks.freecoupon.module.view.account.activity;

import android.os.Bundle;
import android.widget.EditText;

import com.ks.freecoupon.BaseActivity;
import com.ks.freecoupon.R;
import com.ks.freecoupon.module.contract.account.ResetPasswordActivityContract;
import com.ks.freecoupon.module.view.account.presenter.ResetPasswordActivityPresenter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Date:2019/3/15
 * Author：康少
 * Description：重设密码——视图层
 */
public class ResetPasswordActivity extends BaseActivity implements ResetPasswordActivityContract.IResetPasswordActivity {
    private ResetPasswordActivityContract.IResetPasswordActivityPresenter mIActivityPresenter;
    /*控件对象*/
    @BindView(R.id.et_new_password)
    EditText etNewPassword;
    @BindView(R.id.et_conform_password)
    EditText etConformPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        ButterKnife.bind(this);
        new ResetPasswordActivityPresenter(this);
        mIActivityPresenter.start();
    }

    /**
     * Description：宿主方法
     */
    public EditText getEtNewPassword() {
        return etNewPassword;
    }
    public EditText getEtConformPassword() {
        return etConformPassword;
    }

    @Override
    public void setPresenter(ResetPasswordActivityContract.IResetPasswordActivityPresenter mIActivityPresenter) {
        this.mIActivityPresenter = mIActivityPresenter;
    }

    @OnClick(R.id.btn_complete)
    public void onViewClicked() {
        mIActivityPresenter.onViewClicked();
    }
}
