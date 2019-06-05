package com.ks.freecoupon.module.view.pc.activity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.ks.basictools.base_interface.StringCallBack;
import com.ks.basictools.publicView.TitleVarView;
import com.ks.basictools.utils.JSONUtil;
import com.ks.basictools.utils.LogUtils;
import com.ks.basictools.utils.ToastUtil;
import com.ks.freecoupon.R;
import com.ks.freecoupon.module.bean.ScoreGoods;
import com.ks.freecoupon.module.bean.User;
import com.ks.freecoupon.module.view.MainActivity;
import com.ks.freecoupon.override.RoundTransform;
import com.ks.freecoupon.override.ToastKs;
import com.ks.freecoupon.utils.BaseUtils;
import com.ks.freecoupon.utils.OkHttpUtil;
import com.ks.freecoupon.utils.ScreenUtils;
import com.ks.freecoupon.utils.SharedPreferencesUtils;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bingoogolapple.bgabanner.BGABanner;
import okhttp3.Call;

/**
 * Date:2019/4/17
 * Author：康少
 * Description：商品详情
 */
public class ScoreGoodsDetailActivity extends TitleVarView implements View.OnClickListener {

    private BGABanner banner;
    private TextView tvName;
    private TextView tvPrice;
    private WebView wvDesc;
    private Button btnChange;
    /*参数*/
    private Handler mHandler;
    private String goods_id;
    private List<String> imageList;
    private ScoreGoods scoreGoods;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score_goods_detail);
        setTitleBar(true, "商品详情", "");
        findId();
        start();
    }

    private void findId() {
        banner = findViewById(R.id.banner);
        tvName = findViewById(R.id.tv_name);
        tvPrice = findViewById(R.id.tv_price);
        wvDesc = findViewById(R.id.wv_desc);
        btnChange = findViewById(R.id.btn_change);

        /*设置banner宽高比为16:9*/
        int height16_9 = ScreenUtils.getHeight16_9(this);
        ViewGroup.LayoutParams layoutParams = banner.getLayoutParams();
        layoutParams.height = height16_9 * 16 / 9;
        banner.setLayoutParams(layoutParams);

        imageList = new ArrayList<>();
        btnChange.setOnClickListener(this);
    }

    @SuppressLint("HandlerLeak")
    private void start() {
        goods_id = getIntent().getStringExtra("goods_id");
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 1:
                        applyView();
                        break;
                }
            }
        };
        getHttpData();
    }

    /**
     * Description：渲染视图
     */
    private void applyView() {
        if (scoreGoods != null) {
            /*加载banner图*/
            List<String> imageList = scoreGoods.getImageList();
            banner.setAdapter(new BGABanner.Adapter<ImageView, String>() {
                @Override
                public void fillBannerItem(BGABanner banner, ImageView itemView, String model, int position) {
                    if (model != null && !model.equals("")) {
                        Picasso.with(ScoreGoodsDetailActivity.this)    //context
                                .load(model)     //图片加载地址
                                .transform(new RoundTransform(0))
                                .error(android.R.drawable.ic_menu_delete)   //图片记载失败时显示的页面
                                .fit()      //智能展示图片，对于图片的大小和imageview的尺寸进行了测量，计算出最佳的大小和最佳的质量显示出来
                                .into(itemView);    //需要加载图片的控件
                    }
                }
            });
            banner.setData(imageList, Collections.singletonList(""));
            banner.setAutoPlayAble(false);//不自动播放
            tvName.setText(scoreGoods.getGoodsDesc());
            tvPrice.setText(scoreGoods.getScorePrice());
            //实物介绍webView
            wvDesc.loadDataWithBaseURL(null, scoreGoods.getGoodsDetail(), "text/html", "UTF-8", null);
            wvDesc.getSettings().setUseWideViewPort(true);
            wvDesc.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
            wvDesc.getSettings().setLoadWithOverviewMode(true);
        }
    }

    /**
     * Description：获取详情
     */
    private void getHttpData() {
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        map.put("token", SharedPreferencesUtils.getParam(this, "token", ""));
        map.put("goods_id", goods_id);
        list.add(map);
        OkHttpUtil okHttpUtil = new OkHttpUtil(new StringCallBack() {
            @Override
            public void onResponse(String response, int id) {
                boolean status = JSONUtil.getBoolean(response, "status", false);
                if (!status) {
                    ToastUtil.show(ScoreGoodsDetailActivity.this, JSONUtil.getString(response, "msg", ""));
                } else {
                    imageList.clear();
                    String data = JSONUtil.getString(response, "data", "");
                    String[] img_lists = JSONUtil.getStringArray(data, "img_list", null);
                    if (img_lists != null) {
                        imageList.addAll(Arrays.asList(img_lists));
                    }
                    scoreGoods = new ScoreGoods(
                            imageList
                            , "" + JSONUtil.getString(data, "name", "")
                            , "" + JSONUtil.getInt(data, "score", 0)
                            , "" + JSONUtil.getInt(data, "is_physical", 0)
                            , "" + JSONUtil.getString(data, "detail", "")
                    );
                }
                mHandler.sendEmptyMessage(1);
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                LogUtils.e(e.getMessage());
            }
        });
        okHttpUtil.requestCall(this, "goods.getscoregoodsdetail", list);
    }

    /**
     * Description：兑换——点击事件
     */
    @Override
    public void onClick(View v) {
        boolean isLogin = (boolean) SharedPreferencesUtils.getParam(this, "isLogin", false);
        if (!isLogin) {
            BaseUtils.promptLogin(ScoreGoodsDetailActivity.this);
        } else if (User.getInstance().getShip_area().equals("") || User.getInstance().getShip_address().equals("")) {
            ToastKs.show(ScoreGoodsDetailActivity.this, "请先完善收货地址");
        } else {
            change();
        }
    }

    /**
     * Description：立即兑换
     */
    private void change() {
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        map.put("token", SharedPreferencesUtils.getParam(this, "token", ""));
        map.put("goods_id", goods_id);
        list.add(map);
        OkHttpUtil okHttpUtil = new OkHttpUtil(new StringCallBack() {
            @Override
            public void onResponse(String response, int id) {
                boolean status = JSONUtil.getBoolean(response, "status", false);
                if (!status) {
                    ToastUtil.show(ScoreGoodsDetailActivity.this, JSONUtil.getString(response, "msg", ""));
                } else {
                    String msg = "";
                    switch (scoreGoods.getIs_physical()) {
                        case "1"://1为虚拟商品
                            msg = "兑换成功，请在“我的-联系我们”关注微信公众号后领取！";
                            break;
                        case "0"://0 为实物商品
                            msg = "兑换成功，我们将3-5个工作日内快递发出，请耐心等候！";
                            break;
                    }
                    // 兑换成功的操作
                    AlertDialog alertDialog = new AlertDialog.Builder(ScoreGoodsDetailActivity.this)
                            .setTitle("温馨提示")
                            .setMessage(msg)
                            .setIcon(R.drawable.f015)
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //PCFragmentPresenter 更新 积分数
                                    int point = User.getInstance().getPoint() - Integer.parseInt(scoreGoods.getScorePrice());
                                    User.getInstance().setPoint(point);
                                    EventBus.getDefault().post(User.getInstance());
                                    finish();
                                }
                            })
                            .show();
                    alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorPrimary));
                }
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                LogUtils.e(e.getMessage());
            }
        });
        okHttpUtil.requestCall(this, "goods.receivescoregoods", list);
    }
}
