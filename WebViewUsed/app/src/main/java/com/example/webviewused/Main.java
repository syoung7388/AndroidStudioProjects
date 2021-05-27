package com.example.webviewused;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class Main extends AppCompatActivity {

    public static final String TAG = "Tag";
    public WebView wView;
    //private  static final String used_url="http://192.168.50.124:8080";
    private  static final String used_url="http://172.30.1.33:8080";
    Context context;

    public final int CameraCode = 1997;
    public final int MapCode= 1998;





    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);
        wView = (WebView)findViewById(R.id.used_main);
        Permission permission = new Permission(CameraCode, MapCode, getApplicationContext(), this);
        permission.MapCheck();
        permission.CameraCheck();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permission, @NonNull int[] grantResult){
        Log.d(TAG, "permission:"+permission);
        Log.d(TAG, "grantResult:"+grantResult);

        if(requestCode == CameraCode){
            if(grantResult.length >0 ){
                for (int i= 0; i< grantResult.length; ++i){
                    if(grantResult[i] == PackageManager.PERMISSION_DENIED){
                        Log.d(TAG, "result:"+grantResult[i]);
                        Log.d(TAG, "i:"+i);

                        new AlertDialog.Builder(context).setTitle("알림").setMessage("앱을 사용하기 위해서는 권한 허용을 필수 입니다.")
                                .setPositiveButton("종료", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        finish();
                                    }
                                }).setNegativeButton("권한설정", new DialogInterface.OnClickListener() {
                                    @Override
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



        super.onRequestPermissionsResult(requestCode, permission, grantResult);
    }




}
