package com.ks.freecoupon.module.view.account.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

import com.ks.basictools.ActivityCollector;
import com.ks.freecoupon.BaseActivity;
import com.ks.freecoupon.R;
import com.ks.freecoupon.module.contract.account.LoginActivityContract;
import com.ks.freecoupon.module.view.MainActivity;
import com.ks.freecoupon.module.view.WelcomeActivity;
import com.ks.freecoupon.module.view.account.presenter.LoginActivityPresenter;

import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.internal.Utils;

/**
 * Date:2019/3/14
 * Author：康少
 * Description：登录——视图层
 */
public class LoginActivity extends BaseActivity implements LoginActivityContract.ILoginActivity {
    private LoginActivityContract.ILoginActivityPresenter mIActivityPresenter;

    @BindView(R.id.login_username)
    EditText loginUsername;
    @BindView(R.id.login_password)
    EditText loginPassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        new LoginActivityPresenter(this);
        mIActivityPresenter.start();
    }

    /**
     * Description：宿主方法
     */
    public EditText getUsername() {
        return loginUsername;
    }
    public EditText getPassword() {
        return loginPassword;
    }

    @Override
    public void setPresenter(LoginActivityContract.ILoginActivityPresenter mIActivityPresenter) {
        this.mIActivityPresenter = mIActivityPresenter;
    }

    @OnClick({R.id.btn_login, R.id.login_forget_pwd, R.id.login_register})
    public void onViewClicked(View view) {
        mIActivityPresenter.onClick(view);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mIActivityPresenter.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // 点击手机上的返回键，返回上一层
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent i = getIntent();
            boolean isLogout = i.getBooleanExtra("isLogout",false);
            if (isLogout) {//如果是退出状态，则按返回键时重新游客登录
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            } else {//否则直接返回
                this.finish();
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
