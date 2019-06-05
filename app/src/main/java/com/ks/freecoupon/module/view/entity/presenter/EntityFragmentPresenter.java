package com.ks.freecoupon.module.view.entity.presenter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.ks.basictools.base_interface.StringCallBack;
import com.ks.basictools.utils.JSONUtil;
import com.ks.basictools.utils.LogUtils;
import com.ks.freecoupon.BaseActivity;
import com.ks.freecoupon.R;
import com.ks.freecoupon.module.contract.entity.EntityFragmentContract;
import com.ks.freecoupon.module.bean.Entity;
import com.ks.freecoupon.module.view.MainActivity;
import com.ks.freecoupon.module.view.entity.fragment.EntityFragment;
import com.ks.freecoupon.module.view.pc.activity.ConformOrderActivity;
import com.ks.freecoupon.override.RoundTransform;
import com.ks.freecoupon.utils.BaseUtils;
import com.ks.freecoupon.utils.OkHttpUtil;
import com.ks.freecoupon.utils.ScreenUtils;
import com.ks.freecoupon.utils.SharedPreferencesUtils;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bingoogolapple.bgabanner.BGABanner;
import okhttp3.Call;

/**
 * author：康少
 * date：2019/3/21
 * description：领实物——逻辑层
 */
public class EntityFragmentPresenter implements EntityFragmentContract.IEntityFragmentPresenter {
    private EntityFragment mEntityFragment;
    private Context mContext;
    /*控件对象*/
    private BGABanner banner;
    private TextView tvTitle;
    private WebView wvDesc;
    private SwipeRefreshLayout srl_refresh;
    /*参数*/
    private Handler mHandler;
    private Entity entity;//实物产品

    public EntityFragmentPresenter(EntityFragment mEntityFragment) {
        this.mEntityFragment = mEntityFragment;
        mEntityFragment.setPresenter(this);
    }

    @SuppressLint("HandlerLeak")
    @Override
    public void start() {
        initObject();

        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 1:
                        applyView(entity);
                        break;
                }
            }
        };
        getHttpData();
    }

    private void initObject() {
        banner = mEntityFragment.getBanner();
        tvTitle = mEntityFragment.getTvTitle();
        wvDesc = mEntityFragment.getWvDesc();
        mContext = mEntityFragment.getmContext();
        srl_refresh = mEntityFragment.getSrl_refresh();

        /*设置banner宽高比为1:1*/
        int height16_9 = ScreenUtils.getHeight16_9((MainActivity) mContext);
        ViewGroup.LayoutParams layoutParams = banner.getLayoutParams();
        layoutParams.height = height16_9 * 16 / 9;
        banner.setLayoutParams(layoutParams);

        srl_refresh.setColorSchemeResources(R.color.colorPrimary);
        srl_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getHttpData();
                srl_refresh.setRefreshing(false);//刷新结束，隐藏刷新进度条
            }
        });
    }

    /**
     * Description：获取网络数据
     */
    private void getHttpData() {
        ((BaseActivity) mContext).showLoading();
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        map.put("token", SharedPreferencesUtils.getParam(mContext, "token", ""));
        list.add(map);
        OkHttpUtil okHttpUtil = new OkHttpUtil(new StringCallBack() {
            @Override
            public void onResponse(String response, int id) {
                boolean status = JSONUtil.getBoolean(response, "status", false);
                if (status) {
                    String data = JSONUtil.getString(response, "data", "");
                    List<String> scorllImages = new ArrayList<>();
                    String[] img_lists = JSONUtil.getStringArray(data, "img_list", null);
                    if (img_lists != null) {
                        scorllImages.addAll(Arrays.asList(img_lists));
                    }
                    entity = new Entity(JSONUtil.getString(data, "id", "") + ""
                            , JSONUtil.getString(data, "name", "") + ""
                            , JSONUtil.getInt(data, "stock", 0) + ""
                            , JSONUtil.getString(data, "intro", "") + ""
                            , scorllImages);
                    mHandler.sendEmptyMessage(1);
                } else {
                    if (mContext != null) {
                        ((BaseActivity) mContext).dismissLoading();
                    }
                    LogUtils.e(JSONUtil.getString(response, "msg", ""));
                }
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                if (mContext != null) {
                    ((BaseActivity) mContext).dismissLoading();
                }
            }
        });
        okHttpUtil.requestCall(mContext, "goods.getfreegoods", list);
    }

    /**
     * Description：渲染视图
     * @param entity
     */
    private void applyView(Entity entity) {
        /*加载banner图*/
        List<String> imageList = entity.getImageList();
        banner.setAdapter(new BGABanner.Adapter<ImageView, String>() {
            @Override
            public void fillBannerItem(BGABanner banner, ImageView itemView, String model, int position) {
                if (model != null && !model.equals("")) {
                    Picasso.with(mContext)    //context
                            .load(model)     //图片加载地址
                            .transform(new RoundTransform(10))
                            .error(android.R.drawable.ic_menu_delete)   //图片记载失败时显示的页面
                            .fit()      //智能展示图片，对于图片的大小和imageview的尺寸进行了测量，计算出最佳的大小和最佳的质量显示出来
                            .into(itemView);    //需要加载图片的控件
                }
            }
        });
        banner.setData(imageList, Collections.singletonList(""));
        banner.setAutoPlayAble(false);//不自动播放
        /*加载内容*/
        tvTitle.setText(entity.getTitle());
        //实物介绍webView
        wvDesc.loadDataWithBaseURL(null, entity.getDesc(), "text/html", "UTF-8", null);
        wvDesc.getSettings().setUseWideViewPort(true);
        wvDesc.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        wvDesc.getSettings().setLoadWithOverviewMode(true);
        //取消加载中。。。
        ((BaseActivity) mContext).dismissLoading();
    }

    /**
     * Description：领实物——Button点击事件
     */
    @Override
    public void onViewClicked() {
        boolean isLogin = (boolean) SharedPreferencesUtils.getParam(mContext, "isLogin", false);
        if (isLogin) {
            Intent intent = new Intent(mContext, ConformOrderActivity.class);
            intent.putExtra("entity", entity);
            mContext.startActivity(intent);
        } else {
            BaseUtils.promptLogin(mContext);
        }
    }

    @Override
    public void onDestroy() {
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
    }
}
