package com.ks.freecoupon.module.view.pc.presenter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ks.basictools.base_interface.StringCallBack;
import com.ks.basictools.utils.JSONUtil;
import com.ks.basictools.utils.ToastUtil;
import com.ks.freecoupon.BaseActivity;
import com.ks.freecoupon.R;
import com.ks.freecoupon.module.contract.pc.PCFragmentContract;
import com.ks.freecoupon.module.bean.User;
import com.ks.freecoupon.module.view.account.activity.LoginActivity;
import com.ks.freecoupon.module.view.account.activity.RegisterActivity;
import com.ks.freecoupon.module.view.pc.activity.AboutActivity;
import com.ks.freecoupon.module.view.pc.activity.DiscountCouponActivity;
import com.ks.freecoupon.module.view.pc.activity.EarnPointsActivity;
import com.ks.freecoupon.module.view.pc.activity.FeedBackActivity;
import com.ks.freecoupon.module.view.pc.activity.LinkUsActivity;
import com.ks.freecoupon.module.view.pc.activity.PersonInfoActivity;
import com.ks.freecoupon.module.view.pc.activity.ScoreShopActivity;
import com.ks.freecoupon.module.view.pc.fragment.PCFragment;
import com.ks.freecoupon.override.CustomPopWindow;
import com.ks.freecoupon.override.CustomPopWindowItem;
import com.ks.freecoupon.override.RoundTransform;
import com.ks.freecoupon.utils.BaseUtils;
import com.ks.freecoupon.utils.OkHttpUtil;
import com.ks.freecoupon.utils.SharedPreferencesUtils;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

/**
 * author：康少
 * date：2019/3/7
 * description：个人中心——逻辑层
 */
public class PCFragmentPresenter implements PCFragmentContract.IPCFragmentPresenter {
    private PCFragment mPCFragment;
    private Context mContext;

    /*控件对象*/
    private ImageView ivAvatar;
    private TextView tvName;
    private LinearLayout afterLogin;
    private LinearLayout beforeLogin;
    private TextView tvIntegral;
    private TextView tvCoupon;
    private ImageView iv_wenhao;
    private SwipeRefreshLayout srl_refresh;
    /*参数*/
    private boolean isLogin;
    private String default_avatar;
    private Handler mHandler;

    public PCFragmentPresenter(PCFragment mPCFragment) {
        this.mPCFragment = mPCFragment;
        mPCFragment.setPresenter(this);
    }

    @SuppressLint({"CheckResult", "HandlerLeak"})
    @Override
    public void start() {
        initObject();
        applyView();
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
    }

    private void initObject() {
        mContext = mPCFragment.getmContext();
        ivAvatar = mPCFragment.getIvAvatar();
        tvName = mPCFragment.getTvName();
        afterLogin = mPCFragment.getAfterLogin();
        beforeLogin = mPCFragment.getBeforeLogin();
        tvIntegral = mPCFragment.getTvIntegral();
        tvCoupon = mPCFragment.getTvCoupon();
        iv_wenhao = mPCFragment.getIv_wenhao();
        srl_refresh = mPCFragment.getSrl_refresh();

        srl_refresh.setColorSchemeResources(R.color.colorPrimary);
        srl_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshPersonInfo();
                srl_refresh.setRefreshing(false);//刷新结束，隐藏刷新进度条
            }
        });

        iv_wenhao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<CustomPopWindowItem> popWindowItems = new ArrayList<>();
                popWindowItems.add(new CustomPopWindowItem(
                        "0", 0,
                        mContext.getResources().getString(R.string.string_rule)
                ));
                CustomPopWindow customPopWindow = new CustomPopWindow(mContext, popWindowItems, null);
                customPopWindow.showAtBottom(iv_wenhao);
            }
        });

        isLogin = (boolean) SharedPreferencesUtils.getParam(mContext, "isLogin", false);

        //注册EventBus
        EventBus.getDefault().register(this);
    }

    /**
     * Description：加载视图
     */
    private void applyView() {
        toggleLogin(isLogin);//切换登录
        if (isLogin) {
            tvName.setText(User.getInstance().getNickname());//设置昵称
            tvIntegral.setText(String.valueOf(User.getInstance().getPoint()));//设置积分数
            tvCoupon.setText(String.valueOf(User.getInstance().getCoupon()));//设置卡券数
            if (User.getInstance().getAvatar() != null && !User.getInstance().getAvatar().equals("")) {
                Picasso.with(mContext)    //context
                        .load(User.getInstance().getAvatar())     //图片加载地址
                        .transform(new RoundTransform(400))
                        .error(android.R.drawable.ic_menu_delete)   //图片记载失败时显示的页面
                        .fit()      //智能展示图片，对于图片的大小和imageview的尺寸进行了测量，计算出最佳的大小和最佳的质量显示出来
                        .into(ivAvatar);    //需要加载图片的控件
            }
        } else {
            tvIntegral.setText(mContext.getResources().getString(R.string.string_moren));
            tvCoupon.setText(mContext.getResources().getString(R.string.string_moren));
            default_avatar = "http:\\/\\/card.benwunet.com\\/static\\/images\\/default_avatar.png";
            Picasso.with(mContext)    //context
                    .load(default_avatar)     //图片加载地址
                    .transform(new RoundTransform(400))
                    .error(android.R.drawable.ic_menu_delete)   //图片记载失败时显示的页面
                    .fit()      //智能展示图片，对于图片的大小和imageview的尺寸进行了测量，计算出最佳的大小和最佳的质量显示出来
                    .into(ivAvatar);    //需要加载图片的控件
        }
        ((BaseActivity) mContext).dismissLoading();
    }

    /**
     * Description：刷新用户数据
     */
    private void refreshPersonInfo() {
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
                    int user_id = JSONUtil.getInt(data, "id", 0);
                    String mobile = JSONUtil.getString(data, "mobile", "");
                    String avatar = JSONUtil.getString(data, "avatar", "");
                    String nickname = JSONUtil.getString(data, "nickname", "");
                    int point = JSONUtil.getInt(data, "point", 0);
                    int coupon = JSONUtil.getInt(data, "coupon", 0);
                    String ship_name = JSONUtil.getString(data, "ship_name", "");
                    String ship_mobile = JSONUtil.getString(data, "ship_mobile", "");
                    String ship_area = JSONUtil.getString(data, "ship_area", "");
                    String ship_address = JSONUtil.getString(data, "ship_address", "");

                    User user = User.getInstance();
                    user.setId(user_id);
                    user.setMobile(mobile);
                    user.setAvatar(avatar);
                    user.setNickname(nickname);
                    user.setPoint(point);
                    user.setCoupon(coupon);
                    user.setShip_name(ship_name == null ? "" : ship_name);
                    user.setShip_mobile(ship_mobile == null ? "" : ship_mobile);
                    user.setShip_area(ship_area == null ? "" : ship_area);
                    user.setShip_address(ship_address == null ? "" : ship_address);

                    mHandler.sendEmptyMessage(1);
                } else {
                    ToastUtil.show(mContext, JSONUtil.getString(response, "msg", ""));
                }
            }
            @Override
            public void onError(Call call, Exception e, int id) {}
        });
        okHttpUtil.requestCall(mContext, "user.info", list);
    }

    /**
     * 由 UpdatePersonInfoActivityPresenter 发出通知——修改昵称
     * 由 ScoreGoodsDetailActivity 发出通知——更新积分
     * 由 GetNearCouponActivityPresenter 发出通知——更新卡券数量
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateView(User user) {
        toggleLogin(isLogin);//切换登录
        if (isLogin) {
            tvName.setText(User.getInstance().getNickname());//设置昵称
            tvIntegral.setText(String.valueOf(User.getInstance().getPoint()));//设置积分数
            tvCoupon.setText(String.valueOf(User.getInstance().getCoupon()));//设置卡券数
            if (User.getInstance().getAvatar() != null && !User.getInstance().getAvatar().equals("")) {
                Picasso.with(mContext)    //context
                        .load(User.getInstance().getAvatar())     //图片加载地址
                        .transform(new RoundTransform(400))
                        .error(android.R.drawable.ic_menu_delete)   //图片记载失败时显示的页面
                        .fit()      //智能展示图片，对于图片的大小和imageview的尺寸进行了测量，计算出最佳的大小和最佳的质量显示出来
                        .into(ivAvatar);    //需要加载图片的控件
            }
        } else {
            tvIntegral.setText(mContext.getResources().getString(R.string.string_moren));
            tvCoupon.setText(mContext.getResources().getString(R.string.string_moren));
            default_avatar = "http:\\/\\/card.benwunet.com\\/static\\/images\\/default_avatar.png";
            Picasso.with(mContext)    //context
                    .load(default_avatar)     //图片加载地址
                    .transform(new RoundTransform(400))
                    .error(android.R.drawable.ic_menu_delete)   //图片记载失败时显示的页面
                    .fit()      //智能展示图片，对于图片的大小和imageview的尺寸进行了测量，计算出最佳的大小和最佳的质量显示出来
                    .into(ivAvatar);    //需要加载图片的控件
        }
        ((BaseActivity) mContext).dismissLoading();
    }

    /**
     * Description：切换登录
     */
    private void toggleLogin(boolean isLogin) {
        if (isLogin) {
            afterLogin.setVisibility(View.VISIBLE);
            beforeLogin.setVisibility(View.GONE);
        } else {
            afterLogin.setVisibility(View.GONE);
            beforeLogin.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Description：跳转Activity方法
     */
    private void startActivity(Context context, Intent intent) {
        if (context != null) {
            context.startActivity(intent);
        } else {
            try {
                throw new Exception("mPCFragment.getContext()为空！");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onResume() {
        boolean isLogin2 = (boolean) SharedPreferencesUtils.getParam(mContext, "isLogin", false);
        if (!isLogin && isLogin2) {
            isLogin = true;
            applyView();//开始没登录，重回界面登陆了
        } else if (isLogin && !isLogin2) {
            toggleLogin(false);//开始登陆了，后来没登录
        }
        if (isLogin) {
            refreshPersonInfo();
        }
    }

    /**
     * Description：点击事件
     */
    @Override
    public void onClick(View view) {
        boolean isLogin = (boolean) SharedPreferencesUtils.getParam(mContext, "isLogin", false);
        Intent intent;
        switch (view.getId()) {
            case R.id.tv_login://登录
                intent = new Intent(mContext, LoginActivity.class);
                startActivity(mContext, intent);
                break;
            case R.id.tv_register://注册
                intent = new Intent(mContext, RegisterActivity.class);
                startActivity(mContext, intent);
                break;
            case R.id.ll_integral://我的积分
                if (isLogin) {
                    intent = new Intent(mContext, ScoreShopActivity.class);
                    startActivity(mContext, intent);
                } else {
                    BaseUtils.promptLogin(mContext);
                }
                break;
            case R.id.ll_coupon://我的卡券
                if (isLogin) {
                    // 展示我的优惠券列表
                    intent = new Intent(mContext, DiscountCouponActivity.class);
                    startActivity(mContext, intent);
                } else {
                    BaseUtils.promptLogin(mContext);
                }
                break;
            case R.id.rl_connect_us://联系我们
                intent = new Intent(mContext, LinkUsActivity.class);
                startActivity(mContext, intent);
                break;
            case R.id.rl_exchange://赚取积分
                if (isLogin) {
                    // 展示赚取积分
                    intent = new Intent(mContext, EarnPointsActivity.class);
                    startActivity(mContext, intent);
                } else {
                    BaseUtils.promptLogin(mContext);
                }
                break;
            case R.id.rl_feedback://意见反馈
                if (isLogin) {
                    intent = new Intent(mContext, FeedBackActivity.class);
                    startActivity(mContext, intent);
                } else {
                    BaseUtils.promptLogin(mContext);
                }
                break;
            case R.id.rl_about://关于
                intent = new Intent(mContext, AboutActivity.class);
                startActivity(mContext, intent);
                break;
            case R.id.rl_set://设置
                if (isLogin) {
                    intent = new Intent(mContext, PersonInfoActivity.class);
                    startActivity(mContext, intent);
                } else {
                    BaseUtils.promptLogin(mContext);
                }
                break;
        }
    }

    @Override
    public void onDestroy() {
        //解除EventBus注册
        if(EventBus.getDefault().isRegistered(mContext)) {
            EventBus.getDefault().unregister(mContext);
        }
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
    }
}
