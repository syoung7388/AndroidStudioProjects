package com.example.usedwebview;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;

public class Main extends AppCompatActivity {

    public static final String TAG = "Tag";
    public WebView wView;
    //private  static final String used_url="http://192.168.50.124:8080";
    private  static final String used_url="http://172.30.1.33:8080";
    Context context;

    public final int permissionCode = 1997;
    WebSettings ws ;




    public ValueCallback<Uri[]> filePathCallbackLollipop;






    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        wView = (WebView)findViewById(R.id.used_main);
        Permission permission = new Permission(permissionCode, getApplicationContext(), this);
        permission.PermissionCheck();

        ws = wView.getSettings();
        ws.setJavaScriptEnabled(true);
        ws.setSupportMultipleWindows(true);
        ws.setJavaScriptCanOpenWindowsAutomatically(true);
        ws.setLoadWithOverviewMode(true);
        ws.setUseWideViewPort(true);
        ws.setSupportZoom(false);
        ws.setBuiltInZoomControls(true);
        ws.setGeolocationEnabled(true);
        ws.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        ws.setCacheMode(WebSettings.LOAD_NO_CACHE);
        ws.setDomStorageEnabled(true);
        wView.loadUrl(used_url);
        wView.setWebViewClient(new WebViewClientClass());
        wView.setWebChromeClient(new WebChromeClientClass());
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permission, @NonNull int[] grantResult){
        Log.d(TAG, "permission:"+permission);
        Log.d(TAG, "grantResult:"+grantResult);

        super.onRequestPermissionsResult(requestCode, permission, grantResult);

        if(requestCode == permissionCode){
            if(grantResult.length >0 ){
                for (int i= 0; i< grantResult.length; ++i){
                    if(grantResult[i] == PackageManager.PERMISSION_DENIED){
                        Log.d(TAG, "result:"+grantResult[i]);
                        Log.d(TAG, "i:"+i);


                        new AlertDialog.Builder(this).setTitle("알림").setMessage("앱을 사용하기 위해서는 권한 허용을 필수 입니다.")
                                .setPositiveButton("종료", new DialogInterface.OnClickListener(){
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        finish();
                                    }
                                }).setNegativeButton("권한설정", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                intent.setData(Uri.parse("pakage:"+getApplicationContext().getPackageName()));
                                getApplicationContext().startActivity(intent);
                            }
                        }).setCancelable(false).show();
                        return;
                    }
                }
            }


        }
    }

    private class WebChromeClientClass extends WebChromeClient{

        @Override
        public boolean onJsAlert(WebView view,String url, String message, final JsResult result){
            new AlertDialog.Builder(view.getContext())
                    .setTitle("Alert")
                    .setMessage(message)
                    .setPositiveButton(android.R.string.ok,
                            new AlertDialog.OnClickListener(){
                                public void onClick(DialogInterface dialog, int which){
                                    result.confirm();
                                }

                    })
                    .setCancelable(false)
                    .create()
                    .show();
            return true;
        }

        @Override
        public boolean onJsConfirm(WebView view, String url, String message,
                                   final JsResult result) {
            Log.d(TAG , "onJsConfirm");
            new androidx.appcompat.app.AlertDialog.Builder(view.getContext())
                    .setTitle("Confirm")
                    .setMessage(message)
                    .setPositiveButton("yes",
                            new androidx.appcompat.app.AlertDialog.OnClickListener(){
                                public void onClick(DialogInterface dialog, int which) {
                                    result.confirm();
                                }
                            })
                    .setNegativeButton("no",
                            new androidx.appcompat.app.AlertDialog.OnClickListener(){
                                public void onClick(DialogInterface dialog, int which) {
                                    result.cancel();
                                }
                            })
                    .setCancelable(false)
                    .create()
                    .show();
            return true;
        }




        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        public boolean onShoeFileChooser(
                WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParam){
            if(filePathCallbackLollipop != null){
                filePathCallbackLollipop.onReceiveValue(null);
            }
            filePathCallbackLollipop = filePathCallback;

            boolean isCapture = fileChooserParam.isCaptureEnabled();
            Camera camera = new Camera();
            camera.onCamera(isCapture, context);

            return true;
        }

    }

    private class WebViewClientClass extends WebViewClient{


        @Override
        public boolean shouldOverrideUrlLoading(WebView webView, String url){
            webView.loadUrl(url);
            return true;
        }

    }





}
