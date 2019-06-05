package com.ks.freecoupon.module.view.pc.activity;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

import com.ks.basictools.publicView.TitleVarView;
import com.ks.freecoupon.R;
import com.ks.freecoupon.module.contract.pc.ScoreShopActivityContract;
import com.ks.freecoupon.module.view.pc.presenter.ScoreShopActivityPresenter;

import butterknife.BindView;

/**
 * Date:2019/4/3
 * Author：康少
 * Description：积分商城——视图层
 */
public class ScoreShopActivity extends TitleVarView implements ScoreShopActivityContract.IScoreShopActivity {
    private ScoreShopActivityContract.IScoreShopActivityPresenter mIActivityPresenter;

    private RecyclerView rv_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score_shop);
        findId();
        setTitleBar(true, "积分商城", "");
        new ScoreShopActivityPresenter(this);
        mIActivityPresenter.start();
    }

    private void findId() {
        rv_list = findViewById(R.id.rv_list);
    }

    public RecyclerView getRv_list() {
        return rv_list;
    }

    @Override
    public void setPresenter(ScoreShopActivityContract.IScoreShopActivityPresenter mIActivityPresenter) {
        this.mIActivityPresenter = mIActivityPresenter;
    }
}
