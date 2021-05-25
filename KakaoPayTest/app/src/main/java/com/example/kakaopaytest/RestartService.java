package com.example.kakaopaytest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class RestartService extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent){
        if(intent.getAction().equals("ACTION.RESTART.Sub")){
            Intent i = new Intent(context, SubActivity.class);
            context.startActivity(i);
        }
    }
}
