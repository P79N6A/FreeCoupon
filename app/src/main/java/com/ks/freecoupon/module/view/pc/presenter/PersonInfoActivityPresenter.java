package com.ks.freecoupon.module.view.pc.presenter;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ks.basictools.utils.ToastUtil;
import com.ks.freecoupon.R;
import com.ks.freecoupon.module.bean.User;
import com.ks.freecoupon.module.contract.pc.PersonInfoActivityContract;
import com.ks.freecoupon.module.view.account.activity.LoginActivity;
import com.ks.freecoupon.module.view.entity.activity.AddressActivity;
import com.ks.freecoupon.module.view.pc.activity.AvatarActivity;
import com.ks.freecoupon.module.view.pc.activity.PersonInfoActivity;
import com.ks.freecoupon.module.view.pc.activity.ShareActivity;
import com.ks.freecoupon.module.view.pc.activity.UpdatePersonInfoActivity;
import com.ks.freecoupon.override.RoundTransform;
import com.ks.freecoupon.utils.BaseUtils;
import com.ks.freecoupon.utils.SharedPreferencesUtils;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.Serializable;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;

/**
 * author：康少
 * date：2019/3/23
 * description：个人信息——逻辑层
 */
public class PersonInfoActivityPresenter implements PersonInfoActivityContract.IPersonInfoActivityPresenter {
    private PersonInfoActivity mPersonInfoActivity;

    /*控件对象*/
    private ImageView ivAvatar;
    private TextView tvPhone;
    private TextView tvNickname;
    /*参数*/
    private final int RESULT_UPDATE = 0X0001;
    private final int RESULT_AVATAR = 0X0002;
    private User user;//个人信息Bean对象

    public PersonInfoActivityPresenter(PersonInfoActivity mPersonInfoActivity) {
        this.mPersonInfoActivity = mPersonInfoActivity;
        mPersonInfoActivity.setPresenter(this);
    }

    @Override
    public void start() {
        initObject();
        initView();
    }

    private void initObject() {
        ivAvatar = mPersonInfoActivity.getIvAvatar();
        tvPhone = mPersonInfoActivity.getTvPhone();
        tvNickname = mPersonInfoActivity.getTvNickname();

        user = User.getInstance();
    }

    private void initView() {
        if (user.getAvatar() != null && !user.getAvatar().equals("")) {
            Picasso.with(mPersonInfoActivity)    //context
                    .load(user.getAvatar())     //图片加载地址
                    .transform(new RoundTransform(400))
                    .error(android.R.drawable.ic_menu_delete)   //图片记载失败时显示的页面
                    .fit()      //智能展示图片，对于图片的大小和imageview的尺寸进行了测量，计算出最佳的大小和最佳的质量显示出来
                    .into(ivAvatar);    //需要加载图片的控件
        }
        tvPhone.setText(user.getMobile());
        tvNickname.setText(user.getNickname());
    }

    @Override
    public void updateAvatar(User user) {
        if (user.getAvatar() != null && !user.getAvatar().equals("")) {
            Picasso.with(mPersonInfoActivity)    //context
                    .load(user.getAvatar())     //图片加载地址
                    .transform(new RoundTransform(400))
                    .error(android.R.drawable.ic_menu_delete)   //图片记载失败时显示的页面
                    .fit()      //智能展示图片，对于图片的大小和imageview的尺寸进行了测量，计算出最佳的大小和最佳的质量显示出来
                    .into(ivAvatar);    //需要加载图片的控件
        }
    }

    @Override
    public void onViewClicked(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.ll_avatar:
                //头像
                intent = new Intent(mPersonInfoActivity, AvatarActivity.class);
                intent.putExtra("avatar", user.getAvatar());
                mPersonInfoActivity.startActivityForResult(intent, RESULT_AVATAR);
                break;
            case R.id.ll_nickname:
                // 修改昵称
                String nickname = tvNickname.getText().toString();
                intent = new Intent(mPersonInfoActivity, UpdatePersonInfoActivity.class);
                intent.putExtra("cache", nickname);
                intent.putExtra("type", "nickname");
                mPersonInfoActivity.startActivityForResult(intent, RESULT_UPDATE);
                break;
            case R.id.ll_share://分享有礼
                intent = new Intent(mPersonInfoActivity, ShareActivity.class);
                mPersonInfoActivity.startActivity(intent);
                break;
            case R.id.ll_address:
                //修改收货地址
                intent = new Intent(mPersonInfoActivity, AddressActivity.class);
                mPersonInfoActivity.startActivity(intent);
                break;
            case R.id.ll_logout:
                // 退出登录
                BaseUtils.promptLogout(mPersonInfoActivity);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case RESULT_UPDATE:
                    String type;
                    String update_data;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {// Android 4.4及以上版本适用
                        type = Objects.requireNonNull(data.getExtras()).getString("type");//得到新Activity 关闭后返回的数据
                        update_data = Objects.requireNonNull(data.getExtras()).getString("update_data");//得到新Activity 关闭后返回的数据
                    } else {
                        ToastUtil.show(mPersonInfoActivity, "您的Android系统版本过低，请升级系统至Android4.4及以上版本后使用此功能");
                        return;
                    }
                    if (type != null) {
                        switch (type) {
                            case "nickname":
                                tvNickname.setText(update_data);
                                break;
                        }
                    }
                    break;
            }
        }
    }
}
