package com.example.maptest;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.webkit.GeolocationPermissions;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.util.Log;
import android.content.Context;



public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MyTag";
    private WebView wView;
    private static final String map_url="http://192.168.50.124:8080";
    private double lat;
    private double lon;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_activity);
        wView = (WebView)findViewById(R.id.map);
        permissionCheck();
    }
    private void permissionCheck(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED){
            Log.d(TAG,"PcheckOK");
            inMapView();
        }else {
            ActivityCompat.requestPermissions(this, new String[]
                    {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 0);
            Log.d(TAG,"PcheckNo");
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permission, @NonNull int[] grantResult){
        Log.d(TAG,"requestCode:"+requestCode);
        super.onRequestPermissionsResult(requestCode, permission, grantResult);

        if(requestCode == 0){
            inMapView();
        }
    }
    public void inMapView(){
        Log.d(TAG,"inMapView:");

        WebSettings ws = wView.getSettings();
        ws.setJavaScriptEnabled(true);
        ws.setDomStorageEnabled(true);
        ws.setGeolocationEnabled(true);
        ws.setGeolocationDatabasePath(getFilesDir().getPath());
        ws.setLoadWithOverviewMode(true);
        ws.setUseWideViewPort(true);
        ws.setSupportZoom(false);
        ws.setBuiltInZoomControls(false);
        ws.setCacheMode(WebSettings.LOAD_NO_CACHE);
        ws.setDatabaseEnabled(true);
        ws.setAppCacheEnabled(true);
        wView.setWebViewClient(new WebViewClient());
        Log.d(TAG,"inMapView:");
        wView.setWebChromeClient(new WebChromeClient());
        wView.loadUrl(map_url);

    }

    private class WebChromeClientClass extends WebChromeClient{
        @Override
        public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback){
            Log.d(TAG,"WebChromeClientClass:");
            super.onGeolocationPermissionsShowPrompt(origin, callback);
            Log.d(TAG,"WebChromeClientClass:");
            callback.invoke(origin, true, false);
        }

    }

}
