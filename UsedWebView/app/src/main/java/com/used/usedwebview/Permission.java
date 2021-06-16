package com.used.usedwebview;
import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Camera;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


public class Permission {
    public static final String TAG = "Tag";
    public int permissionCode;
    Context context;
    Activity activity;

    public Permission(int p_code, Context context, Activity activity){
        this.permissionCode = p_code;
        this.context = context;
        this.activity = activity;
    }



    public void PermissionCheck(){
        Log.d(TAG, "permissionCode:"+permissionCode);
        if(ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS)!= PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_NETWORK_STATE)!= PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED
            ||ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ){

//            if(ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                                                                    ||ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.CAMERA)){
//                Toast.makeText(context, "권한 관련 요청을 해주셔야 카메라를 이용할 수 있습니다.", Toast.LENGTH_SHORT).show();
//            }else {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.INTERNET,
                        Manifest.permission.CAMERA, Manifest.permission.ACCESS_NETWORK_STATE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE,  Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                }, permissionCode);

           // }

        }
    }



    public void CameraResult(int requestCode, @NonNull String[] permission, @NonNull int[] grantResult){



    }



}
