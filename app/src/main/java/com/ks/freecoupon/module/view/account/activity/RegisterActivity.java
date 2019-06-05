package com.ks.freecoupon.module.view.account.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.ks.freecoupon.BaseActivity;
import com.ks.freecoupon.R;
import com.ks.freecoupon.module.contract.account.RegisterActivityContract;
import com.ks.freecoupon.module.view.account.presenter.RegisterActivityPresenter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Date:2019/3/15
 * Author：康少
 * Description：注册——视图层
 */
public class RegisterActivity extends BaseActivity implements RegisterActivityContract.IRegisterActivity {
    private RegisterActivityContract.IRegisterActivityPresenter mIActivityPresenter;

    /*控件对象*/
    @BindView(R.id.et_phone)
    EditText etPhone;
    @BindView(R.id.et_YZM)
    EditText etYZM;
    @BindView(R.id.btn_getYZM)
    TextView btnGetYZM;
    @BindView(R.id.et_password)
    EditText etPassword;
    @BindView(R.id.et_conform_password)
    EditText etConformPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        new RegisterActivityPresenter(this);
        mIActivityPresenter.start();
    }

    /**
     * Description：宿主方法
     */
    public EditText getEtPhone() {
        return etPhone;
    }
    public EditText getEtYZM() {
        return etYZM;
    }
    public EditText getEtPassword() {
        return etPassword;
    }
    public EditText getEtConformPassword() {
        return etConformPassword;
    }
    public TextView getBtnGetYZM() {
        return btnGetYZM;
    }

    @Override
    public void setPresenter(RegisterActivityContract.IRegisterActivityPresenter mIActivityPresenter) {
        this.mIActivityPresenter = mIActivityPresenter;
    }

    @OnClick({R.id.tv_protocol, R.id.btn_getYZM, R.id.btn_register_conform})
    public void onViewClicked(View view) {
        mIActivityPresenter.onViewClicked(view);
    }
}
