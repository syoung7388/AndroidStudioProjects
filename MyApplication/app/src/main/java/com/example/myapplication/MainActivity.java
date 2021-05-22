package com.example.myapplication;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;



public class MainActivity extends AppCompatActivity {

    WebView wView;
    ProgressBar pBar;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        wView = findViewById(R.id.wView);
        pBar = findViewById(R.id.pBar);
        pBar.setVisibility(View.GONE); /// 공간마저 감추겠다  //VISIBLE // INVISIBLE

        initWebView();
    }
    public void initWebView(){
        wView.setWebViewClient(new WebViewClient(){ // webview에 client 연결함
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon){
                super.onPageStarted(view, url, favicon);
                pBar.setVisibility(View.VISIBLE);
            }// 로딩이 시작될때

            @Override
            public void onPageFinished(WebView view , String url){
                super.onPageFinished(view, url);
                pBar.setVisibility(View.GONE);
            }//완료시 한번 호출


//            @Override
//            public void doUpdateVisitedHistory(WebView view, String url, boolean isReload) {
//                super.doUpdateVisitedHistory(view, url, isReload);
//            } => 방문내역 업데이트 할시


            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url){
                view.loadUrl(url);
                return true;
            } // => 어케 할까?
        });
        WebSettings ws = wView.getSettings();
        ws.setJavaScriptEnabled(true);
//        ws.setCacheMode(WebSettings.LOAD_NO_CACHE);
//        ws.setDomStorageEnabled(true);
//        ws.setDatabaseEnabled(true);
//        ws.setAllowContentAccess(true);
//        ws.setUseWideViewPort(true);
//        ws.setJavaScriptCanOpenWindowsAutomatically(true);
        ws.setAllowFileAccess(true);
        ws.setAllowContentAccess(true);
        ws.setAllowFileAccessFromFileURLs(true);
        ws.setAllowUniversalAccessFromFileURLs(true);

        wView.loadUrl("http://172.30.1.29:8080");
//        wView.loadUrl("http://172.30.1.29:9200");
//        wView.loadUrl("http://172.30.1.29:7000");



    }
}