package com.example.myjourneyfriend;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class LocationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent ıntent) {


        Intent serviceIntent = new Intent(context,LocationIntent.class);
        context.startService(serviceIntent);


    }
}

