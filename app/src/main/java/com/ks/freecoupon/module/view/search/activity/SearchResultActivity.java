package com.ks.freecoupon.module.view.search.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.EditText;

import com.ks.basictools.utils.LogUtils;
import com.ks.basictools.utils.ToastUtil;
import com.ks.freecoupon.BaseActivity;
import com.ks.freecoupon.R;
import com.ks.freecoupon.module.adapter.ViewPagerAdapter;
import com.ks.freecoupon.module.view.coupon.fragment.JingDongCouponFragment;
import com.ks.freecoupon.module.view.coupon.fragment.NearShopFragment;
import com.ks.freecoupon.module.view.coupon.fragment.TaoBaoCouponFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Date:2019/4/1
 * Author：康少
 * Description：搜索结果展示界面
 */
public class SearchResultActivity extends BaseActivity {
    /*关键词*/
    private String keyWord = "";
    /*参数*/
    private List<Fragment> viewList;
    private NearShopFragment mNearShopFragment;
    private TaoBaoCouponFragment mTaoBaoCouponFragment;
    private JingDongCouponFragment mJingDongCouponFragment;
    private ViewPagerAdapter pagerAdapter;

    @BindView(R.id.et_search_clazz)
    EditText etSearchClazz;
    @BindView(R.id.tab)
    TabLayout tab;
    @BindView(R.id.viewpager)
    ViewPager viewpager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);
        ButterKnife.bind(this);
        start();
    }

    private void start() {
        initObject();
        initView();
    }

    private void initObject() {
        String keyWord = getIntent().getStringExtra("keyWord");
        setKeyWord(keyWord);
        etSearchClazz.setText(keyWord);

        viewList = new ArrayList<>();
        mNearShopFragment = new NearShopFragment();
        mTaoBaoCouponFragment = new TaoBaoCouponFragment();
        mJingDongCouponFragment = new JingDongCouponFragment();
    }

    private void initView() {
        viewList.add(mTaoBaoCouponFragment);
        viewList.add(mJingDongCouponFragment);
        viewList.add(mNearShopFragment);
        /*适配器部分*/
        pagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), viewList);
        //给ViewPager设置适配器
        viewpager.setAdapter(pagerAdapter);
        //当前页面两侧缓存/预加载的页面数目。当页面切换时，当前页面相邻两侧的numbers页面不会被销毁
        viewpager.setOffscreenPageLimit(2);
        //将TabLayout和ViewPager关联起来。
        tab.setupWithViewPager(viewpager);
        //设置可以滑动
        tab.setTabMode(TabLayout.MODE_SCROLLABLE);
        //充满整个屏幕
        tab.setTabMode(TabLayout.MODE_FIXED);
    }

    /**
     * Description：宿主方法
     */
    public String getKeyWord() {
        return keyWord;
    }
    public void setKeyWord(String keyWord) {
        this.keyWord = keyWord;
    }

    /**
     * Description：执行搜索操作
     */
    private void goToSearch() {
        List<Fragment> fragments = new ArrayList<>();
        TaoBaoCouponFragment mTaoBaoCouponFragment = new TaoBaoCouponFragment();
        NearShopFragment mNearShopFragment = new NearShopFragment();
        JingDongCouponFragment mmJingDongCouponFragment = new JingDongCouponFragment();
        fragments.add(mTaoBaoCouponFragment);
        fragments.add(mmJingDongCouponFragment);
        fragments.add(mNearShopFragment);
        pagerAdapter.refreshFragments(fragments);//刷新fragments数据
    }

    @OnClick({R.id.iv_back, R.id.tv_search})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                this.finish();
                break;
            case R.id.tv_search:
                String keyword = etSearchClazz.getText().toString();
                setKeyWord(keyword);
                goToSearch();
                break;
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (mNearShopFragment != null) {
            mNearShopFragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
