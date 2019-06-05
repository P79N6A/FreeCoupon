package com.ks.freecoupon.module.view.pc.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.ks.basictools.base_interface.StringCallBack;
import com.ks.basictools.overView.CommonAdapter;
import com.ks.basictools.overView.LoadMoreWrapper;
import com.ks.basictools.publicView.TitleVarView;
import com.ks.basictools.utils.JSONUtil;
import com.ks.freecoupon.R;
import com.ks.freecoupon.module.bean.ScoreChangeRecord;
import com.ks.freecoupon.utils.BaseUtils;
import com.ks.freecoupon.utils.OkHttpUtil;
import com.ks.freecoupon.utils.SharedPreferencesUtils;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

public class ScoreRecordActivity extends TitleVarView {
    private Context mContext;
    private RecyclerView rvList;
    /*参数*/
    private List<ScoreChangeRecord> scoreChangeRecordList;
    private CommonAdapter mAdapter;
    private LoadMoreWrapper mLoadMoreWrapper;
    private int currentPage = 1;

    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score_record);
        mContext = this;
        setTitleBar(true, "兑换记录", "");
        rvList = findViewById(R.id.rv_list);
        scoreChangeRecordList = new ArrayList<>();
        rvList.setLayoutManager(new GridLayoutManager(mContext, 1));
        start();
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

    private void getHttpData() {
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        map.put("token", SharedPreferencesUtils.getParam(mContext, "token", ""));
        map.put("type", "no_used");
        list.add(map);
        OkHttpUtil okHttpUtil = new OkHttpUtil(new StringCallBack() {
            @Override
            public void onResponse(String response, int id) {
                boolean status = JSONUtil.getBoolean(response, "status", false);
                if (status) {
                    String[] data = JSONUtil.getStringArray(response, "data", null);
                    if (data != null) {
                        scoreChangeRecordList.clear();
                        for (String aData : data) {
                            int is_physical = JSONUtil.getInt(aData, "is_physical", -1);
                            ScoreChangeRecord scoreChangeRecord;
                            switch (is_physical) {
                                case 1://虚物
                                    scoreChangeRecord = new ScoreChangeRecord(
                                            "" + JSONUtil.getString(aData, "order_id", "")
                                            , "" + JSONUtil.getString(aData, "name", "")
                                            , "" + BaseUtils.timeStamp2Date("" + JSONUtil.getInt(aData, "ctime", 0), null)
                                            , "" + JSONUtil.getInt(aData, "point", 0)
                                            , "" + JSONUtil.getString(aData, "mark", "")
                                            , "" + JSONUtil.getInt(aData, "is_physical", -1)
                                    );
                                    scoreChangeRecordList.add(scoreChangeRecord);
                                    break;
                                case 0://实物
                                    scoreChangeRecord = new ScoreChangeRecord(
                                            "" + JSONUtil.getString(aData, "order_id", "")
                                            , "" + JSONUtil.getString(aData, "name", "")
                                            , "" + BaseUtils.timeStamp2Date("" + JSONUtil.getInt(aData, "ctime", 0), null)
                                            , "" + JSONUtil.getInt(aData, "point", 0)
                                            , "" + JSONUtil.getInt(aData, "is_physical", -1)
                                            , "" + JSONUtil.getString(aData, "delivery_code", "")
                                            , "" + JSONUtil.getString(aData, "delivery_name", "")
                                    );
                                    scoreChangeRecordList.add(scoreChangeRecord);
                                    break;
                            }
                        }
                        mHandler.sendEmptyMessage(1);
                    }
                }
            }

            @Override
            public void onError(Call call, Exception e, int id) {

            }
        });
        okHttpUtil.requestCall(mContext, "user.scorerecordlist", list);
    }

    private void applyView() {
        mAdapter = new CommonAdapter<ScoreChangeRecord>(mContext, R.layout.item_change_record, scoreChangeRecordList) {
            @SuppressLint("SetTextI18n")
            @Override
            protected void convert(ViewHolder holder, final ScoreChangeRecord scoreChangeRecord, int position) {
                toggle(scoreChangeRecord.getIs_physical().equals("0"), holder);
                ((TextView) holder.getView(R.id.tv_order_id)).setText("单号：" + scoreChangeRecord.getOrder_id());
                ((TextView) holder.getView(R.id.tv_name)).setText(scoreChangeRecord.getName());
                ((TextView) holder.getView(R.id.tv_ctime)).setText("兑换时间：" + scoreChangeRecord.getCtime());
                ((TextView) holder.getView(R.id.tv_mark)).setText("备注：" + scoreChangeRecord.getMark());
                ((TextView) holder.getView(R.id.tv_logistics_name)).setText("物流名称：" + scoreChangeRecord.getDelivery_name());
                ((TextView) holder.getView(R.id.tv_logistics_num)).setText("物流单号:" + scoreChangeRecord.getDelivery_code());
                ((TextView) holder.getView(R.id.tv_price)).setText("﹣" + scoreChangeRecord.getPoint());
            }
        };
        // 加载更多
        loadMore();
    }

    /**
     * Description：切换实物虚物
     *
     * @param isEntity true：实物；false：虚物
     * @param holder   视图对象
     */
    private void toggle(boolean isEntity, ViewHolder holder) {
        if (isEntity) {
            holder.getView(R.id.tv_mark).setVisibility(View.GONE);
            holder.getView(R.id.ll_logistics).setVisibility(View.VISIBLE);
        } else {
            holder.getView(R.id.tv_mark).setVisibility(View.VISIBLE);
            holder.getView(R.id.ll_logistics).setVisibility(View.GONE);
        }
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
        map.put("token", SharedPreferencesUtils.getParam(mContext, "token", ""));
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
                            int is_physical = JSONUtil.getInt(aData, "is_physical", -1);
                            ScoreChangeRecord scoreChangeRecord;
                            switch (is_physical) {
                                case 1://虚物
                                    scoreChangeRecord = new ScoreChangeRecord(
                                            "" + JSONUtil.getString(aData, "order_id", "")
                                            , "" + JSONUtil.getString(aData, "name", "")
                                            , "" + BaseUtils.timeStamp2Date("" + JSONUtil.getInt(aData, "ctime", 0), null)
                                            , "" + JSONUtil.getInt(aData, "point", 0)
                                            , "" + JSONUtil.getString(aData, "mark", "")
                                            , "" + JSONUtil.getInt(aData, "is_physical", -1)
                                    );
                                    scoreChangeRecordList.add(scoreChangeRecord);
                                    break;
                                case 0://实物
                                    scoreChangeRecord = new ScoreChangeRecord(
                                            "" + JSONUtil.getString(aData, "order_id", "")
                                            , "" + JSONUtil.getString(aData, "name", "")
                                            , "" + BaseUtils.timeStamp2Date("" + JSONUtil.getInt(aData, "ctime", 0), null)
                                            , "" + JSONUtil.getInt(aData, "point", 0)
                                            , "" + JSONUtil.getInt(aData, "is_physical", -1)
                                            , "" + JSONUtil.getString(aData, "delivery_code", "")
                                            , "" + JSONUtil.getString(aData, "delivery_name", "")
                                    );
                                    scoreChangeRecordList.add(scoreChangeRecord);
                                    break;
                            }
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
        okHttpUtil.requestCall(mContext, "user.scorerecordlist", list);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
    }
}
