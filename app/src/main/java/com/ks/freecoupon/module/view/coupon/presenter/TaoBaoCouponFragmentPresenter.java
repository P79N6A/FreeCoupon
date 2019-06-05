package com.ks.freecoupon.module.view.coupon.presenter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
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
import com.ks.basictools.utils.LogUtils;
import com.ks.basictools.utils.ToastUtil;
import com.ks.freecoupon.BaseActivity;
import com.ks.freecoupon.R;
import com.ks.freecoupon.module.contract.coupon.TaoBaoCouponFragmentContract;
import com.ks.freecoupon.module.bean.TaoBaoGoods;
import com.ks.freecoupon.module.view.coupon.activity.CopyTaoWordActivity;
import com.ks.freecoupon.module.view.coupon.fragment.TaoBaoCouponFragment;
import com.ks.freecoupon.module.view.search.activity.SearchResultActivity;
import com.ks.freecoupon.override.RoundTransform;
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
 * date：2019/3/7
 * description：淘宝优惠券——逻辑层
 */
public class TaoBaoCouponFragmentPresenter implements TaoBaoCouponFragmentContract.ITaoBaoCouponFragmentPresenter {
    private TaoBaoCouponFragment mTaoBaoCouponFragment;
    private Context mContext;
    private Activity mActivity;
    /*控件对象*/
    private RecyclerView rv_list;
    private RelativeLayout rlEmpty;
    private SwipeRefreshLayout srl_refresh;
    /*商品列表*/
    private List<TaoBaoGoods> taoBaoGoodsList;
    /*参数*/
    private String keyWord = "";//搜索词
    private Handler mHandler;
    private CommonAdapter mAdapter;
    private LoadMoreWrapper mLoadMoreWrapper;
    private int currentPage = 1;//当前页数

    public TaoBaoCouponFragmentPresenter(TaoBaoCouponFragment mTaoBaoCouponFragment) {
        this.mTaoBaoCouponFragment = mTaoBaoCouponFragment;
        mTaoBaoCouponFragment.setPresenter(this);
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
                        if (taoBaoGoodsList.size() == 0) {
                            showEmpty();
                        } else {
                            applyView();
                        }
                        break;
                }
            }
        };
        getHttpData();
    }

    private void initObject() {
        rv_list = mTaoBaoCouponFragment.getRv_list();
        mContext = mTaoBaoCouponFragment.getmContext();
        rlEmpty = mTaoBaoCouponFragment.getRlEmpty();
        mActivity = mTaoBaoCouponFragment.getmActivity();
        srl_refresh = mTaoBaoCouponFragment.getSrl_refresh();
        if (mContext instanceof SearchResultActivity) {
            keyWord = ((SearchResultActivity) mContext).getKeyWord();
        }

        srl_refresh.setColorSchemeResources(R.color.colorPrimary);
        srl_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                taoBaoGoodsList.clear();
                currentPage = 1;
                getHttpData();
                srl_refresh.setRefreshing(false);//刷新结束，隐藏刷新进度条
            }
        });
        rv_list.setLayoutManager(new GridLayoutManager(mContext, 1));
        taoBaoGoodsList = new ArrayList<>();
    }

    /**
     * Description：展示空数据界面
     */
    private void showEmpty() {
        rlEmpty.setVisibility(View.VISIBLE);
        rv_list.setVisibility(View.GONE);
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
        rv_list.setVisibility(View.VISIBLE);
        mAdapter = new CommonAdapter<TaoBaoGoods>(mContext, R.layout.item_taobao_goods, taoBaoGoodsList) {
            @SuppressLint("SetTextI18n")
            @Override
            protected void convert(ViewHolder holder, final TaoBaoGoods taoBaoGoods, int position) {
                if (!taoBaoGoods.getImage().equals("")) {
                    Picasso.with(mContext)    //context
                            .load(taoBaoGoods.getImage())     //图片加载地址
                            .transform(new RoundTransform(10))
                            .error(android.R.drawable.ic_menu_delete)   //图片记载失败时显示的页面
                            .fit()      //智能展示图片，对于图片的大小和imageview的尺寸进行了测量，计算出最佳的大小和最佳的质量显示出来
                            .into((ImageView) holder.getView(R.id.image_item));    //需要加载图片的控件
                }
                ((TextView) holder.getView(R.id.tv_name)).setText(taoBaoGoods.getName());
                ((TextView) holder.getView(R.id.tv_juan)).setText("券" + taoBaoGoods.getPrice() + "元");
                ((TextView) holder.getView(R.id.tv_yuanjia)).setText("￥" + taoBaoGoods.getYuanjia());
                ((TextView) holder.getView(R.id.tv_yuanjia)).getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                ((TextView) holder.getView(R.id.tv_xianjia)).setText("￥" + taoBaoGoods.getDiscountPrice());
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolean isLogin = (boolean) SharedPreferencesUtils.getParam(mContext, "isLogin", false);
                        if (isLogin) {
                            // 跳转复制淘口令界面
                            Intent intent = new Intent(mActivity, CopyTaoWordActivity.class);
                            intent.putExtra("coupon_pwd", taoBaoGoods.getCoupon_pwd());
                            intent.putExtra("title", taoBaoGoods.getName());
                            intent.putExtra("yuanjia", taoBaoGoods.getYuanjia());
                            intent.putExtra("discount_price", taoBaoGoods.getDiscountPrice());
                            if (mActivity != null) {
                                mActivity.startActivity(intent);
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
        if (mActivity != null) {
            ((BaseActivity) mActivity).dismissLoading();
        }
    }

    private void getHttpData() {
        if (mActivity != null) {
            ((BaseActivity) mActivity).showLoading();
        }
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        map.put("page_no", currentPage);
        map.put("keyword", keyWord);
        list.add(map);
        OkHttpUtil okHttpUtil = new OkHttpUtil(new StringCallBack() {
            @Override
            public void onResponse(String response, int id) {
                boolean status = JSONUtil.getBoolean(response, "status", false);
                if (status) {
                    String data = JSONUtil.getString(response, "data", "");
                    String[] coupon_lists = JSONUtil.getStringArray(data, "coupon_list", null);
                    if (coupon_lists != null) {
                        taoBaoGoodsList.clear();
                        for (String coupon_list : coupon_lists) {
                            double zk_final_price = Float.parseFloat(JSONUtil.getString(coupon_list, "zk_final_price", ""));
                            double coupon_value = Float.parseFloat(JSONUtil.getString(coupon_list, "coupon_value", ""));
                            double discountPricec = zk_final_price - coupon_value;
                            TaoBaoGoods taoBaoGoods = new TaoBaoGoods(
                                    JSONUtil.getString(coupon_list, "num_iid", "") + "",
                                    JSONUtil.getString(coupon_list, "pict_url", "") + "",
                                    JSONUtil.getString(coupon_list, "title", "") + "",
                                    BaseUtils.doubleFormatBy2(coupon_value) + "",
                                    BaseUtils.doubleFormatBy2(zk_final_price) + "",
                                    BaseUtils.doubleFormatBy2(discountPricec) + "",
                                    JSONUtil.getString(coupon_list, "coupon_pwd", "") + ""
                            );
                            taoBaoGoodsList.add(taoBaoGoods);
                        }
                        mHandler.sendEmptyMessage(1);
                    } else {
                        if (mActivity != null) {
                            ((BaseActivity) mActivity).dismissLoading();
                        }
                    }
                }else {
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
                if (mActivity != null) {
                    ((BaseActivity) mActivity).dismissLoading();
                }
            }
        });
        okHttpUtil.requestCall(mContext, "coupon.gettblist", list);
    }

    private void loadMore() {
        mLoadMoreWrapper = new LoadMoreWrapper(mContext, mAdapter);
        mLoadMoreWrapper.setLoadMoreView(LayoutInflater.from(mContext).inflate(R.layout.loading, rv_list, false));
        mLoadMoreWrapper.setOnLoadMoreListener(new LoadMoreWrapper.OnLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                /*获取商品列表*/
                updateRecyclerView();
            }
        });
        rv_list.setAdapter(mLoadMoreWrapper);
    }

    /**
     * Description：加载更多
     */
    private void updateRecyclerView() {
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        map.put("page_no", ++currentPage);
        map.put("keyword", keyWord);
        list.add(map);
        OkHttpUtil okHttpUtil = new OkHttpUtil(new StringCallBack() {
            @Override
            public void onResponse(String response, int id) {
                boolean status = JSONUtil.getBoolean(response, "status", false);
                if (status) {
                    String data = JSONUtil.getString(response, "data", "");
                    String[] coupon_lists = JSONUtil.getStringArray(data, "coupon_list", null);
                    if (coupon_lists != null) {
                        for (String coupon_list : coupon_lists) {
                            double zk_final_price = Float.parseFloat(JSONUtil.getString(coupon_list, "zk_final_price", ""));
                            double coupon_value = Float.parseFloat(JSONUtil.getString(coupon_list, "coupon_value", ""));
                            double discountPrice = zk_final_price - coupon_value;
                            TaoBaoGoods taoBaoGoods = new TaoBaoGoods(
                                    JSONUtil.getString(coupon_list, "num_iid", "") + "",
                                    JSONUtil.getString(coupon_list, "pict_url", "") + "",
                                    JSONUtil.getString(coupon_list, "title", "") + "",
                                    BaseUtils.doubleFormatBy2(coupon_value) + "",
                                    BaseUtils.doubleFormatBy2(zk_final_price) + "",
                                    BaseUtils.doubleFormatBy2(discountPrice) + "",
                                    JSONUtil.getString(coupon_list, "coupon_pwd", "") + "");
                            taoBaoGoodsList.add(taoBaoGoods);
                        }
                        if (coupon_lists.length > 0) {
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
                    String msg = JSONUtil.getString(response, "msg", "");
                    ToastUtil.show(mContext, msg);
                    if (mContext != null) {
                        ((BaseActivity) mContext).dismissLoading();
                    }
                }
            }

            @Override
            public void onError(Call call, Exception e, int id) {

            }
        });
        okHttpUtil.requestCall(mContext, "coupon.gettblist", list);
    }

    @Override
    public void onDestroy() {
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
    }
}
