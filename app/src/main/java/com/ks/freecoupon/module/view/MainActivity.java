package com.ks.freecoupon.module.view;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ks.basictools.publicView.TitleVarView;
import com.ks.freecoupon.BaseActivity;
import com.ks.freecoupon.R;
import com.ks.freecoupon.ks_interface.OnCouponCheckedListener;
import com.ks.freecoupon.module.contract.MainActivityContract;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Date:2019/3/7
 * Author：康少
 * Description：主要Activity——视图层
 */
public class MainActivity extends BaseActivity implements MainActivityContract.IMainActivity ,OnCouponCheckedListener {
    private MainActivityContract.IMainActivityPresenter mIActivityPresenter;

    @BindView(R.id.tab_menu)
    RadioGroup tab_menu;
    @BindView(R.id.rb_homePage)
    RadioButton rb_homePage;
    @BindView(R.id.rb_coupon)
    RadioButton rb_coupon;
    @BindView(R.id.rb_search)
    RadioButton rbSearch;
    @BindView(R.id.rb_entity)
    RadioButton rbEntity;
    @BindView(R.id.rb_mine)
    RadioButton rb_mine;

    /**
     * 宿主方法
     */
    public RadioGroup getTab_menu() {
        return tab_menu;
    }
    public RadioButton getRb_homePage() {
        return rb_homePage;
    }
    public RadioButton getRb_coupon() {
        return rb_coupon;
    }
    public RadioButton getRbSearch() {
        return rbSearch;
    }
    public RadioButton getRbEntity() {
        return rbEntity;
    }
    public RadioButton getRb_mine() {
        return rb_mine;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        /*设置状态栏*/
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decorView = getWindow().getDecorView();
            //状态栏中的文字颜色和图标颜色为深色，需要android系统6.0以上，而且目前只有一种可以修改（一种是深色，一种是浅色即白色）
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            //设置状态栏的颜色
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        new MainActivityPresenter(this);
        mIActivityPresenter.start();
    }

    @Override
    public void setPresenter(MainActivityContract.IMainActivityPresenter mIActivityPresenter) {
        this.mIActivityPresenter = mIActivityPresenter;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mIActivityPresenter.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // 点击手机上的返回键，返回上一层
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            mIActivityPresenter.onBackPressed();
        }
        return false;
    }

    @Override
    public void onChecked(int type) {
        mIActivityPresenter.onChecked(type);
    }
}
