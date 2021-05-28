package com.example.usedwebview;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.net.URISyntaxException;


public class Kakao extends Activity {

    private static final String TAG = "Tag";
    WebView kView;
    Context context;
    String k_url;
    String pg_token;





    @Override
    protected  void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kakao);
        kView = (WebView)findViewById(R.id.kakao);


        WebSettings ks = kView.getSettings();
        ks.setJavaScriptEnabled(true);
        ks.setDomStorageEnabled(true);
        ks.setGeolocationEnabled(true);
        ks.setGeolocationDatabasePath(getFilesDir().getPath());
        ks.setLoadWithOverviewMode(true);
        ks.setUseWideViewPort(true);
        ks.setUseWideViewPort(true);
        ks.setSupportZoom(false);
        ks.setBuiltInZoomControls(false);
        ks.setCacheMode(WebSettings.LOAD_NO_CACHE);
        ks.setDatabaseEnabled(true);
        ks.setAppCacheEnabled(true);

        Intent intent= getIntent();
        k_url = intent.getStringExtra("url");
        kView.loadUrl(k_url);


        kView.setWebViewClient(new WebViewClientClass());



    }

    private class WebViewClientClass extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            Log.d(TAG, "url" + url);
            if (url != null && url.startsWith("intent:")) {

                try {
                    Intent k_intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
                    Intent Package = getApplicationContext().getPackageManager().getLaunchIntentForPackage(k_intent.getPackage()); //com.kakao.talk

                    if (Package != null) {
                        view.getContext().startActivity(k_intent);
                    } else {
                        Intent m_intent = new Intent(Intent.ACTION_VIEW);
                        m_intent.setData(Uri.parse("maket://details?id=" + m_intent.getPackage()));
                        view.getContext().startActivity(m_intent);
                    }
                    return true;
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                }


                ////
            } else if (url != null && url.startsWith("market://")) {
                try {
                    Intent intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
                    if (intent != null) {
                        view.getContext().startActivity(intent);
                    }
                    return true;
                } catch (URISyntaxException e) {
                    Log.e(TAG, e.getMessage());
                }
                ////
            } else if (url != null && url.contains("pg_token")) {

                pg_token = url.substring(url.indexOf("pg_token=") + 9);
                Intent t_intent = new Intent();
                t_intent.putExtra("pg_token", pg_token);
                setResult(RESULT_OK, t_intent);
                finish();
                Log.d(TAG, pg_token);
            }
            ////

            view.loadUrl(url);
            return false;
        }
    }

}
