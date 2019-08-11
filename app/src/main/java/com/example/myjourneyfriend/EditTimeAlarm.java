package com.example.myjourneyfriend;

import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;


import static com.example.myjourneyfriend.MainActivity.alarmListesi;
import static com.example.myjourneyfriend.MainActivity.db_name;
import static com.example.myjourneyfriend.MainActivity.shared;
import static com.example.myjourneyfriend.MapsActivity.alarm_bilgi;
import static com.example.myjourneyfriend.MapsActivity.coord;



public class EditTimeAlarm extends AppCompatActivity {

    Toolbar toolbar;
    private Button next;
    static EditText bilgi;
    static String sayac;
    private FloatingActionButton music;
    final int req=12;
    public static TextView music_tone;
    public static Uri alarmtone;
    LinearLayout ringToneContainer;
    String alarmtitle;
    int alarmzamanları;
    int alarmId;
    //int alarmId;
    String bilgim;
    static int tıklama=0;
    double lat;
    double lon;
    static int is;
    boolean checked;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_time_alarm);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        bilgi=findViewById(R.id.bilgi);
        music=findViewById(R.id.add_music);
        next=findViewById(R.id.next);
        music_tone=findViewById(R.id.textView);
        Intent i=getIntent();

        music.setEnabled(false);

        lat=i.getDoubleExtra("lan",0);
        lon=i.getDoubleExtra("lon",0);
        alarmId=i.getIntExtra("alarmId",-1);
        checked=true;
        if(!music_tone.getText().toString().equals("") || !music_tone.getText().toString().equals(null) )
        {music_tone.setText(RingtoneManager.getRingtone(this,alarmtone).getTitle(this));}
        ringToneContainer=findViewById(R.id.alarm_ringtone_container);

        //alarm seçtirecem.Kapalı intent oluşturdum.
        ringToneContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tıklama++;
                is=view.getId();
                Intent i=new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
                //Hiçbir şey seçmediyse varsayılan ses seçilmiş olsun.
                //      i.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT,false);
                i.putExtra(RingtoneManager.EXTRA_RINGTONE_DEFAULT_URI, Settings.System.DEFAULT_ALARM_ALERT_URI);
                //Kullanıcı kendi istediği müzik ya da alarm seçebilir.
                i.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE,RingtoneManager.TYPE_ALL);
                startActivityForResult(i,req);


            }
        });
        if(alarmId != -1) {
            music_tone.setText(RingtoneManager.getRingtone(this, alarmtone).getTitle(this));
        } else {


        }



        shared = getApplicationContext()
                .getSharedPreferences(db_name, Context.MODE_PRIVATE);

        bilgim=shared.getString(alarmId + "_duration", null);
        if(bilgim!=null)
        {
            bilgi.setText(bilgim);
        }
        next.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View view) {
                shared = getApplicationContext()
                        .getSharedPreferences(db_name, Context.MODE_PRIVATE);
                if(bilgi.getText().toString().isEmpty() ) {
                    Toast.makeText(EditTimeAlarm.this,"Plz Enter all the data",Toast.LENGTH_LONG).show();
                }else {
                   dbEkleme();
                    Intent in = new Intent(EditTimeAlarm.this, SayacActivity.class);
                    //              in.putExtra("sayac", sayac);
                    in.putExtra("alarmId", alarmId);
                    startActivity(in);
                    overridePendingTransition(R.anim.anim_in, R.anim.anim_out);


                }
            }
        });

        FloatingActionButton fab = findViewById(R.id.add_music);
        fab.setEnabled(false);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                /*
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK) {
            switch(requestCode) {
                case req:
                    alarmtone=data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
                    music_tone.setText(RingtoneManager.getRingtone(this, alarmtone).getTitle(this));
                    break;

                default:
                    break;


            }

        }


    }
    public void dbEkleme() {
       JSONArray myalarm = new JSONArray();
        JSONObject Alarmsjo = new JSONObject();
        try {
            Alarmsjo.put("alarm_name", alarm_bilgi);  //sorun alarm_bilgi=null alıyor

        } catch (Exception e) {
            e.printStackTrace();
        }
        //alarmListesi.add(Alarmsjo);
        alarmListesi.set(alarmId, Alarmsjo);
        MainActivity.adapter.notifyDataSetChanged();
        shared = getApplicationContext()
                .getSharedPreferences(db_name, Context.MODE_PRIVATE);
        for (int i = 0; i < alarmListesi.size(); i++) {
            myalarm.put(alarmListesi.get(i));
        }
        if (!alarmListesi.isEmpty()) {
            shared.edit().putString(MainActivity.alarm_shared, myalarm.toString()).apply();
        } else {
            Toast.makeText(getApplicationContext(), "Alarm can not be added", Toast.LENGTH_LONG).show();
        }
        // HashSet<String>  set = new  HashSet<String> (alarms);

        shared.edit().putInt(alarmId + "_Id", alarmId).apply();
        //shared.edit().putStringSet("alarms", set).apply();
        sayac = bilgi.getText().toString();
        shared = getApplicationContext()
                .getSharedPreferences(db_name, Context.MODE_PRIVATE);
        //zamanları ekliyoruz...
        shared.edit().putString(alarmId + "_duration", sayac).apply();
        shared.edit().putString(alarmId + "_address", coord).apply();

    }


}