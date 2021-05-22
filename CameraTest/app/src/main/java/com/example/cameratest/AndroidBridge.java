package com.example.cameratest;


import android.os.Message;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;

import android.webkit.WebView;
import android.content.Context;
import android.widget.Toast;


import android.os.Handler;

public class AndroidBridge {

    final public Handler handler = new Handler();

    private WebView bView;
    private Context bcontext;


    public AndroidBridge(Context context, WebView  webview ){
        this.bcontext = context;
        this.bView = webview;
    }
    @JavascriptInterface
    public void OnCamera(){
        Toast.makeText(bcontext, "카메라를 켭니다.", Toast.LENGTH_SHORT).show();//alert창으로 생각하셈
    }








}
