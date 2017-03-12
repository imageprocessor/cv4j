package com.cv4j.app.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.cv4j.app.R;
import com.cv4j.app.app.BaseFragment;
import com.safframework.injectview.Injector;
import com.safframework.injectview.annotations.InjectView;

/**
 * Created by Tony Shen on 2017/3/12.
 */

public class HomeFragment extends BaseFragment {


    @InjectView(R.id.webview)
    WebView webview;

    ProgressDialog progDailog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);
        Injector.injectInto(this, v);

//        progDailog = ProgressDialog.show(mContext, "Loading","Please wait...", true);
//        progDailog.setCancelable(false);
//
//        initViews();

        return v;
    }

    private void initViews() {
        webview.loadUrl("https://github.com/imageprocessor/cv4j");
        //覆盖webView默认通过系统浏览器打开网页的方式
        webview.setWebViewClient(new WebViewClient(){

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                progDailog.show();
                view.loadUrl(url);

                return true;
            }
            @Override
            public void onPageFinished(WebView view, final String url) {
                progDailog.dismiss();
            }
        });
        //获取WebView类设置对象
        WebSettings settings = webview.getSettings();
        //使webView支持js
        settings.setJavaScriptEnabled(true);
        //设置webView缓存模式
        webview.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);//(优先使用缓存)
        //webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);//(不使用缓存)
        webview.setWebChromeClient(new WebChromeClient() {});

        webview.getSettings().setAllowFileAccess(true);
        //如果访问的页面中有Javascript，则webview必须设置支持Javascript
        webview.getSettings().setJavaScriptEnabled(true);
//        webView.getSettings().setUserAgentString(MyApplication.getUserAgent());
        webview.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        webview.getSettings().setAllowFileAccess(true);
        webview.getSettings().setAppCacheEnabled(true);
        webview.getSettings().setDomStorageEnabled(true);
        webview.getSettings().setDatabaseEnabled(true);
    }
}
