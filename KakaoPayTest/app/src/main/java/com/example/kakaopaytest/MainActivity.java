package com.example.kakaopaytest;

import android.content.Context;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.net.URISyntaxException;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MyTag";
    private WebView wView;
//    private static final String map_url="http://172.30.1.33:8080/";
    private static final String map_url="http://192.168.50.124:8080";
    private double lat;
    private double lon;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kakoactivity);
        wView =(WebView)findViewById(R.id.kakao);



        Log.d(TAG, "===============================");
        Intent intent = getIntent();
        String pg_token = intent.getStringExtra("pg_token");
        Log.d(TAG, "====================="+pg_token);

        WebSettings ws = wView.getSettings();
        ws.setJavaScriptEnabled(true);
        ws.setDomStorageEnabled(true);
        ws.setGeolocationDatabasePath(getFilesDir().getPath());
//        ws.setLoadWithOverviewMode(true);
        ws.setUseWideViewPort(true);
        ws.setSupportZoom(false);
//        ws.setBuiltInZoomControls(false);
//        ws.setCacheMode(WebSettings.LOAD_NO_CACHE);
        ws.setAppCacheEnabled(true);
        ws.setDatabaseEnabled(true);



        wView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url){
                view.loadUrl(url);
                return true;

            }
        });




        wView.addJavascriptInterface(new KakaoBridge(), "kakaopay");
        wView.loadUrl(map_url);


    }
    final class KakaoBridge{

        @JavascriptInterface
        public void PayWindow(String url){
           Log.d(TAG, url);//alert창으로 생각하셈
            wView.post(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(MainActivity.this , SubActivity.class);

                    intent.putExtra("url", url);
                    startActivity(intent);
                }
            });
        }


        @JavascriptInterface
        public void PayOk(){

        }

    }


//https://recipes4dev.tistory.com/147

}
