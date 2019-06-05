package com.ks.freecoupon.module.view.account.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.ks.freecoupon.BaseActivity;
import com.ks.freecoupon.R;
import com.ks.freecoupon.module.contract.account.AccountAuthActivityContract;
import com.ks.freecoupon.module.view.account.presenter.AccountAuthActivityPresenter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Date:2019/3/15
 * Author：康少
 * Description：账户认证——视图层
 */
public class AccountAuthActivity extends BaseActivity implements AccountAuthActivityContract.IAccountAuthActivity {
    private AccountAuthActivityContract.IAccountAuthActivityPresenter mIActivityPresenter;
    /*控件对象*/
    @BindView(R.id.et_phoneNumber)
    EditText etPhoneNumber;
    @BindView(R.id.btn_getYZM)
    TextView btnGetYZM;
    @BindView(R.id.et_YZM)
    EditText etYZM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_auth);
        ButterKnife.bind(this);
        new AccountAuthActivityPresenter(this);
        mIActivityPresenter.start();
    }

    /**
     * Description：控件对象
     */
    public EditText getEtPhoneNumber() {
        return etPhoneNumber;
    }

    public EditText getEtYZM() {
        return etYZM;
    }

    public TextView getBtnGetYZM() {
        return btnGetYZM;
    }

    @Override
    public void setPresenter(AccountAuthActivityContract.IAccountAuthActivityPresenter mIActivityPresenter) {
        this.mIActivityPresenter = mIActivityPresenter;
    }

    @OnClick({R.id.btn_getYZM, R.id.btn_next})
    public void onViewClicked(View view) {
        mIActivityPresenter.onViewClicked(view);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mIActivityPresenter.onActivityResult(requestCode, resultCode, data);
    }
}
