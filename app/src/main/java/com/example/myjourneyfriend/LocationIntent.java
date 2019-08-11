package com.example.myjourneyfriend;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import static com.example.myjourneyfriend.MapsActivity.boylam;
import static com.example.myjourneyfriend.MapsActivity.enlem;
import static com.example.myjourneyfriend.MapsActivity.ln;
import static com.example.myjourneyfriend.MapsActivity.lt;
import static com.example.myjourneyfriend.SayacActivity.say;

public class LocationIntent extends IntentService {
    static NotificationManagerCompat notificationManager;
    static int Notification_Id=0;
    int back_ıd=1;
    final int request_code=1;
    public LocationIntent() {
        super("LocationIntent");
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
            String mesaj="Kalan mesafe :"+distance(lt, ln, enlem, boylam);
            Toast.makeText(getApplicationContext(),mesaj,Toast.LENGTH_LONG).show();



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

    private double distance(double lat1, double lng1, double lat2, double lng2) {

        double earthRadius = 3958.75; // in miles, change to 6371 for kilometer output

        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lng2-lng1);

        double sindLat = Math.sin(dLat / 2);
        double sindLng = Math.sin(dLng / 2);

        double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
                * Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2));

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

        double dist = earthRadius * c;

        return dist; // output distance, in MILES
    }

}
