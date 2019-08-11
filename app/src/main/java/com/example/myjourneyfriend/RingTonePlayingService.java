package com.example.myjourneyfriend;


import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;


import static com.example.myjourneyfriend.MapsActivity.boylam;
import static com.example.myjourneyfriend.MapsActivity.enlem;
import static com.example.myjourneyfriend.SayacActivity.say;
//IntentService,Service'in bi subclass'ıdır.
//Servisten farkı işi bittikten sonra kendini kapatır.Tekrar zamanlayıcı çalışırsa bu servis devreye girecek
public class RingTonePlayingService extends IntentService {
    static NotificationManagerCompat notificationManager;
    static int Notification_Id=0;
    int back_ıd=1;
    final int request_code=1;
    public RingTonePlayingService() {
        super("RingToneService");
        //setIntentRedelivery(true);
        //Oluşturulan thread a verilen ismi üst sınıa gönderiyoruz.
    }




    //
    @Override
    protected void onHandleIntent(Intent ıntent) {
        //Ayarlanmışsa bu geçmiş yığınında yeni bir görevin başlangıcı olacaktır.

    }


    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("1", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent ıntent) {
        return null;
    }


    @Override
    public void onCreate() {
        super.onCreate();

    }

    //Farklı uygulamalarla bilgi paylaşılığı zaman kullanılır.
    //onStartCommand servis çağrıldığı zaman kullanılır.
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        try
        //Intent önceki aktiviteden gönderilen bilgiler için alarmDurumu vs.
        {
            createNotificationChannel();
            //    Intent intent1 = new Intent(this.getApplicationContext(),SayacActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            //    PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent1, 0);


                NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "1")
                        .setSmallIcon(R.drawable.ic_alarm)
                        .setContentTitle("Alarm Info !")
                        .setContentText("Time: ")
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText("Hey,my journey friend! Here is your Time Remainder : " + say.getText()))
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setDefaults(Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND)
                        .setOngoing(true)  // bu kalıcı bildirim sağlıyor...kenarda dursun.
                        .setAutoCancel(false)
                        .setUsesChronometer(true);
                notificationManager = NotificationManagerCompat.from(this);
                startForeground(back_ıd, builder.build());
                //setAutoCancel(false); Yani bildirime tıklasamda gitmeyecek... //****///



        }
        catch (Exception e)
        {
            e.printStackTrace();
            //Notification a tıklayınca uygulama hata veriyor.
        }
        return START_NOT_STICKY;
//return super.onStartCommand(intent,flag,startId); yollarsak == return START_STICKY;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onDestroy() {
        //  stopForeground(Service.STOP_FOREGROUND_REMOVE);
        super.onDestroy();

    }

}
