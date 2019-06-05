package com.ks.freecoupon.module.view.pc.presenter;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ks.basictools.base_interface.StringCallBack;
import com.ks.basictools.overView.CommonAdapter;
import com.ks.basictools.overView.LoadMoreWrapper;
import com.ks.basictools.utils.JSONUtil;
import com.ks.freecoupon.R;
import com.ks.freecoupon.module.bean.NearGoods;
import com.ks.freecoupon.module.contract.pc.DiscountCouponActivityContract;
import com.ks.freecoupon.module.view.pc.activity.DiscountCouponActivity;
import com.ks.freecoupon.module.view.pc.activity.ShowCouponCodeActivity;
import com.ks.freecoupon.utils.BaseUtils;
import com.ks.freecoupon.utils.OkHttpUtil;
import com.ks.freecoupon.utils.SharedPreferencesUtils;
import com.squareup.picasso.Picasso;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

/**
 * author：康少
 * date：2019/3/13
 * description：我的优惠券——逻辑层
 */
public class DiscountCouponActivityPresenter implements DiscountCouponActivityContract.IDiscountCouponActivityPresenter {
    private DiscountCouponActivity mDiscountCouponActivity;

    /*控件对象*/
    private RecyclerView rv_RecyclerView;
    private RelativeLayout rl_empty;
    private SwipeRefreshLayout srl_refresh;
    /*参数*/
    private List<NearGoods> discountCouponList;
    private CommonAdapter mAdapter;
    private LoadMoreWrapper mLoadMoreWrapper;
    private int currentPage = 1;

    private Handler mHandler;

    public DiscountCouponActivityPresenter(DiscountCouponActivity mDiscountCouponActivity) {
        this.mDiscountCouponActivity = mDiscountCouponActivity;
        mDiscountCouponActivity.setPresenter(this);
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
                        applyView();
                        break;
                }
            }
        };
        getHttpData();
    }

    private void initObject() {
        rv_RecyclerView = mDiscountCouponActivity.getRv_RecyclerView();
        rl_empty = mDiscountCouponActivity.getRl_empty();
        srl_refresh = mDiscountCouponActivity.getSrl_refresh();

        srl_refresh.setColorSchemeResources(R.color.colorPrimary);
        srl_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getHttpData();
                srl_refresh.setRefreshing(false);//刷新结束，隐藏刷新进度条
            }
        });

        discountCouponList = new ArrayList<>();
        rv_RecyclerView.setLayoutManager(new GridLayoutManager(mDiscountCouponActivity, 1));
    }

    /**
     * Description：是否展示空界面
     */
    private void toggle(boolean isEmpty) {
        if (isEmpty) {
            rl_empty.setVisibility(View.VISIBLE);
            rv_RecyclerView.setVisibility(View.GONE);
        } else {
            rl_empty.setVisibility(View.GONE);
            rv_RecyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void getHttpData() {
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        map.put("token", SharedPreferencesUtils.getParam(mDiscountCouponActivity, "token", ""));
        map.put("type", "no_used");
        list.add(map);
        OkHttpUtil okHttpUtil = new OkHttpUtil(new StringCallBack() {
            @Override
            public void onResponse(String response, int id) {
                boolean status = JSONUtil.getBoolean(response, "status", false);
                if (status) {
                    String[] data = JSONUtil.getStringArray(response, "data", null);
                    if (data != null) {
                        toggle(false);
                        discountCouponList.clear();
                        for (String aData : data) {
                            NearGoods nearGoods = new NearGoods(JSONUtil.getString(aData, "coupon_code", "") + "",
                                    JSONUtil.getString(aData, "name", "") + "",
                                    JSONUtil.getString(aData, "img", "") + "",
                                    JSONUtil.getString(aData, "mkt_price", "") + "",
                                    JSONUtil.getString(aData, "address", "") + "",
                                    JSONUtil.getString(aData, "coupon_content", "") + "",
                                    JSONUtil.getString(aData, "stime", "") + "",
                                    JSONUtil.getString(aData, "etime", "") + "");
                            discountCouponList.add(nearGoods);
                        }
                        mHandler.sendEmptyMessage(1);
                    } else {
                        toggle(true);
                    }
                }
            }

            @Override
            public void onError(Call call, Exception e, int id) {

            }
        });
        okHttpUtil.requestCall(mDiscountCouponActivity, "user.couponlist", list);
    }

    private void applyView() {
        mAdapter = new CommonAdapter<NearGoods>(mDiscountCouponActivity, R.layout.item_discount_coupon, discountCouponList) {
            @SuppressLint("SetTextI18n")
            @Override
            protected void convert(ViewHolder holder, final NearGoods discountCoupon, int position) {
                if (!discountCoupon.getImg().equals("")) {
                    Picasso.with(mContext)    //context
                            .load(discountCoupon.getImg())     //图片加载地址
                            .error(android.R.drawable.ic_menu_delete)   //图片记载失败时显示的页面
                            .fit()      //智能展示图片，对于图片的大小和imageview的尺寸进行了测量，计算出最佳的大小和最佳的质量显示出来
                            .into((ImageView) holder.getView(R.id.iv_image));    //需要加载图片的控件
                }
                ((TextView) holder.getView(R.id.tv_price)).setText(discountCoupon.getCoupon_content());
                ((TextView) holder.getView(R.id.tv_name)).setText(discountCoupon.getName());
                ((TextView) holder.getView(R.id.tv_eTime)).setText(discountCoupon.getEtime());
                ((TextView) holder.getView(R.id.tv_desc)).setText("满" + discountCoupon.getMkt_price() + "元可用");
                //去使用——点击事件
                holder.getView(R.id.tv_use).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // 跳转二维码界面
                        Intent intent = new Intent(mContext, ShowCouponCodeActivity.class);
                        intent.putExtra("discountCoupon", discountCoupon);
                        mContext.startActivity(intent);
                    }
                });
                BaseUtils.setShadowDrawable(holder.getView(R.id.rl_container), Color.parseColor("#ffffff"), 5,
                        Color.parseColor("#22000000"), 8, 0, 0);
            }
        };
        // 加载更多
        loadMore();
    }

    private void loadMore() {
        mLoadMoreWrapper = new LoadMoreWrapper(mDiscountCouponActivity, mAdapter);
        mLoadMoreWrapper.setLoadMoreView(LayoutInflater.from(mDiscountCouponActivity).inflate(R.layout.loading, rv_RecyclerView, false));
        mLoadMoreWrapper.setOnLoadMoreListener(new LoadMoreWrapper.OnLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                /*获取商品列表*/
                updateRecyclerView();
            }
        });
        rv_RecyclerView.setAdapter(mLoadMoreWrapper);
    }

    /**
     * Description：加载更多
     */
    private void updateRecyclerView() {
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        map.put("token", SharedPreferencesUtils.getParam(mDiscountCouponActivity, "token", ""));
        map.put("type", "no_used");
        map.put("page_no", ++currentPage);
        list.add(map);
        OkHttpUtil okHttpUtil = new OkHttpUtil(new StringCallBack() {
            @Override
            public void onResponse(String response, int id) {
                boolean status = JSONUtil.getBoolean(response, "status", false);
                if (status) {
                    String[] data = JSONUtil.getStringArray(response, "data", null);
                    if (data != null) {
                        for (String aData : data) {
                            NearGoods nearGoods = new NearGoods(JSONUtil.getString(aData, "coupon_code", "") + "",
                                    JSONUtil.getString(aData, "name", "") + "",
                                    JSONUtil.getString(aData, "img", "") + "",
                                    JSONUtil.getString(aData, "mkt_price", "") + "",
                                    JSONUtil.getString(aData, "address", "") + "",
                                    JSONUtil.getString(aData, "coupon_content", "") + "",
                                    JSONUtil.getString(aData, "stime", "") + "",
                                    JSONUtil.getString(aData, "etime", "") + "");
                            discountCouponList.add(nearGoods);
                        }
                        if (data.length > 0) {
                            mLoadMoreWrapper.notifyDataSetChanged();
                        } else {
                            mLoadMoreWrapper.showLoadComplete();
                            currentPage--;
                        }
                    } else {
                        if (mLoadMoreWrapper != null) {
                            mLoadMoreWrapper.showLoadComplete();//加载完成视图
                            currentPage--;
                        }
                    }
                }
            }
            @Override
            public void onError(Call call, Exception e, int id) {
                if (mLoadMoreWrapper != null) {
                    mLoadMoreWrapper.showLoadComplete();//加载完成视图
                    currentPage--;
                }
            }
        });
        okHttpUtil.requestCall(mDiscountCouponActivity, "user.couponlist", list);
    }

    @Override
    public void onDestroy() {
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
    }
}
