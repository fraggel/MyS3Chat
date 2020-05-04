package com.mys3soft.mys3chat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.mys3soft.mys3chat.Services.LocalUserService;

public class AppReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        //Toast.makeText(context, "started", Toast.LENGTH_LONG).show();
        /*Intent i = new Intent(context, Main2Activity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);*/
       context.startService(new Intent(context,AppService.class));
    }
}
