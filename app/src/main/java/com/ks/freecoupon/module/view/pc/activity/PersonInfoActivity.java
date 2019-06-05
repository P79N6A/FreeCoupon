package com.ks.freecoupon.module.view.pc.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ks.basictools.publicView.TitleVarView;
import com.ks.freecoupon.R;
import com.ks.freecoupon.module.bean.User;
import com.ks.freecoupon.module.contract.pc.PersonInfoActivityContract;
import com.ks.freecoupon.module.view.pc.presenter.PersonInfoActivityPresenter;
import com.ks.freecoupon.override.RoundTransform;
import com.ks.freecoupon.utils.SharedPreferencesUtils;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Date:2019/3/23
 * Author：康少
 * Description：个人信息——视图层
 */
public class PersonInfoActivity extends TitleVarView implements PersonInfoActivityContract.IPersonInfoActivity, View.OnClickListener {

    private PersonInfoActivityContract.IPersonInfoActivityPresenter mIActivityPresenter;
    private ImageView ivAvatar;
    private TextView tvPhone;
    private TextView tvNickname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_info);
        EventBus.getDefault().register(this);
        findId();
        initView();
        setTitleBar(true, "个人信息", "");
        new PersonInfoActivityPresenter(this);
        mIActivityPresenter.start();
    }

    /**
     * 由 AvatarActivityPresenter 发出通知——修改头像
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateAvatar(User user) {
        mIActivityPresenter.updateAvatar(user);
    }

    private void findId() {
        ivAvatar = findViewById(R.id.iv_avatar);
        tvPhone = findViewById(R.id.tv_phone);
        tvNickname = findViewById(R.id.tv_nickname);
        findViewById(R.id.ll_avatar).setOnClickListener(this);
        findViewById(R.id.ll_phone).setOnClickListener(this);
        findViewById(R.id.ll_nickname).setOnClickListener(this);
        findViewById(R.id.ll_share).setOnClickListener(this);
        findViewById(R.id.ll_address).setOnClickListener(this);
        findViewById(R.id.ll_logout).setOnClickListener(this);
    }

    private void initView() {
        boolean isLogin = (boolean) SharedPreferencesUtils.getParam(this,"isLogin",false);
        if (isLogin) {
            findViewById(R.id.ll_logout).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.ll_logout).setVisibility(View.GONE);
        }
    }

    /**
     * Description：宿主方法
     */
    public ImageView getIvAvatar() {
        return ivAvatar;
    }
    public TextView getTvPhone() {
        return tvPhone;
    }
    public TextView getTvNickname() {
        return tvNickname;
    }

    @Override
    public void setPresenter(PersonInfoActivityContract.IPersonInfoActivityPresenter mIActivityPresenter) {
        this.mIActivityPresenter = mIActivityPresenter;
    }

    @Override
    public void onClick(View v) {
        mIActivityPresenter.onViewClicked(v);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mIActivityPresenter.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }
}
