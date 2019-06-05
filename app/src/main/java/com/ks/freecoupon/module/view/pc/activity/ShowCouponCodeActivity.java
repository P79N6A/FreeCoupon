package com.ks.freecoupon.module.view.pc.activity;

import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ks.basictools.publicView.TitleVarView;
import com.ks.freecoupon.R;
import com.ks.freecoupon.module.bean.NearGoods;
import com.ks.freecoupon.module.view.coupon.activity.CopyTaoWordActivity;
import com.ks.freecoupon.override.ToastKs;
import com.ks.freecoupon.utils.BitmapUtils;

/**
 * Date:2019/4/13
 * Author：康少
 * Description：展示优惠券二维码
 */
public class ShowCouponCodeActivity extends TitleVarView implements View.OnClickListener {

    private NearGoods discountCoupon;
    private ImageView image;
    private TextView tv_code;
    private TextView tv_address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_coupon_code);
        setTitleBar(true, "我的优惠券","", "");
        findId();
        start();

    }

    private void findId() {
        image = findViewById(R.id.image);
        tv_code = findViewById(R.id.tv_code);
        tv_address = findViewById(R.id.tv_address);
        findViewById(R.id.tv_copy).setOnClickListener(this);
    }

    private void start() {
        discountCoupon = (NearGoods) getIntent().getSerializableExtra("discountCoupon");
        tv_code.setText(discountCoupon.getCoupon_code());
        showQRCode();
        showAddress();
    }

    /**
     * Description：展示二维码
     */
    private void showQRCode() {
        String content = discountCoupon.getCoupon_code();
        Bitmap qrCode = BitmapUtils.createQRCode(content, 200, null);
        image.setImageBitmap(qrCode);
    }

    /**
     * Description：展示地址
     */
    private void showAddress() {
        tv_address.setText(discountCoupon.getAddress());
    }

    @Override
    public void onClick(View v) {
        ClipboardManager copy = (ClipboardManager) ShowCouponCodeActivity.this
                .getSystemService(Context.CLIPBOARD_SERVICE);
        if (copy != null) {
            copy.setText(discountCoupon.getAddress());
            ToastKs.show(this,"复制成功！");
        }
    }
}
