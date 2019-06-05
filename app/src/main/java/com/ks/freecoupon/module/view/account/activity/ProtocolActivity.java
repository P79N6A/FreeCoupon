package com.ks.freecoupon.module.view.account.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.ks.basictools.publicView.TitleVarView;
import com.ks.freecoupon.R;

/**
 * Date:2019/3/22
 * Author：康少
 * Description：用户协议
 */
public class ProtocolActivity extends TitleVarView {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_protocol);
        setTitleBar(true, "用户协议", "");
    }
}
