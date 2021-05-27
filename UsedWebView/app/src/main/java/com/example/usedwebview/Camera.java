package com.example.usedwebview;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;

public class Camera extends AppCompatActivity {

    private  Uri img_uri = null;
    public final static int FILECHOOSER_NORMAL_REQ_CODE = 0;
    public final static int FILECHOOSER_LOLLIPOP_REQ_CODE = 1;
    public ValueCallback<Uri> filePathCallbackNormal;
    public ValueCallback<Uri[]> filePathCallbackLollipop;



    public Camera (){

    }
    public void onCamera(boolean isCapture, Context context){

        Intent c_intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        File path = context.getExternalCacheDir();
        File file = null;


        try {
            file = File.createTempFile("ph_",".jpg", path);
        }catch (IOException e){
            e.printStackTrace();
        }

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            String package_name = context.getPackageName();
            img_uri = FileProvider.getUriForFile(context, package_name+".fileprovider",file);

        }else {
            img_uri = Uri.fromFile(file);
        }

        c_intent.putExtra(MediaStore.EXTRA_OUTPUT, img_uri);

        if(!isCapture){
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

        switch (requestCode)
        {
            case FILECHOOSER_NORMAL_REQ_CODE:
                if (resultCode == RESULT_OK)
                {
                    if (filePathCallbackNormal == null) return;
                    Uri result = (data == null || resultCode != RESULT_OK) ? null : data.getData();


                    filePathCallbackNormal.onReceiveValue(result);   //  onReceiveValue 로 파일 전송
                    filePathCallbackNormal = null;
                }
                break;
            case FILECHOOSER_LOLLIPOP_REQ_CODE: // 2002


                if (resultCode == RESULT_OK)
                {
                    if (filePathCallbackLollipop == null) return;
                    if (data == null)
                        data = new Intent();
                    if (data.getData() == null)
                        data.setData(img_uri);


                    filePathCallbackLollipop.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, data));

                    filePathCallbackLollipop = null;
                }
                else
                {

                    if (filePathCallbackLollipop != null)
                    {   //  resultCode에 RESULT_OK가 들어오지 않으면 null 처리하지 한다.(이렇게 하지 않으면 다음부터 input 태그를 클릭해도 반응하지 않음)

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

}
