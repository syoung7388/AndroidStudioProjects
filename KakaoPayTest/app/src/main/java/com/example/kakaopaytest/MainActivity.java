package com.example.kakaopaytest;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
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
    private static final String map_url="http://172.30.1.33:8080/";
    private double lat;
    private double lon;
    Context context;

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
        ws.setDatabaseEnabled(true);
        ws.setAppCacheEnabled(true);
        wView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url){
                if (url != null && url.startsWith("intent:")) {

                    try {
                        Intent intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);

                        Intent existPackage = getApplicationContext().getPackageManager().getLaunchIntentForPackage(intent.getPackage()); //com.kakao.talk

                        Log.d(TAG,"existPackage=>"+existPackage );// null
                        Log.d(TAG,"intent.getPackage()=>"+intent.getPackage() );
                        Log.d(TAG,"getApplicationContext().getPackageManager()=>"+getApplicationContext().getPackageManager() );
                        if (existPackage != null) {
                            view.getContext().startActivity(intent);
                        } else {
                            Intent marketIntent = new Intent(Intent.ACTION_VIEW);
                            marketIntent.setData(Uri.parse("market://details?id="+intent.getPackage()));
                            view.getContext().startActivity(marketIntent);
                        }
                        return true;
                    }catch (Exception e) {

                    }
                } else if (url != null && url.startsWith("market://")) {
                    try {
                        Intent intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
                        if (intent != null) {
                            view.getContext().startActivity(intent);
                        }
                        return true;
                    } catch (URISyntaxException e) {
                    }
                }else if(url != null && url.contains("pg_token=")){
                    String pg_token = url.substring(url.indexOf("pg_token="+9));
                    Toast.makeText(context, pg_token,Toast.LENGTH_SHORT).show();
                    Log.d(TAG, pg_token);
                }
                view.loadUrl(url);
                return false;

            }
        });


        KakaoBridge bridge = new KakaoBridge(this, wView);
        wView.addJavascriptInterface(bridge, "kakaopay");
        wView.loadUrl(map_url);





    }

//    public void CallKakao(String url){
//        wView.loadUrl(url);
//
//    }


}
