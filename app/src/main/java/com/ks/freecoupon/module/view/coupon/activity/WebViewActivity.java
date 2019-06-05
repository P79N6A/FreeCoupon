package com.ks.freecoupon.module.view.coupon.activity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.http.SslError;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.ks.basictools.utils.LogUtils;
import com.ks.freecoupon.BaseActivity;
import com.ks.freecoupon.R;
import com.ks.freecoupon.module.bean.User;
import com.ks.freecoupon.module.view.account.activity.LoginActivity;
import com.ks.freecoupon.utils.SharedPreferencesUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Date:2019/3/29
 * Author：康少
 * Description：京东领券WebView
 */
public class WebViewActivity extends BaseActivity {
    private String url;

    @BindView(R.id.webView)
    WebView wv_webView;
    @BindView(R.id.prog)
    ProgressBar progressBar;
    private boolean isJingDong = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jing_dong_web_view);
        ButterKnife.bind(this);
        initObject();
        initView();
    }
    private void initObject() {
        url = getIntent().getStringExtra("url");
        isJingDong = getIntent().getBooleanExtra("isJingDong",false);
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initView() {
        if (isJingDong) {
            showTipDialog();
        }
        WebSettings webSettings = wv_webView.getSettings();
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);//设置js可以直接打开窗口，如window.open()，默认为false
        webSettings.setJavaScriptEnabled(true);//是否允许执行js，默认为false。设置true时，会提醒可能造成XSS漏洞
        webSettings.setSupportZoom(true);//是否可以缩放，默认true
        webSettings.setBuiltInZoomControls(true);//是否显示缩放按钮，默认false
        webSettings.setUseWideViewPort(true);//设置此属性，可任意比例缩放。大视图模式
        webSettings.setLoadWithOverviewMode(true);//和setUseWideViewPort(true)一起解决网页自适应问题
        webSettings.setAppCacheEnabled(true);//是否使用缓存
        webSettings.setDomStorageEnabled(true);//DOM Storage
        wv_webView.loadUrl(url);
        wv_webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return super.shouldOverrideUrlLoading(view, url);
            }
            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();
            }
        });
        //设置WebChromeClient类
        wv_webView.setWebChromeClient(new WebChromeClient() {
            //获取网站标题
            @Override
            public void onReceivedTitle(WebView view, String title) {
                LogUtils.d("该html页面标题:"+title);//html中的title值
            }
            //获取加载进度
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    // 网页加载完成
                    progressBar.setVisibility(View.GONE);//加载完网页进度条消失
                } else {
                    // 加载中
                    progressBar.setVisibility(View.VISIBLE);//开始加载网页时显示进度条
                    progressBar.setProgress(newProgress);//设置进度值
                }
            }
        });
    }

    /**
     * Description：展示提示弹出框
     */
    private void showTipDialog() {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("温馨提示")
                .setIcon(getResources().getDrawable(R.mipmap.ic_launcher))
                .setMessage("领取成功后，请到京东APP个人中心中使用")
                .setPositiveButton("确定",null)
                .create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(this.getResources().getColor(R.color.colorPrimary));
        dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(this.getResources().getColor(R.color.colorPrimary));
    }

    //go back
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // Check if the key event was the Back button and if there's history
        if ((keyCode == KeyEvent.KEYCODE_BACK) && wv_webView.canGoBack()) {
            wv_webView.goBack();
            return true;
        }
        //  return true;
        // If it wasn't the Back key or there's no web page history, bubble up to the default
        // system behavior (probably exit the activity)
        return super.onKeyDown(keyCode, event);
    }

    @OnClick(R.id.tv_close)
    void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_close:
                this.finish();
                break;
        }
    }
}
