package com.ks.freecoupon.module.view.pc.presenter;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.XmlResourceParser;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ks.basictools.base_interface.StringCallBack;
import com.ks.basictools.overView.LoadMoreWrapper;
import com.ks.basictools.utils.JSONUtil;
import com.ks.basictools.utils.LogUtils;
import com.ks.basictools.utils.ToastUtil;
import com.ks.freecoupon.R;
import com.ks.freecoupon.module.bean.ScoreGoods;
import com.ks.freecoupon.module.contract.pc.ScoreShopActivityContract;
import com.ks.freecoupon.module.view.pc.activity.ChouJiangActivity;
import com.ks.freecoupon.module.view.pc.activity.ScoreGoodsDetailActivity;
import com.ks.freecoupon.module.view.pc.activity.ScoreRecordActivity;
import com.ks.freecoupon.module.view.pc.activity.ScoreShopActivity;
import com.ks.freecoupon.override.RoundTransform;
import com.ks.freecoupon.override.ToastKs;
import com.ks.freecoupon.utils.OkHttpUtil;
import com.ks.freecoupon.utils.ScreenUtils;
import com.ks.freecoupon.utils.SharedPreferencesUtils;
import com.squareup.picasso.Picasso;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;
import com.zhy.adapter.recyclerview.wrapper.HeaderAndFooterWrapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

/**
 * author：康少
 * date：2019/4/3
 * description：积分商城——逻辑层
 */
public class ScoreShopActivityPresenter implements ScoreShopActivityContract.IScoreShopActivityPresenter {
    private ScoreShopActivity mScoreShopActivity;
    /*控件对象*/
    private RecyclerView rv_list;
    /*RecycleView头部和加载更多*/
    private HeaderAndFooterWrapper mHeaderAndFooterWrapper;
    private LoadMoreWrapper mLoadMoreWrapper;
    private CommonAdapter<ScoreGoods> mAdapter;
    /*其他参数*/
    private Handler handler;
    private List<ScoreGoods> scoreGoodsList;
    private int page = 1;

    public ScoreShopActivityPresenter(ScoreShopActivity mScoreShopActivity) {
        this.mScoreShopActivity = mScoreShopActivity;
        mScoreShopActivity.setPresenter(this);
    }

    @SuppressLint("HandlerLeak")
    @Override
    public void start() {
        initObject();
        /*handler通知*/
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 1:
                        applyView();
                        break;
                }
            }
        };
        /*获取商品列表*/
        getGoodsList();
    }

    private void initObject() {
        rv_list = mScoreShopActivity.getRv_list();
        rv_list.setLayoutManager(new GridLayoutManager(mScoreShopActivity, 2));

        scoreGoodsList = new ArrayList<>();
    }

    private void getGoodsList() {
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        map.put("token", SharedPreferencesUtils.getParam(mScoreShopActivity, "token", ""));
        list.add(map);
        OkHttpUtil okHttpUtil = new OkHttpUtil(new StringCallBack() {
            @Override
            public void onResponse(String response, int id) {
                boolean status = JSONUtil.getBoolean(response, "status", false);
                if (!status) {
                    ToastUtil.show(mScoreShopActivity, JSONUtil.getString(response, "msg", ""));
                } else {
                    String[] dataList = JSONUtil.getStringArray(response, "data", null);
                    if (dataList != null) {
                        scoreGoodsList.clear();
                        for (String data : dataList) {
                            scoreGoodsList.add(new ScoreGoods(
                                    "" + JSONUtil.getInt(data, "id", 0)
                                    , "" + JSONUtil.getString(data, "img", "")
                                    , "" + JSONUtil.getString(data, "name", "")
                                    , "" + JSONUtil.getInt(data, "score", 0)
                                    , "" + JSONUtil.getInt(data, "stock", 0)
                                    , "" + JSONUtil.getInt(data, "is_physical", -1)
                            ));
                        }
                    } else {
                        ToastKs.show(mScoreShopActivity, "暂无积分商品可兑换");
                    }
                }
                handler.sendEmptyMessage(1);
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                LogUtils.e(e.getMessage());
            }
        });
        okHttpUtil.requestCall(mScoreShopActivity, "goods.getscoregoods", list);
    }

    /**
     * Description：加载视图
     */
    private void applyView() {
        mAdapter = new CommonAdapter<ScoreGoods>(mScoreShopActivity, R.layout.item_score_goods, scoreGoodsList) {
            @Override
            protected void convert(final ViewHolder holder, final ScoreGoods scoreGoods, int position) {
                /*设置图片宽高比为16:9*/
                int height16_9 = ScreenUtils.getHeight16_9(mScoreShopActivity,20);
                ViewGroup.LayoutParams layoutParams = holder.getView(R.id.goods_image).getLayoutParams();
                layoutParams.height = height16_9 * 16 / 18;
                holder.getView(R.id.goods_image).setLayoutParams(layoutParams);
                if (!scoreGoods.getGoodsImage().equals("")) {
                    Picasso.with(mContext)
                            .load(scoreGoods.getGoodsImage())     //图片加载地址
                            .transform(new RoundTransform(0))
                            .error(android.R.drawable.ic_menu_delete)   //图片记载失败时显示的页面
                            .fit()      //智能展示图片，对于图片的大小和imageview的尺寸进行了测量，计算出最佳的大小和最佳的质量显示出来
                            .into((ImageView) holder.getView(R.id.goods_image));    //需要加载图片的控件
                }
                ((TextView) holder.getView(R.id.goods_desc)).setText(scoreGoods.getGoodsDesc());
                ((TextView) holder.getView(R.id.tv_integral_price)).setText(scoreGoods.getScorePrice());
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // 跳转详情页
                        Intent intent = new Intent(mContext, ScoreGoodsDetailActivity.class);
                        intent.putExtra("goods_id", scoreGoods.getGoodsId());
                        mScoreShopActivity.startActivity(intent);
                    }
                });
            }
        };
        /*初始化头部*/
        initHeaderAndFooter();
        if (scoreGoodsList.size() > 0) {
            /*加载更多*/
            mLoadMoreWrapper = new LoadMoreWrapper(mScoreShopActivity, mHeaderAndFooterWrapper);
            mLoadMoreWrapper.setLoadMoreView(LayoutInflater.from(mScoreShopActivity).inflate(R.layout.loading, rv_list, false));
            mLoadMoreWrapper.setOnLoadMoreListener(new LoadMoreWrapper.OnLoadMoreListener() {
                @Override
                public void onLoadMoreRequested() {
                    /*获取商品列表*/
                    updateRecyclerView();
                }
            });
            rv_list.setAdapter(mLoadMoreWrapper);
        } else {
            rv_list.setAdapter(mHeaderAndFooterWrapper);
        }
    }

    /**
     * 初始化头部和底部
     */
    private void initHeaderAndFooter() {
        mHeaderAndFooterWrapper = new HeaderAndFooterWrapper(mAdapter);
        View header = LayoutInflater.from(mScoreShopActivity).inflate(R.layout.activity_score_shop_header, rv_list, false);
        ButterKnife.bind(this, header);
        mHeaderAndFooterWrapper.addHeaderView(header);
    }

    /**
     * http接口——上拉加载时调用的更新RecyclerView的方法
     */
    private void updateRecyclerView() {
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        map.put("token", SharedPreferencesUtils.getParam(mScoreShopActivity, "token", ""));
        map.put("page_no", ++page);
        list.add(map);
        OkHttpUtil okHttpUtil = new OkHttpUtil(new StringCallBack() {
            @Override
            public void onResponse(String response, int id) {
                boolean status = JSONUtil.getBoolean(response, "status", false);
                if (!status) {
                    ToastUtil.show(mScoreShopActivity, JSONUtil.getString(response, "msg", ""));
                    mLoadMoreWrapper.showLoadComplete();//加载完成视图
                    page--;
                } else {
                    String[] dataList = JSONUtil.getStringArray(response, "data", null);
                    if (dataList != null && dataList.length > 0) {
                        for (String data : dataList) {
                            scoreGoodsList.add(new ScoreGoods(
                                    "" + JSONUtil.getInt(data, "id", 0)
                                    , "" + JSONUtil.getString(data, "img", "")
                                    , "" + JSONUtil.getString(data, "name", "")
                                    , "" + JSONUtil.getInt(data, "score", 0)
                                    , "" + JSONUtil.getInt(data, "stock", 0)
                                    , "" + JSONUtil.getInt(data, "is_physical", -1)
                            ));
                        }
                        mLoadMoreWrapper.notifyDataSetChanged();
                    } else {
                        mLoadMoreWrapper.showLoadComplete();//加载完成视图
                        page--;
                    }
                }
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                LogUtils.e(e.getMessage());
            }
        });
        okHttpUtil.requestCall(mScoreShopActivity, "goods.getscoregoods", list);
    }

    @OnClick({R.id.tv_auction, R.id.tv_choujiang, R.id.tv_record})
    public void onViewClicked(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.tv_auction://商品竞拍
                ToastUtil.showEmpty(mScoreShopActivity);
                break;
            case R.id.tv_choujiang://积分抽奖
                intent = new Intent(mScoreShopActivity, ChouJiangActivity.class);
                mScoreShopActivity.startActivity(intent);
                break;
            case R.id.tv_record://兑换记录
                intent = new Intent(mScoreShopActivity, ScoreRecordActivity.class);
                mScoreShopActivity.startActivity(intent);
                break;
        }
    }
}
