package com.ks.freecoupon.module.view.pc.activity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.ks.basictools.publicView.TitleVarView;
import com.ks.freecoupon.R;
import com.ks.freecoupon.module.contract.pc.ShareActivityContract;
import com.ks.freecoupon.module.view.pc.presenter.ShareActivityPresenter;

/**
 * Date:2019/3/27
 * Author：康少
 * Description：分享有礼——视图层
 */
public class ShareActivity extends TitleVarView implements ShareActivityContract.IShareActivity {
    private ShareActivityContract.IShareActivityPresenter mIActivityPresenter;

    private ImageView image;
    private ImageView iv_image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
        findId();
        setTitleBar(true, "分享二维码", "","");
        new ShareActivityPresenter(this);
        mIActivityPresenter.start();
    }

    private void findId() {
        image = findViewById(R.id.image);
        iv_image = findViewById(R.id.iv_image);
    }

    public ImageView getImage() {
        return image;
    }
    public ImageView getIv_image() {
        return iv_image;
    }

    @Override
    public void setPresenter(ShareActivityContract.IShareActivityPresenter mIActivityPresenter) {
        this.mIActivityPresenter = mIActivityPresenter;
    }
}
