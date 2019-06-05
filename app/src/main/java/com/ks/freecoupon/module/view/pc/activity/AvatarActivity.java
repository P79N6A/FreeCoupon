package com.ks.freecoupon.module.view.pc.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.ks.basictools.publicView.TitleVarView;
import com.ks.freecoupon.R;
import com.ks.freecoupon.module.contract.pc.AvatarActivityContract;
import com.ks.freecoupon.module.view.pc.presenter.AvatarActivityPresenter;

/**
 * Date:2019/3/26
 * Author：康少
 * Description：头像设置——视图层
 */
public class AvatarActivity extends TitleVarView implements AvatarActivityContract.IAvatarActivity, TitleVarView.TitleBarOnRightClickListener, View.OnClickListener {
    private AvatarActivityContract.IAvatarActivityPresenter mIActivityPresenter;
    private ImageView ivAvatar;
    private Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_avatar);
        setTitleBar(true, "个人头像", getResources().getString(R.string.string_ddd));
        setRightTitleBarOnClickListener(this);
        findId();
        new AvatarActivityPresenter(this);
        mIActivityPresenter.start();
    }

    private void findId() {
        ivAvatar = findViewById(R.id.iv_avatar);
        btnSave = findViewById(R.id.btn_save);
        btnSave.setOnClickListener(this);
    }

    /**
     * Description：宿主方法
     */
    public ImageView getIvAvatar() {
        return ivAvatar;
    }
    public Button getBtnSave() {
        return btnSave;
    }

    @Override
    public void setPresenter(AvatarActivityContract.IAvatarActivityPresenter mIActivityPresenter) {
        this.mIActivityPresenter = mIActivityPresenter;
    }

    @Override
    public void onRightClick() {
        mIActivityPresenter.onRightClick();
    }

    @Override
    public void onClick(View v) {
        mIActivityPresenter.onViewClicked();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mIActivityPresenter.onActivityResult(requestCode, resultCode, data);
    }
}
