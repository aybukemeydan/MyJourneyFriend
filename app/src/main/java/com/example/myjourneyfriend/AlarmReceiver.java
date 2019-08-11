package com.example.myjourneyfriend;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

//Alarmın çaldığını dinliyor ve RingToneService'e zamanı gelince iletiyor...
public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent ıntent) {


        Intent serviceIntent = new Intent(context,IntentServisim.class);
        context.startService(serviceIntent);

}}

