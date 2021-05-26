package com.example.kakaopaytest;

import android.content.Context;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
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
    //private static final String map_url="http://172.30.1.33:8080/";
    private static final String map_url="http://192.168.50.124:8080";
    private double lat;
    private double lon;
    Context context;
    private static final int maincode = 101;
    private String pg_token;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kakoactivity);
        wView =(WebView)findViewById(R.id.kakao);




        WebSettings ws = wView.getSettings();
        ws.setJavaScriptEnabled(true);
        ws.setDomStorageEnabled(true);
        ws.setGeolocationDatabasePath(getFilesDir().getPath());
        ws.setLoadWithOverviewMode(true);
        ws.setUseWideViewPort(true);
        ws.setSupportZoom(false);
        ws.setBuiltInZoomControls(false);
        ws.setCacheMode(WebSettings.LOAD_NO_CACHE);
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


    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == maincode){
            Log.d(TAG, "Main 도착");
            if(resultCode == RESULT_OK){
                pg_token = data.getStringExtra("pg_token");
                Log.d(TAG, "pg_token=>"+pg_token);
                wView.post(new Runnable() {
                    @Override
                    public void run() {
                        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
                            wView.evaluateJavascript("Token_Ok('"+pg_token+"')",null);
                        }else {
                            wView.loadUrl("javascript:Token_Ok('"+pg_token+"')");
                        }
                    }
                });

            }else {
                Log.d(TAG, "오류발생");
            }
        }
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
                    startActivityForResult(intent, maincode);
                }
            });
        }
    }



//https://recipes4dev.tistory.com/147

}
