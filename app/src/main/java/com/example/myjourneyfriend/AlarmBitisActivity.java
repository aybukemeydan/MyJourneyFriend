package com.example.myjourneyfriend;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import static com.example.myjourneyfriend.MapsActivity.alarm_bilgi;
import static com.example.myjourneyfriend.IntentServisim.mMediaPlayer;

public class AlarmBitisActivity extends AppCompatActivity {
    ImageView imageView;
    AnimationDrawable animationDrawable;
    Button button;
    TextView textView;
    public final String TAG = this.getClass().getSimpleName();

    //WakeLock ekranın açılmasını sağlar,gücü yönetir.
    private PowerManager.WakeLock mWakeLock;
    //   private MediaPlayer mPlayer;
    //1 dk içinde işlem yapılmıyorsa alarm ekranını kapat...
    private static final int WAKELOCK_TIMEOUT = 10 * 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_bitis);
        Handler handler=new Handler();
        //Alarm bitiş ekranım için animasyon
        imageView=findViewById(R.id.iv_saat);
        animationDrawable= (AnimationDrawable) imageView.getDrawable();
        animationDrawable.start();
        textView=findViewById(R.id.alarm_screen_name);
        textView.setText(alarm_bilgi);
        button=findViewById(R.id.alarm_finish_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMediaPlayer.stop();
                stopService(new Intent(AlarmBitisActivity.this,RingTonePlayingService.class));
                stopService(new Intent(AlarmBitisActivity.this,LocationIntent.class));
                stopService(new Intent(AlarmBitisActivity.this,IntentServisim.class));
                Intent i=new Intent(AlarmBitisActivity.this,MainActivity.class);
                startActivity(i);
                finish();
            }
        });

        //Ensure wakelock release
        //1 dk sonra flagları temizliyoruz....
        Runnable releaseWakelock = new Runnable() {

            @Override
            public void run() {
                //Ekrana flagler ekledim.
                //Ekranı açık tutmak için...
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
                //Window flag'ı.Bir pencere eklendiğinde ya da görünür hale geldiğinde window gösterildikten sonra
                //sistem ekranı açmak için güç yönetisinin kullanıcı etkinliğini uyandıracaktır.
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                //Bu window kullanıcı ona baktıkça ekranın açık ve parlak tutulması için
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
                //Ekranı kitlediğimizde de göstersin.
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
                //isHeld() çalışır durumda olup olmadığını veriyor.Yani wakeLock çalışır durumda değilse
                if (mWakeLock != null && mWakeLock.isHeld())
                { //Uyandırma kilidi alınmış ama henüz serbest bırakılmamışsa
                    mWakeLock.release(); //Serbest bırak
                }
            }
        };

        handler.postDelayed(releaseWakelock, WAKELOCK_TIMEOUT);


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mWakeLock != null && mWakeLock.isHeld())
        {
            //Uyandırma kilidi alınmış ama henüz serbest bırakılmamışsa
            mWakeLock.release(); //Serbest bırak
        }
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        //Window flag'ı.Bir pencere eklendiğinde ya da görünür hale geldiğinde window gösterildikten sonra
        //sistem ekranı açmak için güç yönetisinin kullanıcı etkinliğini uyandıracaktır.
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //Bu window kullanıcı ona baktıkça ekranın açık ve parlak tutulması için
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        //Ekranı kitlediğimizde de göstersin.
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
    }


    //onPause'da WakeLock aktif değilse serbest bırakıyoruz.



    @Override
    protected void onPause() {
        super.onPause();

        if (mWakeLock != null && mWakeLock.isHeld()) {
            mWakeLock.release();  //Serbest bırakılması==Ekranın tekrar karartırlması,tuş kilidinin devreye girmesi
        }
    }




    @SuppressWarnings("deprecation")
    @Override
    protected void onResume() {
        super.onResume();

        // Set the window to keep screen on
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);

        // Acquire wakelock
        PowerManager pm = (PowerManager) getApplicationContext().getSystemService(Context.POWER_SERVICE);
        if (mWakeLock == null) {
            //          Ekran açıldığında tuş kilidi açılsın.CPU ve ekran açık        //Keyboard kapalı,gerisi açık
            mWakeLock = pm.newWakeLock((PowerManager.FULL_WAKE_LOCK | PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP), TAG);
        }

        if (!mWakeLock.isHeld()) {
            mWakeLock.acquire();
            Log.i(TAG, "Wakelock aquired!!");
        }

    }


}




