package com.ks.freecoupon.module.view.pc.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ks.basictools.base_interface.StringCallBack;
import com.ks.basictools.overView.LoadMoreWrapper;
import com.ks.basictools.publicView.TitleVarView;
import com.ks.basictools.utils.JSONUtil;
import com.ks.basictools.utils.LogUtils;
import com.ks.basictools.utils.ToastUtil;
import com.ks.freecoupon.R;
import com.ks.freecoupon.module.bean.RecordBean;
import com.ks.freecoupon.module.bean.ScoreGoods;
import com.ks.freecoupon.override.ToastKs;
import com.ks.freecoupon.utils.BaseUtils;
import com.ks.freecoupon.utils.OkHttpUtil;
import com.ks.freecoupon.utils.SharedPreferencesUtils;
import com.squareup.picasso.Picasso;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

/**
 * Date:2019/5/9
 * Author：康少
 * Description：抽奖记录
 */
public class ChouJiangRecordActivity extends TitleVarView {
    private Context mContext;

    private RecyclerView mList;
    private RelativeLayout rl_empty;
    private LinearLayout ll_title;
    private TextView tvTip;

    private LoadMoreWrapper mLoadMoreWrapper;
    private CommonAdapter<RecordBean> mAdapter;
    /*其他参数*/
    private Handler mHandler;
    private List<RecordBean> recordBeanList;
    private int page = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chou_jiang_record);
        mContext = this;
        setTitleBar(true, "获奖记录", "");
        findId();
        setTypeface();//设置字体样式
        start();
    }

    private void findId() {
        mList = findViewById(R.id.rv_list);
        rl_empty = findViewById(R.id.rl_empty);
        ll_title = findViewById(R.id.ll_title);
        tvTip = findViewById(R.id.tv_tip);

        mList.setLayoutManager(new GridLayoutManager(mContext, 1));

        recordBeanList = new ArrayList<>();
    }

    /**
     * Description：设置字体样式
     */
    private void setTypeface() {
        //设置字体
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fangzheng.ttf");
        tvTip.setTypeface(typeface);
    }

    @SuppressLint("HandlerLeak")
    private void start() {
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
     * Description：是否展示空界面
     */
    private void toggle(boolean isEmpty) {
        if (isEmpty) {
            rl_empty.setVisibility(View.VISIBLE);
            mList.setVisibility(View.GONE);
            ll_title.setVisibility(View.GONE);
        } else {
            rl_empty.setVisibility(View.GONE);
            mList.setVisibility(View.VISIBLE);
            ll_title.setVisibility(View.VISIBLE);
        }
    }

    private void getHttpData() {
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        map.put("token", SharedPreferencesUtils.getParam(mContext, "token", ""));
        list.add(map);
        OkHttpUtil okHttpUtil = new OkHttpUtil(new StringCallBack() {
            @Override
            public void onResponse(String response, int id) {
                boolean status = JSONUtil.getBoolean(response, "status", false);
                if (status) {
                    String[] dataList = JSONUtil.getStringArray(response, "data", null);
                    if (dataList != null && dataList.length != 0) {
                        toggle(false);
                        recordBeanList.clear();
                        for (String data : dataList) {
                            recordBeanList.add(new RecordBean(
                                    "" + JSONUtil.getString(data, "ctime", "")
                                    , "" + JSONUtil.getString(data, "gift_name", "")
                            ));
                        }
                        mHandler.sendEmptyMessage(1);
                    } else {
                        toggle(true);
                    }
                } else {
                    ToastUtil.show(mContext, JSONUtil.getString(response, "msg", ""));
                }
            }
            @Override
            public void onError(Call call, Exception e, int id) {
                LogUtils.e(e.getMessage());
            }
        });
        okHttpUtil.requestCall(mContext, "user.getlotterygiftlogs", list);
    }

    private void applyView() {
        mAdapter = new CommonAdapter<RecordBean>(mContext, R.layout.item_record, recordBeanList) {
            @SuppressLint("SetTextI18n")
            @Override
            protected void convert(ViewHolder holder, final RecordBean recordBean, final int position) {
                ((TextView) holder.getView(R.id.tv_ctime)).setText(recordBean.getCtime());
                ((TextView) holder.getView(R.id.gift_name)).setText(recordBean.getGift_name());
            }
        };
        // 加载更多
        loadMore();
    }

    private void loadMore() {
        mLoadMoreWrapper = new LoadMoreWrapper(mContext, mAdapter);
        mLoadMoreWrapper.setLoadMoreView(LayoutInflater.from(mContext).inflate(R.layout.loading, mList, false));
        mLoadMoreWrapper.setOnLoadMoreListener(new LoadMoreWrapper.OnLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                /*获取商品列表*/
                updateRecyclerView();
            }
        });
        mList.setAdapter(mLoadMoreWrapper);
    }
    /**
     * Description：加载更多
     */
    private void updateRecyclerView() {
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        map.put("token", SharedPreferencesUtils.getParam(mContext, "token", ""));
        map.put("page_no", ++page);
        list.add(map);
        OkHttpUtil okHttpUtil = new OkHttpUtil(new StringCallBack() {
            @Override
            public void onResponse(String response, int id) {
                boolean status = JSONUtil.getBoolean(response, "status", false);
                if (status) {
                    String[] dataList = JSONUtil.getStringArray(response, "data", null);
                    if (dataList != null) {
                        for (String data : dataList) {
                            recordBeanList.add(new RecordBean(
                                    "" + JSONUtil.getString(data, "ctime", "")
                                    , "" + JSONUtil.getString(data, "gift_name", "")
                            ));
                        }
                        if (dataList.length > 0) {
                            mLoadMoreWrapper.notifyDataSetChanged();
                        } else {
                            mLoadMoreWrapper.showLoadComplete();
                            page--;
                        }
                    } else {
                        if (mLoadMoreWrapper != null) {
                            mLoadMoreWrapper.showLoadComplete();//加载完成视图
                            page--;
                        }
                    }
                }
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                if (mLoadMoreWrapper != null) {
                    mLoadMoreWrapper.showLoadComplete();//加载完成视图
                    page--;
                }
            }
        });
        okHttpUtil.requestCall(mContext, "user.getlotterygiftlogs", list);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
    }
}
