package com.example.myjourneyfriend;

import android.app.IntentService;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;

import androidx.annotation.Nullable;

import static com.example.myjourneyfriend.EditTimeAlarm.alarmtone;
import static com.example.myjourneyfriend.EditTimeAlarm.music_tone;
import static com.example.myjourneyfriend.EditTimeAlarm.tıklama;
public class IntentServisim extends IntentService {
    public static final String TAG=" IntentServisim";
    static MediaPlayer mMediaPlayer;
    String tone;
    public IntentServisim() {
        super(TAG);
    }

    @Override
    public void onCreate() {
        tone=music_tone.getText().toString();

        super.onCreate();
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {

        //    mMediaPlayer = MediaPlayer.create(this, R.raw.livin);
        mMediaPlayer= new MediaPlayer();
        try {
            if ((!tone.equals("Sessiz") || !tone.equals(null)) && tıklama>0 ) {
                //    Uri toneUri = Uri.parse(tone);
                if (alarmtone != null  ) {
                    mMediaPlayer.setDataSource(this, alarmtone);
                    mMediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
                    mMediaPlayer.setLooping(true);
                    mMediaPlayer.prepare();
                    mMediaPlayer.start();
                }
            }
            else {
                mMediaPlayer = MediaPlayer.create(this, R.raw.livin);
                mMediaPlayer.start();}
        } catch (Exception e) {
            e.printStackTrace();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected void onHandleIntent(Intent ıntent) {
        Intent alarmIntent=new Intent(this,AlarmBitisActivity.class);
        alarmIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        alarmIntent.putExtras(ıntent);
        startActivity(alarmIntent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}

