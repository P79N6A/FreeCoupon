package com.ks.freecoupon.module.view.pc.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.ks.basictools.publicView.TitleVarView;
import com.ks.freecoupon.BaseActivity;
import com.ks.freecoupon.R;
import com.ks.freecoupon.module.contract.pc.UpdatePersonInfoActivityContract;
import com.ks.freecoupon.module.view.pc.presenter.UpdatePersonInfoActivityPresenter;

/**
 * Date:2019/3/26
 * Author：康少
 * Description：修改个人信息——视图层
 */
public class UpdatePersonInfoActivity extends TitleVarView implements UpdatePersonInfoActivityContract.IUpdatePersonInfoActivity{
    private UpdatePersonInfoActivityContract.IUpdatePersonInfoActivityPresenter mIActivityPresenter;

    private EditText et_content;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_person_info);
        findId();
        initView();
        new UpdatePersonInfoActivityPresenter(this);
        mIActivityPresenter.start();
    }

    private void findId() {
        et_content = findViewById(R.id.et_content);
    }

    private void initView() {
        String centerText = "";
        String type = getIntent().getStringExtra("type");
        String cache = getIntent().getStringExtra("cache");
        switch (type) {
            case "nickname":
                centerText = "用户名";
                break;
        }
        setTitleBar(true, centerText, "保存");
        et_content.setHint(centerText);
        if (!cache.equals("")) {
            et_content.setText(cache);
        }
    }

    /**
     * Description：宿主方法
     */
    public EditText getEt_content() {
        return et_content;
    }

    @Override
    public void setPresenter(UpdatePersonInfoActivityContract.IUpdatePersonInfoActivityPresenter mIActivityPresenter) {
        this.mIActivityPresenter = mIActivityPresenter;
    }
}
