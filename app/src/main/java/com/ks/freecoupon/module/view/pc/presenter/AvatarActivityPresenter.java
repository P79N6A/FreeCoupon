package com.ks.freecoupon.module.view.pc.presenter;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.ks.basictools.AppManager;
import com.ks.basictools.base_interface.StringCallBack;
import com.ks.basictools.utils.JSONUtil;
import com.ks.basictools.utils.LogUtils;
import com.ks.basictools.utils.ToastUtil;
import com.ks.freecoupon.BaseActivity;
import com.ks.freecoupon.ks_interface.OnBottomMenuItemClickListener;
import com.ks.freecoupon.module.bean.User;
import com.ks.freecoupon.module.contract.pc.AvatarActivityContract;
import com.ks.freecoupon.module.view.pc.activity.AvatarActivity;
import com.ks.freecoupon.override.BottomMenuDialog;
import com.ks.freecoupon.override.LoadingDialog;
import com.ks.freecoupon.override.RoundTransform;
import com.ks.freecoupon.override.ToastKs;
import com.ks.freecoupon.utils.Base64Utils;
import com.ks.freecoupon.utils.BitmapSaveLocal;
import com.ks.freecoupon.utils.OkHttpUtil;
import com.ks.freecoupon.utils.PhotoUtils;
import com.ks.freecoupon.utils.SharedPreferencesUtils;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.entity.LocalMedia;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

import static android.app.Activity.RESULT_OK;
import static com.ks.freecoupon.utils.PhotoUtils.hasSdcard;

/**
 * author：康少
 * date：2019/3/26
 * description：头像设置——逻辑层
 */
public class AvatarActivityPresenter implements AvatarActivityContract.IAvatarActivityPresenter {
    private AvatarActivity mAvatarActivity;
    /*全局加载中Dialog*/
    private LoadingDialog mLoadingDialog;

    private ImageView ivAvatar;
    private Button btnSave;

    private BottomMenuDialog mBottomMenuDialog;
    private static final int CODE_CAMERA_REQUEST = 0xa1;

    public AvatarActivityPresenter(AvatarActivity mAvatarActivity) {
        this.mAvatarActivity = mAvatarActivity;
        mAvatarActivity.setPresenter(this);
    }

    @Override
    public void start() {
        initObject();
        initView();
    }

    private void initObject() {
        ivAvatar = mAvatarActivity.getIvAvatar();
        btnSave = mAvatarActivity.getBtnSave();

        mBottomMenuDialog = new BottomMenuDialog(mAvatarActivity);
        mLoadingDialog = LoadingDialog.getInstance();
    }

    /**
     * Description：显示加载中。。。
     */
    public void showLoading() {
        if (!AppManager.getAppManager().currentActivity().isFinishing()) {
            mLoadingDialog.show();
        }
    }

    /**
     * Description：关掉加载中。。。
     */
    public void dismissLoading() {
        if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
        }
    }

    private void initView() {
        String avatar = mAvatarActivity.getIntent().getStringExtra("avatar");
        if (!avatar.equals("")) {
            Picasso.with(mAvatarActivity)    //context
                    .load(avatar)     //图片加载地址
                    .transform(new RoundTransform(10))
                    .error(android.R.drawable.ic_menu_delete)   //图片记载失败时显示的页面
                    .fit()      //智能展示图片，对于图片的大小和imageview的尺寸进行了测量，计算出最佳的大小和最佳的质量显示出来
                    .into(ivAvatar);    //需要加载图片的控件
        }
    }

    /**
     * Description：••• ——点击事件
     */
    @Override
    public void onRightClick() {
        mBottomMenuDialog.builder();
        if (mBottomMenuDialog.getSheetItemList() == null || mBottomMenuDialog.getSheetItemList().size() <= 0) {
            mBottomMenuDialog.addSheetItem("更换头像", BottomMenuDialog.SheetItemColor.Gray, new OnBottomMenuItemClickListener() {
                @Override
                public void onClick(int which) {
                    if (hasSdcard()) {
                        PhotoUtils.openPic(mAvatarActivity, CODE_CAMERA_REQUEST);
                    } else {
                        ToastUtil.show(mAvatarActivity, "设备没有SD卡！");
                        LogUtils.e("设备没有SD卡");
                    }
                }
            });
            mBottomMenuDialog.addSheetItem("取消", BottomMenuDialog.SheetItemColor.Gray, new OnBottomMenuItemClickListener() {
                @Override
                public void onClick(int which) {
                    mBottomMenuDialog.dismiss();
                }
            });
        }
        mBottomMenuDialog.show();
    }

    /**
     * Description：预览
     */
    private void showAvatar(String picPath) {
        Picasso.with(mAvatarActivity)    //context
                .load("file://" + picPath)     //图片加载地址
                .transform(new RoundTransform(4000))
                .error(android.R.drawable.ic_menu_delete)   //图片记载失败时显示的页面
                .fit()      //智能展示图片，对于图片的大小和imageView的尺寸进行了测量，计算出最佳的大小和最佳的质量显示出来
                .into(ivAvatar);    //需要加载图片的控件
        btnSave.setVisibility(View.VISIBLE);
    }

    /**
     * Description：保存——点击事件
     */
    @Override
    public void onViewClicked() {
        showLoading();
        // 保存头像，并返回更新
        saveAvatar();
    }

    /**
     * Description：保存头像
     */
    private void saveAvatar() {
        Drawable drawable = ivAvatar.getDrawable();
        BitmapDrawable bd = (BitmapDrawable) drawable;
        Bitmap bitmap = bd.getBitmap();
        String path = BitmapSaveLocal.saveBitmap(mAvatarActivity,bitmap);
        String base64 = Base64Utils.imageToBase64(path);
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        map.put("token", SharedPreferencesUtils.getParam(mAvatarActivity, "token", ""));
        map.put("avatar", base64);
        list.add(map);
        OkHttpUtil okHttpUtil = new OkHttpUtil(new StringCallBack() {
            @Override
            public void onResponse(String response, int id) {
                if (!JSONUtil.getBoolean(response, "status", false)) {
                    ToastUtil.show(mAvatarActivity, JSONUtil.getString(response, "msg", "未知错误"));
                    dismissLoading();
                } else {
                    String image = JSONUtil.getString(response, "data", "");
                    User.getInstance().setAvatar(image);
                    //PersonInfoActivityPresenter 更新 头像
                    EventBus.getDefault().post(User.getInstance());
                    ToastKs.show(mAvatarActivity, "保存成功");
                    dismissLoading();
                    mAvatarActivity.finish();
                }
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                dismissLoading();
            }
        });
        okHttpUtil.requestCall(mAvatarActivity, "user.changeavatar", list);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case CODE_CAMERA_REQUEST://拍照完成回调
                    List<LocalMedia> localMedia = PictureSelector.obtainMultipleResult(data);
                    String picPath;
                    if (localMedia.get(0).isCompressed()) {
                        picPath = localMedia.get(0).getCompressPath();
                    } else if (localMedia.get(0).isCut()) {
                        picPath = localMedia.get(0).getCutPath();
                    } else {
                        picPath = localMedia.get(0).getPath();
                    }
                    showAvatar(picPath);
                    break;
            }
        }
    }
}
