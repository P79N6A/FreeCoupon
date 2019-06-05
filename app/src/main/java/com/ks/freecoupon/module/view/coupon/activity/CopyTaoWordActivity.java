package com.ks.freecoupon.module.view.coupon.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipboardManager;
import android.content.ComponentCallbacks;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.TextView;

import com.ks.basictools.publicView.TitleVarView;
import com.ks.freecoupon.R;
import com.ks.freecoupon.override.ToastKs;

import java.io.File;

/**
 * Date:2019/3/21
 * Author：康少
 * Description：复制淘口令
 */
public class CopyTaoWordActivity extends TitleVarView implements View.OnClickListener {

    /*控件对象*/
    private TextView tv_title;
    private TextView tv_yuanPrice;
    private TextView tv_xianjia;
    private TextView tv_copy;
    /*参数*/
    private String coupon_pwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_copy_tao_word);
        setTitleBar(true, "复制淘口令", "");
        initView();
    }

    @SuppressLint("SetTextI18n")
    private void initView() {
        tv_title = findViewById(R.id.tvTitle);
        tv_yuanPrice = findViewById(R.id.tv_yuanPrice);
        tv_xianjia = findViewById(R.id.tv_xianjia);
        tv_copy = findViewById(R.id.tv_copy);
        tv_copy.setOnClickListener(this);

        //TaoBaoCouponFragmentPresenter
        String title = this.getIntent().getStringExtra("title");//商品标题
        String yuanjia = this.getIntent().getStringExtra("yuanjia");//商品原价
        String discount_price = this.getIntent().getStringExtra("discount_price");//商品券后价
        coupon_pwd = this.getIntent().getStringExtra("coupon_pwd");//淘口令

        tv_title.setText(title);
        tv_yuanPrice.setText(yuanjia + "元");
        tv_xianjia.setText(discount_price + "元");
    }

    @Override
    public void onClick(View v) {
        ClipboardManager copy = (ClipboardManager) CopyTaoWordActivity.this
                .getSystemService(Context.CLIPBOARD_SERVICE);
        if (copy != null) {
            copy.setText(coupon_pwd);
            ToastKs.show(this,"复制成功！");
        }
    }
}
