package com.ks.freecoupon.override;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.WindowManager;

import com.ks.basictools.AppManager;
import com.ks.freecoupon.R;

import java.util.Objects;

/**
 * author：康少
 * date：2018/11/5
 * description：自定义加载中 dialog
 */
public class LoadingDialog extends ProgressDialog {
    private static LoadingDialog mInstance;

    public static LoadingDialog getInstance() {
        mInstance = new LoadingDialog(AppManager.getAppManager().currentActivity(), R.style.loadingDialog);
        return mInstance;
    }

    public LoadingDialog(Context context) {
        super(context);
    }

    public LoadingDialog(Context context, int theme) {
        super(context, theme);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init(getContext());
    }

    private void init(Context context) {
        //设置不可取消，点击其他区域不能取消，实际中可以抽出去封装供外包设置
        setCancelable(false);
        setCanceledOnTouchOutside(false);

        setContentView(R.layout.loading_dialog);
        WindowManager.LayoutParams params = Objects.requireNonNull(getWindow()).getAttributes();
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        getWindow().setAttributes(params);
    }

    @Override
    public void show() {
        super.show();
    }

    @Override
    public void dismiss() {
        super.dismiss();
        mInstance = null;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        super.dismiss();
        mInstance = null;
    }
}