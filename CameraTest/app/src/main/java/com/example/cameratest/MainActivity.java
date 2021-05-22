package com.example.cameratest;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private static final String Tag = "MyTag";

    private WebView webView1;
    private WebSettings webSettings;
    private long time = 0;

    public ValueCallback<Uri> filePathCallbackNormal;
    public ValueCallback<Uri[]> filePathCallbackLollipop;
    public final static int FILECHOOSER_NORMAL_REQ_CODE = 2001;
    public final static int FILECHOOSER_LOLLIPOP_REQ_CODE = 2002;
    private Uri cameraImageUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        webView1 = (WebView) findViewById(R.id.webView1);

        checkVerify();

        webSettings = webView1.getSettings();
        webSettings.setJavaScriptEnabled(true);         // 자바스크립트 사용
        webSettings.setSupportMultipleWindows(true);    // 새창 띄우기 허용
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true); // 자바스크립트 새창 띄우기 허용
        webSettings.setLoadWithOverviewMode(true);      // 메타태그 허용
        webSettings.setUseWideViewPort(true);           // 화면 사이즈 맞추기 허용
        webSettings.setSupportZoom(false);              // 화면줌 허용 여부
        webSettings.setBuiltInZoomControls(false);      // 화면 확대 축소 허용 여부
//        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN); // 컨텐츠 사이즈 맞추기
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);        // 브라우저 노캐쉬
        webSettings.setDomStorageEnabled(true);                     // 로컬저장소 허용
        webView1.loadUrl("http://172.30.1.55:8080");
        webView1.setWebChromeClient(new WebChromeClientClass());  //웹뷰에 크롬 사용 허용. 이 부분이 없으면 크롬에서 alert가 뜨지 않음
        webView1.setWebViewClient(new WebViewClientClass());
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        if(System.currentTimeMillis()-time>=2000){
            time=System.currentTimeMillis();
            Toast.makeText(getApplicationContext(),"뒤로 버튼을 한번 더 누르면 종료합니다.",Toast.LENGTH_SHORT).show();
        }else if(System.currentTimeMillis()-time<2000){
            finish();
            return;
        }
    }


    @TargetApi(Build.VERSION_CODES.M)
    public void checkVerify() {


        if (ContextCompat.checkSelfPermission(this,Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this,Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this,Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {


            //카메라 또는 저장공간 권한 획득 여부 확인
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.WRITE_EXTERNAL_STORAGE) || ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.CAMERA)) {

                Toast.makeText(getApplicationContext(),"권한 관련 요청을 허용해 주셔야 카메라 캡처이미지 사용등의 서비스를 이용가능합니다.",Toast.LENGTH_SHORT).show();

            } else {
                Log.d(Tag,"checkVerify()");
                // 카메라 및 저장공간 권한 요청
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.INTERNET, Manifest.permission.CAMERA,
                        Manifest.permission.ACCESS_NETWORK_STATE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            }
        }
    }

    //권한 획득 여부에 따른 결과 반환
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d(Tag,"onRequestPermissionsResult=>requestCode:"+requestCode+"&&& permissions:"+permissions+"&&& grantResults:"+grantResults);

        if (requestCode == 1)
        {
            if (grantResults.length > 0)
            {
                for (int i=0; i<grantResults.length; ++i)
                {
                    if (grantResults[i] == PackageManager.PERMISSION_DENIED)
                    {
                        Log.d(Tag ,"grantResults[X]:"+grantResults[i]);
                        // 카메라, 저장소 중 하나라도 거부한다면 앱실행 불가 메세지 띄움

                        new AlertDialog.Builder(this).setTitle("알림").setMessage("권한을 허용해주셔야 앱을 이용할 수 있습니다.")
                                .setPositiveButton("종료", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        finish();
                                    }
                                }).setNegativeButton("권한 설정", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                        .setData(Uri.parse("package:" + getApplicationContext().getPackageName()));
                                getApplicationContext().startActivity(intent);
                            }
                        }).setCancelable(false).show();

                        return;
                    }
                    Log.d(Tag ,"grantResults[O]:"+grantResults[i]);
                }


            }
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(Tag,"onActivityResult=> resultCode: "+resultCode+"&&&requestCode"+requestCode+"&&& data :"+data);

        switch (requestCode)
        {
            case FILECHOOSER_NORMAL_REQ_CODE:
                if (resultCode == RESULT_OK)
                {
                    if (filePathCallbackNormal == null) return;
                    Uri result = (data == null || resultCode != RESULT_OK) ? null : data.getData();
                    Log.d(Tag,"onActivityResult"+result);

                    filePathCallbackNormal.onReceiveValue(result);   //  onReceiveValue 로 파일 전송
                    filePathCallbackNormal = null;
                }
                break;
            case FILECHOOSER_LOLLIPOP_REQ_CODE: // 2002
                Log.d(Tag,"onActivityResult=>resultCode:"+resultCode);


                if (resultCode == RESULT_OK)
                {
                    if (filePathCallbackLollipop == null) return;
                    if (data == null)
                        data = new Intent();
                    if (data.getData() == null)
                        data.setData(cameraImageUri);
                    Log.d(Tag,"onActivityResult=>data:"+data);

                    filePathCallbackLollipop.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, data));

                    filePathCallbackLollipop = null;
                }
                else
                {

                    if (filePathCallbackLollipop != null)
                    {   //  resultCode에 RESULT_OK가 들어오지 않으면 null 처리하지 한다.(이렇게 하지 않으면 다음부터 input 태그를 클릭해도 반응하지 않음)

                        Log.d(Tag,"onActivityResult=> FILECHOOSER_LOLLIPOP_REQ_CODE /// filePathCallbackLollipop != null");
                        filePathCallbackLollipop.onReceiveValue(null);
                        filePathCallbackLollipop = null;
                    }

                    if (filePathCallbackNormal != null)
                    {
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

    //
    private void runCamera(boolean _isCapture)
    {
        Log.d(Tag , "runCamera=>"+"_isCapture:"+_isCapture);
        Intent intentCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        File path = Environment.getExternalStorageDirectory();
        Log.d(Tag , "runCamera=>"+"path:"+path);// path:/storage/emulated/0
        File file = new File(path, "aa.png"); // 저장될때  파일명
        Log.d(Tag , "runCamera=>"+"file:"+file); //file:/storage/emulated/0/sample.png
        // File 객체의 URI 를 얻는다.


        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
        {
            String strpa = getApplicationContext().getPackageName(); // =>패키지명 : com.example.cameratest
            Log.d(Tag , "runCamera=>"+"strpa:"+strpa);
            cameraImageUri = FileProvider.getUriForFile(this, strpa + ".fileprovider", file);
            Log.d(Tag , "runCamera=>"+"cameraImageUri:"+cameraImageUri); // =>최종 uri  content://com.example.cameratest.fileprovider/sdcard/sample.png
        }
        else
        {
            cameraImageUri = Uri.fromFile(file);
            Log.d(Tag , "runCamera=>"+"cameraImageUri:"+cameraImageUri);

        }


        intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, cameraImageUri);

        if (!_isCapture) // 카메라 갤러리 둘다
        {
            Log.d(Tag , "!_isCaptur=>"+"!_isCaptur:"+cameraImageUri);
            Intent pickIntent = new Intent(Intent.ACTION_PICK);
            pickIntent.setType(MediaStore.Images.Media.CONTENT_TYPE);
            pickIntent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

            String pickTitle = "사진 가져올 방법을 선택해 주세요.";
            Intent chooserIntent = Intent.createChooser(pickIntent, pickTitle);
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Parcelable[]{intentCamera}); // =>선택창 띄우기

            startActivityForResult(chooserIntent, FILECHOOSER_LOLLIPOP_REQ_CODE);
        }
        else
        {
            Log.d(Tag , "_isCaptur=>"+"_isCaptur:"+cameraImageUri);
            startActivityForResult(intentCamera, FILECHOOSER_LOLLIPOP_REQ_CODE); // => 바로 카메라 실행함
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if ((keyCode==KeyEvent.KEYCODE_BACK) && webView1.canGoBack()) {
            webView1.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private class WebChromeClientClass extends WebChromeClient {
        // 자바스크립트의 alert창


        @Override
        public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {

            Log.d(Tag , "onJsAlert");

            new AlertDialog.Builder(view.getContext())
                    .setTitle("Alert")
                    .setMessage(message)
                    .setPositiveButton(android.R.string.ok,
                            new AlertDialog.OnClickListener(){
                                public void onClick(DialogInterface dialog, int which) {
                                    result.confirm();
                                }
                            })
                    .setCancelable(false)
                    .create()
                    .show();
            return true;
        }

        // 자바스크립트의 confirm창
        @Override
        public boolean onJsConfirm(WebView view, String url, String message,
                                   final JsResult result) {
            Log.d(Tag , "onJsConfirm");
            new AlertDialog.Builder(view.getContext())
                    .setTitle("Confirm")
                    .setMessage(message)
                    .setPositiveButton("yes",
                            new AlertDialog.OnClickListener(){
                                public void onClick(DialogInterface dialog, int which) {
                                    result.confirm();
                                }
                            })
                    .setNegativeButton("no",
                            new AlertDialog.OnClickListener(){
                                public void onClick(DialogInterface dialog, int which) {
                                    result.cancel();
                                }
                            })
                    .setCancelable(false)
                    .create()
                    .show();
            return true;
        }




        // For Android 5.0+ 카메라 - <input type="file"/> 태그 선택 했을때 작동
        @SuppressLint("LongLogTag")
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        public boolean onShowFileChooser(
                WebView webView, ValueCallback<Uri[]> filePathCallback,
                FileChooserParams fileChooserParams) {
//            Log.d(Tag , "onShowFileChooser=>webView:"+webView+"&&& filePathCallback:"+filePathCallback+"&&& fileChooserParams:"+fileChooserParams);

           //널값을 넣어 줌으로써 변수 초기화
            if (filePathCallbackLollipop != null) {
                filePathCallbackLollipop.onReceiveValue(null);
//                filePathCallbackLollipop = null;
            }
            filePathCallbackLollipop = filePathCallback;
//            Log.d(Tag , "onShowFileChooser=>filePathCallbackLollipop:"+filePathCallbackLollipop);

            boolean isCapture = fileChooserParams.isCaptureEnabled();
            Log.d(Tag , "onShowFileChooser=>isCapture:"+ isCapture);

            runCamera(isCapture);
            return true;
        }
    }

    private class WebViewClientClass extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view,String url) {
            Log.d(Tag , "WebViewClientClass");

            view.loadUrl(url);
            return true;
        }
    }
}







