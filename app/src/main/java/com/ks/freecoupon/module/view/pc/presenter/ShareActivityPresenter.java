package com.ks.freecoupon.module.view.pc.presenter;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AlertDialog;
import android.widget.ImageView;

import com.king.zxing.util.CodeUtils;
import com.ks.basictools.base_interface.StringCallBack;
import com.ks.basictools.publicView.TitleVarView;
import com.ks.basictools.utils.JSONUtil;
import com.ks.basictools.utils.LogUtils;
import com.ks.basictools.utils.ToastUtil;
import com.ks.freecoupon.R;
import com.ks.freecoupon.ks_interface.OnBottomMenuItemClickListener;
import com.ks.freecoupon.module.bean.User;
import com.ks.freecoupon.module.contract.pc.ShareActivityContract;
import com.ks.freecoupon.module.view.pc.activity.ShareActivity;
import com.ks.freecoupon.override.BottomMenuDialog;
import com.ks.freecoupon.override.RoundTransform;
import com.ks.freecoupon.utils.BitmapUtils;
import com.ks.freecoupon.utils.OkHttpUtil;
import com.ks.freecoupon.utils.SharedPreferencesUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

/**
 * author：康少
 * date：2019/3/27
 * description：分享有礼——逻辑层
 */
public class ShareActivityPresenter implements ShareActivityContract.IShareActivityPresenter, TitleVarView.TitleBarOnRightClickListener {
    private ShareActivity mShareActivity;

    private ImageView image;
    private ImageView iv_image;
    private BottomMenuDialog mBottomMenuDialog;
    private Bitmap bitmap;//二维码Bitmap

    public ShareActivityPresenter(ShareActivity mShareActivity) {
        this.mShareActivity = mShareActivity;
        mShareActivity.setPresenter(this);
    }

    @Override
    public void start() {
        initObject();
        showTipDialog();
        showQRCode();
        mShareActivity.setRightTitleBarOnClickListener(this);
        getFreeGoods();
    }

    private void initObject() {
        image = mShareActivity.getImage();
        iv_image = mShareActivity.getIv_image();
        mBottomMenuDialog = new BottomMenuDialog(mShareActivity);
    }

    /**
     * Description：展示提示弹出框
     */
    private void showTipDialog() {
        AlertDialog dialog = new AlertDialog.Builder(mShareActivity)
                .setTitle("温馨提示")
                .setIcon(mShareActivity.getResources().getDrawable(R.mipmap.ic_launcher))
                .setMessage("截图分享，用户下载APP后您可获得积分奖励哦☺")
                .setPositiveButton("确定",null)
                .create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(mShareActivity.getResources().getColor(R.color.colorPrimary));
        dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(mShareActivity.getResources().getColor(R.color.colorPrimary));
    }

    /**
     * Description：展示二维码
     */
    private void showQRCode() {
        OkHttpUtil okHttpUtil = new OkHttpUtil(new StringCallBack() {
            @Override
            public void onResponse(String response, int id) {
                boolean status = JSONUtil.getBoolean(response, "status", false);
                if (status) {
                    String content = JSONUtil.getString(response, "data", "") + "?mobile=" + User.getInstance().getMobile();
                    //生成二维码最好放子线程生成防止阻塞UI，这里只是演示
                    Bitmap logo = BitmapFactory.decodeResource(mShareActivity.getResources(),R.mipmap.ic_launcher);
                    Bitmap bitmap =  CodeUtils.createQRCode(content,image.getWidth(),logo);
                    //显示二维码
                    image.setImageBitmap(bitmap);
                } else {
                    LogUtils.e(JSONUtil.getString(response, "msg", ""));
                    ToastUtil.show(mShareActivity, "二维码获取失败，请重新进入！");
                }
            }
            @Override
            public void onError(Call call, Exception e, int id) {
                LogUtils.e(e.getMessage());
            }
        });
        okHttpUtil.requestCall(mShareActivity, "user.download", null);
    }

    /**
     * Description：保存图片到相册
     */
    private void saveQRCodeToLocal() {
        BitmapDrawable bmpDrawable = (BitmapDrawable) image.getDrawable();
        Bitmap bmp = bmpDrawable.getBitmap();
        String path = BitmapUtils.saveBitmap(mShareActivity, bmp);
        LogUtils.d("图片已保存至相册" + path);
        ToastUtil.show(mShareActivity, "已保存至相册");
    }

    /**
     * Description：展示免费商品
     */
    private void getFreeGoods() {
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        map.put("token", SharedPreferencesUtils.getParam(mShareActivity, "token", ""));
        list.add(map);
        OkHttpUtil okHttpUtil = new OkHttpUtil(new StringCallBack() {
            @Override
            public void onResponse(String response, int id) {
                boolean status = JSONUtil.getBoolean(response, "status", false);
                if (status) {
                    String data = JSONUtil.getString(response, "data", "");
                    String[] img_lists = JSONUtil.getStringArray(data, "img_list", null);
                    String name = JSONUtil.getString(data, "name", "");
                    String image = "";
                    if (img_lists != null) {
                        image = img_lists[0];
                    }
                    showFreeGoods(image, name);
                } else {
                    LogUtils.e(JSONUtil.getString(response, "msg", ""));
                }
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                LogUtils.e(e.getMessage());
            }
        });
        okHttpUtil.requestCall(mShareActivity, "goods.getfreegoods", list);
    }

    /**
     * Description：展示免费商品
     */
    private void showFreeGoods(String image, String name) {
        if (!image.equals("")) {
            Picasso.with(mShareActivity)
                    .load(image)     //图片加载地址
                    .transform(new RoundTransform(0))
                    .error(android.R.drawable.ic_menu_delete)   //图片记载失败时显示的页面
                    .fit()      //智能展示图片，对于图片的大小和imageview的尺寸进行了测量，计算出最佳的大小和最佳的质量显示出来
                    .into(iv_image);    //需要加载图片的控件
        }
    }

    @Override
    public void onRightClick() {
        mBottomMenuDialog.builder();
        if (mBottomMenuDialog.getSheetItemList() == null || mBottomMenuDialog.getSheetItemList().size() <= 0) {
            mBottomMenuDialog.addSheetItem("保存到手机", BottomMenuDialog.SheetItemColor.Gray, new OnBottomMenuItemClickListener() {
                @Override
                public void onClick(int which) {
                    saveQRCodeToLocal();//保存图片到相册
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
}
