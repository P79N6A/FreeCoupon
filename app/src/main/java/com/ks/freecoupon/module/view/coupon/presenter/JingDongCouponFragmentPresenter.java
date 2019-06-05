package com.ks.freecoupon.module.view.coupon.presenter;

import android.annotation.SuppressLint;
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
import com.ks.freecoupon.module.bean.JingDongGoods;
import com.ks.freecoupon.module.contract.coupon.JingDongCouponFragmentContract;
import com.ks.freecoupon.module.view.coupon.activity.WebViewActivity;
import com.ks.freecoupon.module.view.coupon.fragment.JingDongCouponFragment;
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
 * date：2019/3/27
 * description：京东领券——逻辑层
 */
public class JingDongCouponFragmentPresenter implements JingDongCouponFragmentContract.IJingDongCouponFragmentPresenter {
    private JingDongCouponFragment mJingDongCouponFragment;
    private Context mContext;
    /*控件对象*/
    private RelativeLayout rlEmpty;
    private RecyclerView rvList;
    private SwipeRefreshLayout srl_refresh;
    /*商品列表*/
    private List<JingDongGoods> jingDongGoodsList;
    /*参数*/
    private String keyWord = "";//搜索关键词
    private Handler mHandler;
    private CommonAdapter mAdapter;
    private LoadMoreWrapper mLoadMoreWrapper;
    private int currentPage = 1;//当前页数

    public JingDongCouponFragmentPresenter(JingDongCouponFragment mJingDongCouponFragment) {
        this.mJingDongCouponFragment = mJingDongCouponFragment;
        mJingDongCouponFragment.setPresenter(this);
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
                        if (jingDongGoodsList.size() == 0) {
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
        if (mContext instanceof SearchResultActivity) {
            keyWord = ((SearchResultActivity) mContext).getKeyWord();
        }
        mContext = mJingDongCouponFragment.getmContext();
        rlEmpty = mJingDongCouponFragment.getRlEmpty();
        rvList = mJingDongCouponFragment.getRvList();
        srl_refresh = mJingDongCouponFragment.getSrl_refresh();

        jingDongGoodsList = new ArrayList<>();
        rvList.setLayoutManager(new GridLayoutManager(mContext, 1));
        srl_refresh.setColorSchemeResources(R.color.colorPrimary);
        srl_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                jingDongGoodsList.clear();
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
        mAdapter = new CommonAdapter<JingDongGoods>(mContext, R.layout.item_jingdong_goods, jingDongGoodsList) {
            @SuppressLint("SetTextI18n")
            @Override
            protected void convert(ViewHolder holder, final JingDongGoods jingDongGoods, int position) {
                if (!jingDongGoods.getImage().equals("")) {
                    Picasso.with(mContext)    //context
                            .load(jingDongGoods.getImage())     //图片加载地址
                            .transform(new RoundTransform(10))
                            .error(android.R.drawable.ic_menu_delete)   //图片记载失败时显示的页面
                            .fit()      //智能展示图片，对于图片的大小和imageview的尺寸进行了测量，计算出最佳的大小和最佳的质量显示出来
                            .into((ImageView) holder.getView(R.id.image_item));    //需要加载图片的控件
                }
                ((TextView) holder.getView(R.id.tv_name)).setText(jingDongGoods.getTitle());
                ((TextView) holder.getView(R.id.tv_juan)).setText("券" + jingDongGoods.getPrice() + "元");
                ((TextView) holder.getView(R.id.tv_yuanjia)).setText("￥" + jingDongGoods.getQuota());
                ((TextView) holder.getView(R.id.tv_yuanjia)).getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                ((TextView) holder.getView(R.id.tv_xianjia)).setText("￥" + jingDongGoods.getDiscountPrice());
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolean isLogin = (boolean) SharedPreferencesUtils.getParam(mContext, "isLogin", false);
                        if (isLogin) {
                            // 跳转WebView领券界面
                            Intent intent = new Intent(mContext, WebViewActivity.class);
                            intent.putExtra("url", jingDongGoods.getLink());
                            intent.putExtra("isJingDong", true);
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
        if (jingDongGoodsList.size() == 0) {
            rvList.setAdapter(mAdapter);
        } else {
            loadMore();
        }
        //关闭loading
        if (mContext != null) {
            ((BaseActivity) mContext).dismissLoading();
        }
    }

    private void getHttpData() {
        if (mContext != null) {
            ((BaseActivity) mContext).showLoading();
        }
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        map.put("keyword", keyWord);
        list.add(map);
        OkHttpUtil okHttpUtil = new OkHttpUtil(new StringCallBack() {
            @Override
            public void onResponse(String response, int id) {
                boolean status = JSONUtil.getBoolean(response, "status", false);
                if (status) {
                    String[] dataList = JSONUtil.getStringArray(response, "data", null);
                    if (dataList != null) {
                        jingDongGoodsList.clear();
                        for (String data : dataList) {
                            String couponInfo = JSONUtil.getString(data, "couponInfo", "");
                            String imageInfo = JSONUtil.getString(data, "imageInfo", "");
                            String[] imageList = JSONUtil.getStringArray(imageInfo, "imageList", null);
                            String[] couponLists = JSONUtil.getStringArray(couponInfo, "couponList", null);
                            //封面图
                            String image = JSONUtil.getString(imageList != null ? imageList[0] : null, "url", "");
                            //Title
                            String skuName = JSONUtil.getString(data, "skuName", "");
                            if (couponLists != null && couponLists.length > 0) {
                                String coupon;
                                if (couponLists.length == 2) {
                                    coupon = couponLists[1];
                                } else {
                                    coupon = couponLists[0];
                                }
                                //领券链接
                                String link = JSONUtil.getString(coupon, "link", "");
                                //原价
                                int quota = JSONUtil.getInt(coupon, "quota", 0);
                                //优惠金额
                                int discount = JSONUtil.getInt(coupon, "discount", 0);
                                //现价
                                int discountPrice = quota - discount;
                                JingDongGoods jingDongGoods = new JingDongGoods(
                                        image + "",
                                        skuName + "",
                                        BaseUtils.doubleFormatBy2(discount) + "",
                                        BaseUtils.doubleFormatBy2(quota) + "",
                                        BaseUtils.doubleFormatBy2(discountPrice) + "",
                                        link + ""
                                );
                                jingDongGoodsList.add(jingDongGoods);
                            }
                        }
                        mHandler.sendEmptyMessage(1);
                    } else {
                        if (mContext != null) {
                            ((BaseActivity) mContext).dismissLoading();
                        }
                    }
                } else {
                    LogUtils.e("京东领券 response：" + response);
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
        okHttpUtil.requestCall(mContext, "coupon.getjdlist", list);
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
        list.add(map);
        OkHttpUtil okHttpUtil = new OkHttpUtil(new StringCallBack() {
            @Override
            public void onResponse(String response, int id) {
                boolean status = JSONUtil.getBoolean(response, "status", false);
                if (status) {
                    String[] dataList = JSONUtil.getStringArray(response, "data", null);
                    if (dataList != null) {
                        for (String data : dataList) {
                            String couponInfo = JSONUtil.getString(data, "couponInfo", "");
                            String imageInfo = JSONUtil.getString(data, "imageInfo", "");
                            String[] imageList = JSONUtil.getStringArray(imageInfo, "imageList", null);
                            String[] couponLists = JSONUtil.getStringArray(couponInfo, "couponList", null);
                            //封面图
                            String image = JSONUtil.getString(imageList != null ? imageList[0] : null, "url", "");
                            //Title
                            String skuName = JSONUtil.getString(data, "skuName", "");
                            if (couponLists != null && couponLists.length > 0) {
                                String coupon;
                                if (couponLists.length == 2) {
                                    coupon = couponLists[1];
                                } else {
                                    coupon = couponLists[0];
                                }
                                //领券链接
                                String link = JSONUtil.getString(coupon, "link", "");
                                //满减金额
                                int quota = JSONUtil.getInt(coupon, "quota", 0);
                                //优惠金额
                                int discount = JSONUtil.getInt(coupon, "discount", 0);
                                //现价
                                int discountPrice = quota - discount;
                                JingDongGoods jingDongGoods = new JingDongGoods(
                                        image + "",
                                        skuName + "",
                                        BaseUtils.doubleFormatBy2(discount) + "",
                                        BaseUtils.doubleFormatBy2(quota) + "",
                                        BaseUtils.doubleFormatBy2(discountPrice) + "",
                                        link + ""
                                );
                                jingDongGoodsList.add(jingDongGoods);
                            }
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
                    String msg = JSONUtil.getString(response, "msg", "");
                    ToastUtil.show(mContext, msg);
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
        okHttpUtil.requestCall(mContext, "coupon.getjdlist", list);
    }

    @Override
    public void onDestroy() {
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
    }
}
