package com.ks.freecoupon.module.view.pc.activity;

import android.os.Bundle;

import com.ks.basictools.publicView.TitleVarView;
import com.ks.freecoupon.R;

/**
 * Date:2019/3/27
 * Author：康少
 * Description：关于
 */
public class AboutActivity extends TitleVarView {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        setTitleBar(true, "关于", "");
    }
}
