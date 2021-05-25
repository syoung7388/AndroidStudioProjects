package com.example.kakaopaytest;

import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import java.io.EOFException;
import java.net.URISyntaxException;

public class SubActivity extends AppCompatActivity {
    private static final String TAG = "MyTag";
    WebView sView;
    Context context;
    String kakaourl;
    String pg_token;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub);
        sView = (WebView)findViewById(R.id.sub);


        WebSettings sws = sView.getSettings();
        sws.setJavaScriptEnabled(true);
        sws.setDomStorageEnabled(true);
        sws.setGeolocationDatabasePath(getFilesDir().getPath());
        sws.setLoadWithOverviewMode(true);
        sws.setUseWideViewPort(true);
        sws.setSupportZoom(false);
        sws.setBuiltInZoomControls(false);
        sws.setCacheMode(WebSettings.LOAD_NO_CACHE);
        sws.setDatabaseEnabled(true);
        sws.setAppCacheEnabled(true);

        Intent intent = getIntent();
        kakaourl = intent.getStringExtra("url");
        sView.loadUrl(kakaourl);


        //Kakao app 있는지 여부 확인 // shouldOverrideUrlLoading url 접속시 마다 발생
        sView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url){

                Log.d(TAG, "url"+url);
                if (url != null && url.startsWith("intent:")) {

                    try {
                        Intent k_intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
                        Intent Package = getApplicationContext().getPackageManager().getLaunchIntentForPackage(k_intent.getPackage()); //com.kakao.talk

                        if(Package != null){
                            view.getContext().startActivity(k_intent);
                        }else {
                            Intent m_intent = new Intent(Intent.ACTION_VIEW);
                            m_intent.setData(Uri.parse("maket://details?id="+m_intent.getPackage()));
                            view.getContext().startActivity(m_intent);
                        }
                        return true;
                    }catch (Exception e){
                        Log.e(TAG, e.getMessage());
                    }


                    ////
                }else if(url != null && url.startsWith("market://")){
                    try {
                        Intent intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
                        if(intent != null){
                            view.getContext().startActivity(intent);
                        }
                        return true;
                    }catch (URISyntaxException e){
                        Log.e(TAG, e.getMessage());
                    }
                    ////
                }else if (url != null && url.contains("pg_token")){

                    pg_token = url.substring(url.indexOf("pg_token=")+9);
                    Intent t_intent = new Intent(SubActivity.this, MainActivity.class);
                    t_intent.putExtra("pg_token",pg_token);
                    startActivity(t_intent);
                    Log.d(TAG, pg_token);
                }
                ////

                view.loadUrl(url);
                return false;
            }
        });


    }
}