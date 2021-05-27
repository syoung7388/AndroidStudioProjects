package com.example.usedwebview;


import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;

public class Main extends AppCompatActivity {

    public static final String TAG = "Tag";
    public WebView wView;
    private  static final String used_url="http://192.168.50.124:8080";
    //private  static final String used_url="http://172.30.1.33:8080";
    Context context;

    public final int permissionCode = 1997;
    WebSettings ws ;

    private  Uri img_uri = null;
    private final static int FILECHOOSER_NORMAL_REQ_CODE = 2001;
    private final static int FILECHOOSER_LOLLIPOP_REQ_CODE = 2002;
    private ValueCallback<Uri> filePathCallbackNormal;
    private ValueCallback<Uri[]> filePathCallbackLollipop;






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

        if(Build.VERSION.SDK_INT >= 21) {
            ws.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        wView.loadUrl(used_url);
        wView.setWebChromeClient(new WebChromeClientClass());
        wView.setWebViewClient(new WebViewClientClass());

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


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if((keyCode == KeyEvent.KEYCODE_BACK) && wView.canGoBack()){
            wView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private class WebChromeClientClass extends WebChromeClient{

        @Override
        public boolean onJsAlert(WebView view,String url, String message, final JsResult result){
            Log.d(TAG,"WebChromeClientClass");
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
            new AlertDialog.Builder(view.getContext())
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


        @SuppressLint("LongLogTag")
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        public boolean onShowFileChooser(
                WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams){
            Log.d(TAG,"isCapture");

            if(filePathCallbackLollipop != null){
                filePathCallbackLollipop.onReceiveValue(null);
            }
            filePathCallbackLollipop = filePathCallback;

            boolean isCapture = fileChooserParams.isCaptureEnabled();
            Log.d(TAG,"isCapture");
            runCamera(isCapture);

            return true;
        }

    }
    private void runCamera(boolean isCapture){


        Log.d(TAG,"onCamera");

        Intent c_intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        File path = getExternalCacheDir();
        File file = null;


        try {
            file = File.createTempFile("ph_",".jpg", path);
        }catch (IOException e){
            e.printStackTrace();
        }

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            String strpa =  getApplicationContext().getPackageName();
            img_uri = FileProvider.getUriForFile(this, strpa+".fileprovider",file);

        }else {
            img_uri = Uri.fromFile(file);
        }

        c_intent.putExtra(MediaStore.EXTRA_OUTPUT, img_uri);

        if(!isCapture){
            Log.d(TAG , "!_isCaptur=>"+"!_isCaptur:"+img_uri);
            Intent pickIntent = new Intent(Intent.ACTION_PICK);
            pickIntent.setType(MediaStore.Images.Media.CONTENT_TYPE);
            pickIntent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

            String pickTitle = "사진 가져올 방법을 선택해 주세요.";
            Intent chooserIntent = Intent.createChooser(pickIntent, pickTitle);
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Parcelable[]{c_intent});

            startActivityForResult(chooserIntent, FILECHOOSER_LOLLIPOP_REQ_CODE);
        }else {
            startActivityForResult(c_intent, FILECHOOSER_NORMAL_REQ_CODE);
        }

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case FILECHOOSER_NORMAL_REQ_CODE:
                if (resultCode == RESULT_OK){
                    if (filePathCallbackNormal == null) return;
                    Uri result = (data == null || resultCode != RESULT_OK) ? null : data.getData();

                    Log.d(TAG, "result:"+ result);
                    filePathCallbackNormal.onReceiveValue(result);   //  onReceiveValue 로 파일 전송 // input 태그로
                    filePathCallbackNormal = null;
                }
                break;
            case FILECHOOSER_LOLLIPOP_REQ_CODE: // 2002


                if (resultCode == RESULT_OK) {
                    if (filePathCallbackLollipop == null) return;
                    if (data == null)
                        data = new Intent();
                    if (data.getData() == null)
                        data.setData(img_uri);


                    filePathCallbackLollipop.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, data));
                    Log.d(TAG, "data:"+ data);

                    filePathCallbackLollipop = null;
                }
                else {

                    if (filePathCallbackLollipop != null) {   //  resultCode에 RESULT_OK가 들어오지 않으면 null 처리하지 한다.(이렇게 하지 않으면 다음부터 input 태그를 클릭해도 반응하지 않음)

                        filePathCallbackLollipop.onReceiveValue(null);
                        filePathCallbackLollipop = null;
                    }

                    if (filePathCallbackNormal != null) {
                        filePathCallbackNormal.onReceiveValue(null);
                        filePathCallbackNormal = null;
                    }
                }
                break;
            default:

                break;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }





    private class WebViewClientClass extends WebViewClient{


        @Override
        public boolean shouldOverrideUrlLoading(WebView webView, String url){
            webView.loadUrl(url);
            return true;
        }

    }





}
