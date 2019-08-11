package com.example.myjourneyfriend;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BildirimReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent ıntent) {
        Intent serviceIntent = new Intent(context,RingTonePlayingService.class);
        context.startService(serviceIntent);
    }
}
