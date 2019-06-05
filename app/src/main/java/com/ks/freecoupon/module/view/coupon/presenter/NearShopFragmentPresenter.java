package com.ks.freecoupon.module.view.coupon.presenter;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.ks.basictools.base_interface.StringCallBack;
import com.ks.basictools.overView.CommonAdapter;
import com.ks.basictools.overView.LoadMoreWrapper;
import com.ks.basictools.utils.JSONUtil;
import com.ks.basictools.utils.LogUtils;
import com.ks.basictools.utils.ToastUtil;
import com.ks.freecoupon.BaseActivity;
import com.ks.freecoupon.R;
import com.ks.freecoupon.module.bean.NearGoods;
import com.ks.freecoupon.module.contract.coupon.NearShopFragmentContract;
import com.ks.freecoupon.module.view.MainActivity;
import com.ks.freecoupon.module.view.coupon.activity.GetNearCouponActivity;
import com.ks.freecoupon.module.view.coupon.fragment.NearShopFragment;
import com.ks.freecoupon.module.view.search.activity.SearchResultActivity;
import com.ks.freecoupon.override.RoundTransform;
import com.ks.freecoupon.utils.BaseUtils;
import com.ks.freecoupon.utils.BdLocationUtil;
import com.ks.freecoupon.utils.OkHttpUtil;
import com.ks.freecoupon.utils.SharedPreferencesUtils;
import com.squareup.picasso.Picasso;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

/**
 * author：康少
 * date：2019/3/7
 * description：附近店铺——逻辑层
 */
public class NearShopFragmentPresenter implements NearShopFragmentContract.INearShopFragmentPresenter {
    private NearShopFragment mNearShopFragment;
    private Context mContext;

    /*控件对象*/
    private RecyclerView rvList;
    private RelativeLayout rlEmpty;
    private SwipeRefreshLayout srl_refresh;
    /*商品列表*/
    private List<NearGoods> nearGoodsList;
    /*参数*/
    private String keyWord = "";//搜索关键词
    private Handler mHandler;
    private CommonAdapter mAdapter;
    private LoadMoreWrapper mLoadMoreWrapper;
    private int currentPage = 1;//当前页数
    /*定位*/
    private final int RESULT_REQUEST_PERMISSION = 0x0001;
    private final int BAIDU_ACCESS_COARSE_LOCATION = 0x0002;
    private double longitude = 0;//经度
    private double latitude = 0;//纬度

    public NearShopFragmentPresenter(NearShopFragment mNearShopFragment) {
        this.mNearShopFragment = mNearShopFragment;
        mNearShopFragment.setPresenter(this);
    }

    @SuppressLint("HandlerLeak")
    @Override
    public void start() {
        initObject();
        getLocation();//获取经纬度
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 1:
                        if (nearGoodsList.size() == 0) {
                            showEmpty();
                        } else {
                            applyView();
                        }
                        break;
                }
            }
        };
    }

    private void initObject() {
        mContext = mNearShopFragment.getmContext();
        rlEmpty = mNearShopFragment.getRlEmpty();
        srl_refresh = mNearShopFragment.getSrl_refresh();
        rvList = mNearShopFragment.getRvList();
        if (mContext instanceof SearchResultActivity) {
            keyWord = ((SearchResultActivity) mContext).getKeyWord();
        }
        rvList.setLayoutManager(new GridLayoutManager(mContext, 1));

        nearGoodsList = new ArrayList<>();

        srl_refresh.setColorSchemeResources(R.color.colorPrimary);
        srl_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                nearGoodsList.clear();
                currentPage = 1;
                getHttpData();
                srl_refresh.setRefreshing(false);//刷新结束，隐藏刷新进度条
            }
        });
    }

    /**
     * Description：展示空数据界面
     */
    private void showEmpty() {
        rlEmpty.setVisibility(View.VISIBLE);
        rvList.setVisibility(View.GONE);
        //关闭loading
        if (mContext != null) {
            ((BaseActivity) mContext).dismissLoading();
        }
    }

    /**
     * Description：展示有数据界面
     */
    private void applyView() {
        rlEmpty.setVisibility(View.GONE);
        rvList.setVisibility(View.VISIBLE);
        mAdapter = new CommonAdapter<NearGoods>(mContext, R.layout.item_nearshop_goods, nearGoodsList) {
            @SuppressLint("SetTextI18n")
            @Override
            protected void convert(ViewHolder holder, final NearGoods nearGoods, int position) {
                if (!nearGoods.getImg().equals("")) {
                    Picasso.with(mContext)    //context
                            .load(nearGoods.getImg())     //图片加载地址
                            .transform(new RoundTransform(10))
                            .error(android.R.drawable.ic_menu_delete)   //图片记载失败时显示的页面
                            .fit()      //智能展示图片，对于图片的大小和imageview的尺寸进行了测量，计算出最佳的大小和最佳的质量显示出来
                            .into((ImageView) holder.getView(R.id.image_item));    //需要加载图片的控件
                }
                ((TextView) holder.getView(R.id.tv_name)).setText(nearGoods.getName());
                ((TextView) holder.getView(R.id.tv_juan)).setText(nearGoods.getCoupon_content());
                ((TextView) holder.getView(R.id.tv_condition)).setText("满" + nearGoods.getMkt_price() + "元可用");
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolean isLogin = (boolean) SharedPreferencesUtils.getParam(mContext, "isLogin", false);
                        if (isLogin) {
                            // 跳转附近领券界面
                            Intent intent = new Intent(mContext, GetNearCouponActivity.class);
                            intent.putExtra("nearGoods", nearGoods);
                            if (mContext != null) {
                                mContext.startActivity(intent);
                            }
                        } else {
                            BaseUtils.promptLogin(mContext);
                        }
                    }
                });
            }
        };
        // 加载更多
        loadMore();
        //关闭loading
        if (mContext != null) {
            ((BaseActivity) mContext).dismissLoading();
        }
    }

    /**
     * Description：获取附近优惠券列表
     */
    private void getHttpData() {
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        map.put("page_no", currentPage);
        map.put("keyword", keyWord);
        map.put("lng", longitude);
        map.put("lat", latitude);
        list.add(map);
        OkHttpUtil okHttpUtil = new OkHttpUtil(new StringCallBack() {
            @Override
            public void onResponse(String response, int id) {
                boolean status = JSONUtil.getBoolean(response, "status", false);
                if (status) {
                    String[] dataList = JSONUtil.getStringArray(response, "data", null);
                    if (dataList != null) {
                        nearGoodsList.clear();
                        for (String data : dataList) {
                            NearGoods nearGoods = new NearGoods(JSONUtil.getInt(data, "id", -1),
                                    JSONUtil.getString(data, "name", "") + "",
                                    JSONUtil.getString(data, "img", "") + "",
                                    JSONUtil.getString(data, "cost_price", "") + "",
                                    JSONUtil.getString(data, "mkt_price", "") + "",
                                    JSONUtil.getString(data, "address", "") + "",
                                    JSONUtil.getString(data, "tel", "") + "",
                                    JSONUtil.getString(data, "coupon_content", "") + "",
                                    JSONUtil.getString(data, "stime", "") + "",
                                    JSONUtil.getString(data, "etime", "") + "",
                                    JSONUtil.getString(data, "lng", "") + "",
                                    JSONUtil.getString(data, "lat", "") + "");
                            nearGoodsList.add(nearGoods);
                        }
                        mHandler.sendEmptyMessage(1);
                    } else {
                        if (mContext != null) {
                            ((BaseActivity) mContext).dismissLoading();
                        }
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

    private void loadMore() {
        mLoadMoreWrapper = new LoadMoreWrapper(mContext, mAdapter);
        mLoadMoreWrapper.setLoadMoreView(LayoutInflater.from(mContext).inflate(R.layout.loading, rvList, false));
        mLoadMoreWrapper.setOnLoadMoreListener(new LoadMoreWrapper.OnLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                /*获取商品列表*/
                updateRecyclerView();
            }
        });
        rvList.setAdapter(mLoadMoreWrapper);
    }

    /**
     * Description：加载更多
     */
    private void updateRecyclerView() {
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        map.put("page_no", ++currentPage);
        map.put("keyword", keyWord);
        map.put("lng", longitude);
        map.put("lat", latitude);
        list.add(map);
        OkHttpUtil okHttpUtil = new OkHttpUtil(new StringCallBack() {
            @Override
            public void onResponse(String response, int id) {
                boolean status = JSONUtil.getBoolean(response, "status", false);
                if (status) {
                    String[] dataList = JSONUtil.getStringArray(response, "data", null);
                    if (dataList != null) {
                        for (String data : dataList) {
                            NearGoods nearGoods = new NearGoods(JSONUtil.getInt(data, "id", -1),
                                    JSONUtil.getString(data, "name", "") + "",
                                    JSONUtil.getString(data, "img", "") + "",
                                    JSONUtil.getString(data, "cost_price", "") + "",
                                    JSONUtil.getString(data, "mkt_price", "") + "",
                                    JSONUtil.getString(data, "address", "") + "",
                                    JSONUtil.getString(data, "tel", "") + "",
                                    JSONUtil.getString(data, "coupon_content", "") + "",
                                    JSONUtil.getString(data, "stime", "") + "",
                                    JSONUtil.getString(data, "etime", "") + "",
                                    JSONUtil.getString(data, "lng", "") + "",
                                    JSONUtil.getString(data, "lat", "") + "");
                            nearGoodsList.add(nearGoods);
                        }
                        if (dataList.length > 0) {
                            mLoadMoreWrapper.notifyDataSetChanged();
                        } else {
                            mLoadMoreWrapper.showLoadComplete();
                            currentPage--;
                        }
                    } else {
                        mLoadMoreWrapper.showLoadComplete();
                        currentPage--;
                    }
                } else {
                    LogUtils.e("附近领券 response：" + response);
                    String msg = JSONUtil.getString(response, "msg", "");
                    LogUtils.e(msg);
                    mLoadMoreWrapper.showLoadComplete();
                    currentPage--;
                }
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                mLoadMoreWrapper.showLoadComplete();
                currentPage--;
            }
        });
        okHttpUtil.requestCall(mContext, "coupon.getplatformlist", list);
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
                    getHttpData();
                }
            }
        });
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
    }
}
