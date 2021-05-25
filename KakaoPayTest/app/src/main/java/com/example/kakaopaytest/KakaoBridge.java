package com.example.kakaopaytest;



import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.content.Context;
import android.widget.Toast;
import android.util.Log;


import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

public class KakaoBridge  extends AppCompatActivity {
    private String TAG = "MyTag";
    final public Handler handler = new Handler();

    private WebView bView;
    private Context bcontext;







    public KakaoBridge(Context context, WebView  webview ){
        this.bcontext = context;
        this.bView = webview;

    }
    @JavascriptInterface
    public void PayWindow(String url){
        Toast.makeText(bcontext, url, Toast.LENGTH_SHORT).show();//alert창으로 생각하셈
        bView.post(new Runnable() {
            @Override
            public void run() {
                bView.loadUrl(url);

            }
        });


    }

}
