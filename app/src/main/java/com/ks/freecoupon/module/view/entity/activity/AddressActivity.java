package com.ks.freecoupon.module.view.entity.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ks.basictools.publicView.TitleVarView;
import com.ks.freecoupon.R;
import com.ks.freecoupon.module.contract.entity.AddressContract;
import com.ks.freecoupon.module.view.entity.presenter.AddressActivityPresenter;

/**
 * Date:2019/3/25
 * Author：康少
 * Description：收货地址——视图层
 */
public class AddressActivity extends TitleVarView implements AddressContract.IAddressActivity{
    private AddressContract.IAddressActivityPresenter mIActivityPresenter;

    private RelativeLayout rl_city;
    private TextView tv_city;//省、市、区
    private EditText et_linkman;//收货联系人
    private EditText et_phone;//联系方式
    private EditText et_address;//详细地址
    private TextView tv_phone;//手机号
    private TextView tv_sendYZM;//发送验证码
    private EditText et_yzm;//验证码
    private Button btn_confirm;//确定

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);
        setTitleBar(true, "收货地址", "", "");
        findId();
        new AddressActivityPresenter(this);
        mIActivityPresenter.start();
    }

    private void findId() {
        rl_city = findViewById(R.id.rl_city);
        tv_city = findViewById(R.id.tv_city);
        et_linkman = findViewById(R.id.et_linkman);
        et_phone = findViewById(R.id.et_phone);
        et_address = findViewById(R.id.et_address);
        btn_confirm = findViewById(R.id.btn_confirm);
        tv_phone = findViewById(R.id.tv_phone);
        et_yzm = findViewById(R.id.et_yzm);
        tv_sendYZM = findViewById(R.id.tv_sendYZM);
    }

    /**
     * Description：宿主方法
     */
    public RelativeLayout getRl_city() {
        return rl_city;
    }
    public TextView getTv_city() {
        return tv_city;
    }
    public EditText getEt_linkman() {
        return et_linkman;
    }
    public EditText getEt_phone() {
        return et_phone;
    }
    public EditText getEt_address() {
        return et_address;
    }
    public Button getBtn_confirm() {
        return btn_confirm;
    }
    public TextView getTv_phone() {
        return tv_phone;
    }
    public TextView getTv_sendYZM() {
        return tv_sendYZM;
    }
    public EditText getEt_yzm() {
        return et_yzm;
    }

    @Override
    public void setPresenter(AddressContract.IAddressActivityPresenter mIActivityPresenter) {
        this.mIActivityPresenter = mIActivityPresenter;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mIActivityPresenter.onDestroy();
    }
}
