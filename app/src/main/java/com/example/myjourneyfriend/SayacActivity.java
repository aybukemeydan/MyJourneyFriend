package com.example.myjourneyfriend;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.PowerManager;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import static com.example.myjourneyfriend.EditTimeAlarm.sayac;
import static com.example.myjourneyfriend.MainActivity.db_name;
import static com.example.myjourneyfriend.MainActivity.shared;
import static com.example.myjourneyfriend.MapsActivity.boylam;
import static com.example.myjourneyfriend.MapsActivity.coord;
import static com.example.myjourneyfriend.MapsActivity.enlem;
import static com.example.myjourneyfriend.RingTonePlayingService.Notification_Id;
import static com.example.myjourneyfriend.RingTonePlayingService.notificationManager;


public class SayacActivity extends AppCompatActivity {
    static TextView say;
    int alarmId;
    int time;
    private PendingIntent pending_intent;
    private PendingIntent pending_intent2;
    private PendingIntent location_pending_intent;
    AlarmManager alarmManager;
    Context context=this;
    Handler handler=new Handler();
    private static final int WAKELOCK_TIMEOUT = 10 * 1000;
    public final String TAG = this.getClass().getSimpleName();
     private PowerManager.WakeLock mWakeLock;
    CountDownTimer c;

    //servisleri geziyoruz.Çalışıyolar mı diye
    public boolean isRunningService() {
        ActivityManager servicemanager= (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for(ActivityManager.RunningServiceInfo servis : servicemanager.getRunningServices(Integer.MAX_VALUE))
        {
            if(context.getPackageName().equals(servis.service.getPackageName()))
            {
                return true;
            }

        }
        return false;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sayac);

        say = findViewById(R.id.sayac);
        final Animation animation = AnimationUtils.loadAnimation(this, R.anim.translate);
        final Button translate = findViewById(R.id.başla);
        final Button iptal = findViewById(R.id.iptal);
        final Intent myMusicPlayerIntent = new Intent(this, AlarmReceiver.class);
        final Intent myNotificationIntent = new Intent(this, BildirimReceiver.class);
        final Intent mylocationIntent=new Intent(this,LocationReceiver.class);
        iptal.setEnabled(false);
        // Get the alarm manager service
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        // set the alarm to the time that you picked
        final Calendar calendar = Calendar.getInstance();
        Intent i = getIntent();
        // String sayac = i.getStringExtra("sayac");
        alarmId = i.getIntExtra("alarmId", -1);
        shared = getApplicationContext()
                .getSharedPreferences(db_name, Context.MODE_PRIVATE);
        shared.getString(alarmId+1 + "_start", null);




        if (alarmId != -1 && sayac!=null && !sayac.isEmpty()) {
            EditTimeAlarm.bilgi.setText(sayac);
        }
        time = Integer.parseInt(sayac);
        say.setText("Timer:" + time);
if(isRunningService())
{
    translate.setEnabled(false);
    Toast.makeText(getApplicationContext(),"You can't start until your journey is finished!",Toast.LENGTH_LONG).show();
}


        translate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                iptal.setEnabled(true);
                translate.setEnabled(false);
                //   translate.setVisibility(false);
                view.startAnimation(animation);

                 c=new CountDownTimer(time * 60000, 60000) {
                    public void onTick(long millisUntilFinished) {

                        say.setText("" + String.format("%d Dakika, %d Saniye",
                                TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
                                TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                                        TimeUnit.MINUTES.toSeconds
                                                (TimeUnit.MILLISECONDS.
                                                toMinutes(millisUntilFinished))
                        ));

                        calendar.add(Calendar.SECOND, 1);
                    //    eğer geldiyse direk geldin mesajı verip alarm çalsın...
                        if (MapsActivity.geldinMi==true ) {
                           location_pending_intent=PendingIntent.getBroadcast(SayacActivity.this,12,mylocationIntent,PendingIntent.FLAG_UPDATE_CURRENT);
                              alarmManager.set(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),location_pending_intent);
                            pending_intent = PendingIntent.getBroadcast(SayacActivity.this, 0, myMusicPlayerIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pending_intent);
                            onFinish();
                          //  time=0;
                        }
                        else{
                        pending_intent2 = PendingIntent.getBroadcast(SayacActivity.this, 0, myNotificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pending_intent2);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            startForegroundService(myNotificationIntent);

                        } }

                    }


                    public void onFinish() {
                        // stopService(my2Intent);
                        say.setText("Time is over!");
                        notificationManager.cancel(Notification_Id);
                        calendar.add(Calendar.SECOND, 1);
                        //setAlarmText("You clicked a button");
                        myMusicPlayerIntent.putExtra("extra", "yes");

                        pending_intent = PendingIntent.getBroadcast(SayacActivity.this, 0, myMusicPlayerIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pending_intent);
                    }
                }.start();
            }
        });

        iptal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                c.onFinish();
            }
        });

    }

}
