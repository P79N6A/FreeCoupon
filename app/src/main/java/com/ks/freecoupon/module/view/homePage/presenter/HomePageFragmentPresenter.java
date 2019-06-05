package com.ks.freecoupon.module.view.homePage.presenter;

import android.Manifest;
import android.annotation.SuppressLint;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import android.graphics.Color;
import android.graphics.Paint;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.ks.basictools.base_interface.StringCallBack;
import com.ks.basictools.overView.CommonAdapter;
import com.ks.basictools.overView.HeaderAndFooterWrapper;
import com.ks.basictools.overView.LoadMoreWrapper;
import com.ks.basictools.utils.JSONUtil;
import com.ks.basictools.utils.LogUtils;
import com.ks.basictools.utils.ToastUtil;

import com.ks.freecoupon.BaseActivity;
import com.ks.freecoupon.R;
import com.ks.freecoupon.module.bean.BannerBean;
import com.ks.freecoupon.module.bean.HotGoods;
import com.ks.freecoupon.module.bean.JingDongGoods;
import com.ks.freecoupon.module.bean.NearGoods;
import com.ks.freecoupon.module.bean.TaoBaoGoods;
import com.ks.freecoupon.module.contract.homePage.HomePageFragmentContract;
import com.ks.freecoupon.module.view.MainActivity;
import com.ks.freecoupon.module.view.coupon.activity.CopyTaoWordActivity;
import com.ks.freecoupon.module.view.coupon.activity.GetNearCouponActivity;
import com.ks.freecoupon.module.view.coupon.activity.WebViewActivity;
import com.ks.freecoupon.module.view.homePage.fragment.HomePageFragment;
import com.ks.freecoupon.module.view.search.activity.SearchResultActivity;
import com.ks.freecoupon.override.RoundTransform;
import com.ks.freecoupon.utils.BaseUtils;
import com.ks.freecoupon.utils.BdLocationUtil;
import com.ks.freecoupon.utils.OkHttpUtil;
import com.ks.freecoupon.utils.ScreenUtils;
import com.ks.freecoupon.utils.SharedPreferencesUtils;

import com.squareup.picasso.Picasso;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bingoogolapple.bgabanner.BGABanner;
import okhttp3.Call;

/**
 * author：康少
 * date：2019/3/12
 * description：首页Fragment——逻辑层
 */
public class HomePageFragmentPresenter implements HomePageFragmentContract.IHomePageFragmentPresenter, BGABanner.Delegate {
    private HomePageFragment mHomePageFragment;
    private Context mContext;
    /*控件对象*/
    private RecyclerView rv_list;
//    private SwipeRefreshLayout srl_refresh;
    @BindView(R.id.banner)
    BGABanner banner;
    @BindView(R.id.rl_container)
    RelativeLayout rl_container;
    @BindView(R.id.banner_recommend)
    BGABanner banner_recommend;//今日推荐
    /*参数*/
    private List<BannerBean> bannerBeanList;
    private List<BannerBean> recommendBeanList;//今日推荐
    private List<HotGoods> hotGoodsList;
    private CommonAdapter mAdapter;
    private HeaderAndFooterWrapper mHeaderAndFooterWrapper;
    private LoadMoreWrapper mLoadMoreWrapper;
    private int currentPage = 1;//当前页数
    private int total = 10;//首页默认推荐商品数
    private boolean isFull = false;//是否已满total个商品数
    /*定位*/
    private final int RESULT_REQUEST_PERMISSION = 0x0001;
    private final int BAIDU_ACCESS_COARSE_LOCATION = 0x0002;
    private double longitude = 0;//经度
    private double latitude = 0;//纬度

    private Handler mHandler;
    private Handler bannerHandler;//轮播图Handler

    public HomePageFragmentPresenter(HomePageFragment mHomePageFragment) {
        this.mHomePageFragment = mHomePageFragment;
        mHomePageFragment.setPresenter(this);
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
                        applyView();//展示数据
                        break;
                    case 2:
                        getFreeGoods();//获取免费商品
                        break;
                    case 3:
                        getTaoBaoGoods();//获取淘宝商品
                        break;
                }
            }
        };
        bannerHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 1:
                        applyBannerView();//加载首页Banner图
                        break;
                    case 2:
                        applyRecommendBannerView();//加载今日推荐Banner图
                        break;
                }
            }
        };
        getLocation();//获取经纬度
    }

    private void initObject() {
        rv_list = mHomePageFragment.getRv_list();
        mContext = mHomePageFragment.getmContext();
//        srl_refresh = mHomePageFragment.getSrl_refresh();
        rv_list.setLayoutManager(new GridLayoutManager(mContext, 2));

        rv_list.addOnScrollListener(new RecyclerView.OnScrollListener() {
            private int totalDy = 0;

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                totalDy += dy;
                LogUtils.d("recyclerView:" + recyclerView + "\n dx:" + dx + "\n dy:" + dy + "\n totalDy:" + totalDy);
                if (totalDy >= 500) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        ((MainActivity) mContext).getWindow().setStatusBarColor(mContext.getResources().getColor(R.color.white));
                    }
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        //设置状态栏的颜色
                        ((MainActivity) mContext).getWindow().setStatusBarColor(Color.TRANSPARENT);
                    }
                }
            }
        });

        bannerBeanList = new ArrayList<>();
        recommendBeanList = new ArrayList<>();
        hotGoodsList = new ArrayList<>();

//        srl_refresh.setColorSchemeResources(R.color.colorPrimary);
//        srl_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                hotGoodsList.clear();
//                bannerBeanList.clear();
//                recommendBeanList.clear();
//                currentPage = 1;
//                srl_refresh.setRefreshing(false);//刷新结束，隐藏刷新进度条
//                getLocation();//获取经纬度
//            }
//        });
    }

    /**
     * Description：获取定位经纬度
     */
    private void getLocation() {
        if (mContext != null) {
            ((BaseActivity) mContext).showLoading();
        }
        myPermissionRequest();
    }

    /**
     * Description：获取附近优惠券列表
     */
    private void getNearGoods() {
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        map.put("page_no", currentPage);
        map.put("lng", longitude);
        map.put("lat", latitude);
        list.add(map);
        OkHttpUtil okHttpUtil = new OkHttpUtil(new StringCallBack() {
            @Override
            public void onResponse(String response, int id) {
                boolean status = JSONUtil.getBoolean(response, "status", false);
                if (status) {
                    hotGoodsList.clear();
                    String[] dataList = JSONUtil.getStringArray(response, "data", null);
                    if (dataList != null) {
                        if (dataList.length >= total) {//数据长度，超过或等于默认条数
                            isFull = true;
                            for (int i = 0; i < total; i++) {
                                String mkt_price1 = JSONUtil.getString(dataList[i], "mkt_price", "-1");
                                if (!mkt_price1.equals("")) {
                                    //首页只看无门槛
                                    double mkt_price = Double.valueOf(mkt_price1);
                                    if (mkt_price == 0) {
                                        HotGoods hotGoods = new HotGoods(JSONUtil.getInt(dataList[i], "id", -1) + ""
                                                , JSONUtil.getString(dataList[i], "name", "") + ""
                                                , JSONUtil.getString(dataList[i], "img", "") + ""
                                                , mkt_price + ""
                                                , JSONUtil.getString(dataList[i], "address", "") + ""
                                                , JSONUtil.getString(dataList[i], "tel", "") + ""
                                                , JSONUtil.getString(dataList[i], "coupon_content", "") + ""
                                                , JSONUtil.getString(dataList[i], "stime", "") + ""
                                                , JSONUtil.getString(dataList[i], "etime", "") + "");
                                        hotGoodsList.add(hotGoods);
                                    }
                                }
                            }
                            //直接加载数据视图
                            mHandler.sendEmptyMessage(1);
                        } else {//数据长度不足默认条数
                            isFull = false;
                            for (int i = 0; i < dataList.length; i++) {
                                String mkt_price1 = JSONUtil.getString(dataList[i], "mkt_price", "-1");
                                if (!mkt_price1.equals("")) {
                                    //首页只看无门槛
                                    double mkt_price = Double.valueOf(mkt_price1);
                                    if (mkt_price == 0) {
                                        HotGoods hotGoods = new HotGoods(JSONUtil.getInt(dataList[i], "id", -1) + ""
                                                , JSONUtil.getString(dataList[i], "name", "") + ""
                                                , JSONUtil.getString(dataList[i], "img", "") + ""
                                                , mkt_price1 + ""
                                                , JSONUtil.getString(dataList[i], "address", "") + ""
                                                , JSONUtil.getString(dataList[i], "tel", "") + ""
                                                , JSONUtil.getString(dataList[i], "coupon_content", "") + ""
                                                , JSONUtil.getString(dataList[i], "stime", "") + ""
                                                , JSONUtil.getString(dataList[i], "etime", "") + "");
                                        hotGoodsList.add(hotGoods);
                                    }
                                }
                            }
                            //继续请求获取免费商品
                            mHandler.sendEmptyMessage(2);
                        }
                    } else {
                        //继续请求获取免费商品
                        mHandler.sendEmptyMessage(2);
                    }
                } else {
                    LogUtils.e("附近领券 response：" + response);
                    if (mContext != null) {
                        ((BaseActivity) mContext).dismissLoading();
                    }
                }
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                if (mContext != null) {
                    ((BaseActivity) mContext).dismissLoading();
                }
            }
        });
        okHttpUtil.requestCall(mContext, "coupon.getplatformlist", list);
    }

    /**
     * Description：获取免费商品
     */
    private void getFreeGoods() {
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
                    HotGoods hotGoods = new HotGoods(JSONUtil.getString(data, "name", "") + "",
                            scorllImages.size() > 0 ? scorllImages.get(0) : "");
                    hotGoodsList.add(hotGoods);
                    if (hotGoodsList.size() >= total) {
                        // 展示商品
                        mHandler.sendEmptyMessage(1);
                    } else {
                        // 继续请求淘宝数据
                        mHandler.sendEmptyMessage(3);
                    }
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
     * Description：获取淘宝商品
     */
    private void getTaoBaoGoods() {
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        map.put("page_no", currentPage);
        list.add(map);
        OkHttpUtil okHttpUtil = new OkHttpUtil(new StringCallBack() {
            @Override
            public void onResponse(String response, int id) {
                boolean status = JSONUtil.getBoolean(response, "status", false);
                if (status) {
                    String data = JSONUtil.getString(response, "data", "");
                    String[] coupon_lists = JSONUtil.getStringArray(data, "coupon_list", null);
                    if (coupon_lists != null) {
                        int count = total - hotGoodsList.size();//剩余展示商品的个数
                        if (coupon_lists.length >= count) {
                            for (int i = 0; i < count; i++) {
                                //原价
                                double zk_final_price = Float.parseFloat(JSONUtil.getString(coupon_lists[i], "zk_final_price", ""));
                                //优惠券额度
                                double coupon_value = Float.parseFloat(JSONUtil.getString(coupon_lists[i], "coupon_value", ""));
                                //现价
                                double discountPrice = zk_final_price - coupon_value;
                                HotGoods hotGoods = new HotGoods(JSONUtil.getString(coupon_lists[i], "title", "") + ""
                                        , JSONUtil.getString(coupon_lists[i], "pict_url", "") + ""
                                        , BaseUtils.doubleFormatBy2(zk_final_price) + ""
                                        , BaseUtils.doubleFormatBy2(coupon_value) + ""
                                        , BaseUtils.doubleFormatBy2(discountPrice) + ""
                                        , JSONUtil.getString(coupon_lists[i], "coupon_pwd", "") + "");
                                hotGoodsList.add(hotGoods);
                            }
                            mHandler.sendEmptyMessage(1);
                        } else {
                            for (int i = 0; i < coupon_lists.length; i++) {
                                //原价
                                double zk_final_price = Float.parseFloat(JSONUtil.getString(coupon_lists[i], "zk_final_price", ""));
                                //优惠券额度
                                double coupon_value = Float.parseFloat(JSONUtil.getString(coupon_lists[i], "coupon_value", ""));
                                //现价
                                double discountPrice = zk_final_price - coupon_value;
                                HotGoods hotGoods = new HotGoods(JSONUtil.getString(coupon_lists[i], "title", "") + ""
                                        , JSONUtil.getString(coupon_lists[i], "pict_url", "") + ""
                                        , BaseUtils.doubleFormatBy2(zk_final_price) + ""
                                        , BaseUtils.doubleFormatBy2(coupon_value) + ""
                                        , BaseUtils.doubleFormatBy2(discountPrice) + ""
                                        , JSONUtil.getString(coupon_lists[i], "coupon_pwd", "") + "");
                                hotGoodsList.add(hotGoods);
                            }
                            mHandler.sendEmptyMessage(4);
                        }
                    } else {
                        if (hotGoodsList.size() >= total) {
                            //展示数据
                            mHandler.sendEmptyMessage(1);
                        } else {
                            //TODO 添加京东数据
                        }
                    }
                } else {
                    LogUtils.e("淘宝领券 response：" + response);
                    String msg = JSONUtil.getString(response, "msg", "");
                    ToastUtil.show(mContext, msg);
                    if (mContext != null) {
                        ((BaseActivity) mContext).dismissLoading();
                    }
                }
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                if (mContext != null) {
                    ((BaseActivity) mContext).dismissLoading();
                }
            }
        });
        okHttpUtil.requestCall(mContext, "coupon.gettblist", list);
    }

    /**
     * Description：加载视图
     */
    private void applyView() {
        mAdapter = new CommonAdapter<HotGoods>(mContext, R.layout.item_hot_goods, hotGoodsList) {
            @SuppressLint("SetTextI18n")
            @Override
            protected void convert(ViewHolder holder, final HotGoods hotGoods, int position) {
                if (!hotGoods.getImg().equals("")) {
                    Picasso.with(mContext)    //context
                            .load(hotGoods.getImg())     //图片加载地址
                            .transform(new RoundTransform(10))
                            .error(android.R.drawable.ic_menu_delete)   //图片记载失败时显示的页面
                            .fit()      //智能展示图片，对于图片的大小和imageview的尺寸进行了测量，计算出最佳的大小和最佳的质量显示出来
                            .into((ImageView) holder.getView(R.id.image_item));    //需要加载图片的控件
                }
                ((TextView) holder.getView(R.id.tv_name)).setText(hotGoods.getTitle());
                switch (hotGoods.getType()) {
                    case "1"://附近
                        ((TextView) holder.getView(R.id.tv_juan)).setText(hotGoods.getCoupon_content());
                        ((TextView) holder.getView(R.id.tv_condition)).setText("满" + hotGoods.getMkt_price() + "元可用");
                        break;
                    case "2"://免费
                        ((TextView) holder.getView(R.id.tv_juan)).setText("免费领");
//                        ((TextView) holder.getView(R.id.tv_condition)).setText("积分满50分可领");
                        break;
                    case "3"://淘宝
                        ((TextView) holder.getView(R.id.tv_juan)).setText("券" + hotGoods.getPrice() + "元");
                        ((TextView) holder.getView(R.id.tv_yuanjia)).setText("￥" + hotGoods.getYuanjia());
                        ((TextView) holder.getView(R.id.tv_yuanjia)).getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                        ((TextView) holder.getView(R.id.tv_xianjia)).setText("￥" + hotGoods.getDiscountPrice());
                        break;
                }
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolean isLogin = (boolean) SharedPreferencesUtils.getParam(mContext, "isLogin", false);
                        if (isLogin) {
                            Intent intent;
                            switch (hotGoods.getType()) {
                                case "1"://附近
                                    NearGoods nearGoods = new NearGoods(Integer.parseInt(hotGoods.getCoupon_id()),
                                            hotGoods.getTitle() + "",
                                            hotGoods.getImg() + "",
                                            hotGoods.getDiscountPrice() + "",
                                            hotGoods.getMkt_price() + "",
                                            hotGoods.getAddress() + "",
                                            hotGoods.getTel() + "",
                                            hotGoods.getCoupon_content() + "",
                                            hotGoods.getStime() + "",
                                            hotGoods.getEtime() + "",
                                            "",
                                            "");
                                    intent = new Intent(mContext, GetNearCouponActivity.class);
                                    intent.putExtra("nearGoods", nearGoods);
                                    if (mContext != null) {
                                        mContext.startActivity(intent);
                                    }
                                    break;
                                case "2"://免费
                                    if (mContext != null) {
                                        RadioButton rb_entity = ((MainActivity) mContext).findViewById(R.id.rb_entity);
                                        rb_entity.setChecked(true);
                                    }
                                    break;
                                case "3"://淘宝
                                    // 跳转复制淘口令界面
                                    intent = new Intent(mContext, CopyTaoWordActivity.class);
                                    intent.putExtra("coupon_pwd", hotGoods.getCoupon_pwd());
                                    intent.putExtra("title", hotGoods.getTitle());
                                    intent.putExtra("yuanjia", hotGoods.getYuanjia());
                                    intent.putExtra("discount_price", hotGoods.getDiscountPrice());
                                    if (mContext != null) {
                                        mContext.startActivity(intent);
                                    }
                                    break;
                            }
                        } else {
                            BaseUtils.promptLogin(mContext);
                        }
                    }
                });
            }
        };
        // 加载头部
        loadHeader();
    }

    /**
     * Description：加载头部
     */
    private void loadHeader() {
        mHeaderAndFooterWrapper = new HeaderAndFooterWrapper(mAdapter);
        View view = LayoutInflater.from(mContext).inflate(R.layout.fragment_home_page_header, rv_list, false);
        ButterKnife.bind(this, view);
//        /*获取状态栏高度，不与状态栏重合*/
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if (mContext != null) {
//                int statusBarHeight = StatusBarUtil.getStatusBarHeight(mContext);
//                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//                layoutParams.setMargins(0, statusBarHeight, 0, 0);
//                rl_container.setLayoutParams(layoutParams);
//            }
//        }
        initHomePageView();
        /*设置banner宽高比为16:9*/
        int height16_9 = ScreenUtils.getHeight16_9(((BaseActivity) mContext), 60);
        ViewGroup.LayoutParams layoutParams = banner.getLayoutParams();
        layoutParams.height = height16_9;
        banner.setLayoutParams(layoutParams);
        initBannerView();
        mHeaderAndFooterWrapper.addHeaderView(view);
        rv_list.setAdapter(mHeaderAndFooterWrapper);
        //关闭loading
        ((BaseActivity) mContext).dismissLoading();
    }

    /**
     * Description：首页视图初始化
     */
    private void initHomePageView() {
        OkHttpUtil okHttpUtil = new OkHttpUtil(new StringCallBack() {
            @Override
            public void onResponse(String response, int id) {
                boolean status = JSONUtil.getBoolean(response, "status", false);
                if (status) {
                    String data = JSONUtil.getString(response, "data", "");
                    String[] lists = JSONUtil.getStringArray(data, "list", null);
                    if (lists != null) {
                        recommendBeanList.clear();
                        for (String list : lists) {
                            String img = JSONUtil.getString(list, "img", "");
                            String url = JSONUtil.getString(list, "url", "");
                            recommendBeanList.add(new BannerBean(img, url));
                        }
                        bannerHandler.sendEmptyMessage(2);
                    } else {
                        LogUtils.e("首页今日推荐Banner图数据为空");
                    }
                } else {
                    LogUtils.e("获取首页Banner图接口advert.gettodayrecommendlist返回状态status为false");
                }
            }

            @Override
            public void onError(Call call, Exception e, int id) {

            }
        });
        okHttpUtil.requestCall(mContext, "advert.gettodayrecommendlist", null);
    }

    /**
     * 初始化Banner视图
     */
    private void initBannerView() {
        OkHttpUtil okHttpUtil = new OkHttpUtil(new StringCallBack() {
            @Override
            public void onResponse(String response, int id) {
                boolean status = JSONUtil.getBoolean(response, "status", false);
                if (status) {
                    String data = JSONUtil.getString(response, "data", "");
                    String[] lists = JSONUtil.getStringArray(data, "list", null);
                    if (lists != null) {
                        bannerBeanList.clear();
                        for (String list : lists) {
                            String img = JSONUtil.getString(list, "img", "");
                            String url = JSONUtil.getString(list, "url", "");
                            bannerBeanList.add(new BannerBean(img, url));
                        }
                        bannerHandler.sendEmptyMessage(1);
                    } else {
                        LogUtils.e("首页Banner图数据为空");
                    }
                } else {
                    LogUtils.e("获取首页Banner图接口advert.getAdvertList返回状态status为false");
                }
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                LogUtils.e("获取首页Banner图接口advert.getAdvertList请求失败");
            }
        });
        okHttpUtil.requestCall(mContext, "advert.getAdvertList", null);

    }

    private void applyBannerView() {
        banner.setAdapter(new BGABanner.Adapter<ImageView, BannerBean>() {
            @Override
            public void fillBannerItem(BGABanner banner, ImageView itemView, BannerBean model, int position) {
                if (model != null && !model.getImage().equals("")) {
                    Picasso.with(mContext)    //context
                            .load(model.getImage())     //图片加载地址
                            .transform(new RoundTransform(10))
                            .error(android.R.drawable.ic_menu_delete)   //图片记载失败时显示的页面
                            .fit()      //智能展示图片，对于图片的大小和imageview的尺寸进行了测量，计算出最佳的大小和最佳的质量显示出来
                            .into(itemView);    //需要加载图片的控件
                }
            }
        });
        banner.setData(bannerBeanList, Collections.singletonList(""));
        banner.setAutoPlayAble(bannerBeanList.size() > 1);
        banner.setDelegate(this);//Item点击事件
    }

    /**
     * Description：加载今日推荐Banner视图
     */
    private void applyRecommendBannerView() {
        banner_recommend.setAdapter(new BGABanner.Adapter<ImageView, BannerBean>() {
            @Override
            public void fillBannerItem(BGABanner banner, ImageView itemView, BannerBean model, int position) {
                if (model != null && !model.getImage().equals("")) {
                    Picasso.with(mContext)    //context
                            .load(model.getImage())     //图片加载地址
                            .transform(new RoundTransform(10))
                            .error(android.R.drawable.ic_menu_delete)   //图片记载失败时显示的页面
                            .fit()      //智能展示图片，对于图片的大小和imageview的尺寸进行了测量，计算出最佳的大小和最佳的质量显示出来
                            .into(itemView);    //需要加载图片的控件
                }
            }
        });
        banner_recommend.setData(recommendBeanList, Collections.singletonList(""));
        banner_recommend.setAutoPlayAble(recommendBeanList.size() > 1);
        banner_recommend.setDelegate(this);//Item点击事件
    }

    /**
     * 动态请求权限，安卓手机版本在5.0以上时需要
     */
    private void myPermissionRequest() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // 检查是否拥有权限，申请一个（或多个）权限，并提供用于回调返回的获取码（用户定义)
            if (mContext.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ((BaseActivity)mContext).requestPermissions(new String[] {
                        Manifest.permission.ACCESS_COARSE_LOCATION
                }, BAIDU_ACCESS_COARSE_LOCATION);
            }
            else {
                // 已拥有权限，作相应处理（调用定位SDK应当确保相关权限均被授权，否则可能引起定位失败）
                myLocation();
            }
        }else {
            // 安卓手机版本在5.0时，配置清单中已申明权限，作相应处理，此处正对sdk版本低于23的手机
            myLocation();
        }
    }

    /**
     * 百度地图定位的请求方法
     */
    private void myLocation() {
        BdLocationUtil.getInstance().requestLocation(new BdLocationUtil.MyLocationListener() {
            @Override
            public void myLocation(BDLocation location) {
                if (location == null) {
                    ((BaseActivity) mContext).dismissLoading();
                    return;
                }
                if (location.getLocType() == BDLocation.TypeNetWorkLocation) {
                    //获取维度信息
                    latitude = location.getLatitude();
                    //获取经度信息
                    longitude = location.getLongitude();
                    getNearGoods();
                }
            }
        });
    }

    /**
     * Description：Banner图Item点击事件
     */
    @Override
    public void onBannerItemClick(BGABanner banner, View itemView, @Nullable Object model, int position) {
        if (model != null) {
            Intent intent = new Intent(mContext, WebViewActivity.class);
            intent.putExtra("url", ((BannerBean) model).getUrl());
            intent.putExtra("isJingDong", false);
            mContext.startActivity(intent);
        }
    }

    @OnClick({R.id.tv_taobao, R.id.tv_jingdong, R.id.tv_xianxia, R.id.tv_mianfeiling, R.id.iv_more})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_taobao://淘宝领券
                if (mContext != null) {
                    ((MainActivity) mContext).onChecked(0);
                }
                break;
            case R.id.tv_jingdong://京东领券
                if (mContext != null) {
                    ((MainActivity) mContext).onChecked(1);
                }
                break;
            case R.id.tv_xianxia://附近领券
                if (mContext != null) {
                    ((MainActivity) mContext).onChecked(2);
                }
                break;
            case R.id.tv_mianfeiling://免费领
                if (mContext != null) {
                    RadioButton rb_entity = ((MainActivity) mContext).findViewById(R.id.rb_entity);
                    rb_entity.setChecked(true);
                }
                break;
            case R.id.iv_more://更多
                if (mContext != null) {
                    ((MainActivity) mContext).onChecked(2);
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            // requestCode即所声明的权限获取码，在checkSelfPermission时传入
            case BAIDU_ACCESS_COARSE_LOCATION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 第一次获取到权限，请求定位
                    myLocation();
                }
                else {
                    // 没有获取到权限，做特殊处理
                    ToastUtil.show(mContext, "请打开定位权限！无法获取附近优惠券");
                    if (mContext != null) {
                        ((BaseActivity) mContext).dismissLoading();
                    }
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onDestroy() {
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
        if (bannerHandler != null) {
            bannerHandler.removeCallbacksAndMessages(null);
        }
    }
}
