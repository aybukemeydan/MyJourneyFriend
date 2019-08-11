package com.example.myjourneyfriend;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import static com.example.myjourneyfriend.MainActivity.alarmListesi;
import static com.example.myjourneyfriend.MainActivity.db_name;
import static com.example.myjourneyfriend.MainActivity.shared;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener {
    int back_ıd=4;
    AlarmManager alarmManager;
    private GoogleMap mMap;
    private PendingIntent location_pending_intent;
    int alarmId;
    Button btn_go;
    EditText editText;
    private PowerManager.WakeLock mWakeLock;
    static String alarm_bilgi;
    final int requestcode=13;
    LocationManager locationManager;
    LocationListener locationListener;
    String address="";
    Handler handler=new Handler();
    final String db="gecici_db";
    //Gideceği konumun bilgilerini aldım.
    double latitude;
    double longtitude;
    String kayıtlıyer;
    static  String coord;
    static double enlem;
    static double boylam;
     double kayıtlılatitude;
     double kayıtlılongtitude;
    private static final int WAKELOCK_TIMEOUT = 10 * 1000;
    public final String TAG = this.getClass().getSimpleName();
   // static JSONArray myalarm;
    String kayıtlıbilgi;
    int  tıktık=0;
   static boolean geldinMi;
    static double lt;
    static double ln;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        editText = findViewById(R.id.et_location);
        btn_go=findViewById(R.id.btn_Go);
         Toast.makeText(this,"if you want to choose your location,Plz click long on map.",Toast.LENGTH_LONG).show();
///kayıtlı bilgiyi getir.


        Intent intent = getIntent();
        alarmId = intent.getIntExtra("alarmId", -1);

        if (alarmId != -1) {

            try {
                editText.setText(alarmListesi.get(alarmId).getString("alarm_name"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            //yeni alarm

            JSONObject baska=new JSONObject();

            try{
                baska.put("alarm_name",""); //sorun alarm_bilgi=null alıyor
                MainActivity.alarmListesi.add(baska);
                MainActivity.adapter.notifyDataSetChanged();
                alarmId=alarmListesi.size()-1;  }
            catch(Exception e) {
                e.printStackTrace();
            }


        }


        btn_go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(address.equals(null) && coord.equals("") && tıktık<1) {
                    Toast.makeText(MapsActivity.this,"Rotanızı seçmelisiniz...",Toast.LENGTH_LONG).show();
                }else{
                    alarm_bilgi = editText.getText().toString();
                   geldinMi=vardınMı(enlem,boylam,lt,ln);
                    Intent i = new Intent(MapsActivity.this, EditTimeAlarm.class);
                    i.putExtra("alarmId", alarmId);
                    startActivity(i);
                }
            }
        });

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
alarmManager=(AlarmManager) getSystemService(ALARM_SERVICE);
        final Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.SECOND, 1);
        final Intent mylocationIntent=new Intent(this,LocationReceiver.class);
        mMap = googleMap;

        shared = getApplicationContext()
                .getSharedPreferences(db_name, Context.MODE_PRIVATE);
        //kayıtlı yer varsa işaretli olarak gelir.//////////////////////
        kayıtlıyer=shared.getString(alarmId+"_address", null);
        if(kayıtlıyer!=null)
        {
            String[] latlong =  kayıtlıyer.split(";");
            kayıtlılatitude = Double.parseDouble(latlong[0]);
            kayıtlılongtitude = Double.parseDouble(latlong[1]);
            LatLng kayıtlıLatLng=new LatLng(kayıtlılatitude,kayıtlılongtitude);

            mMap.addMarker(new MarkerOptions().position(kayıtlıLatLng).title("Rotan"));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(kayıtlıLatLng));

        }

//kullanıcının yeri değiştiğinde gözlemleyebiliyoruz artık.

        locationManager= (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener=new LocationListener() {
            @SuppressLint("InvalidWakeLockTag")
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onLocationChanged(Location location){

                //Kullanıcı ilk defa API kullanıyorsa ilk onLocationChanged'de kullanıcının lokasyonunu bulmamız gerekir.
                // Çözümüm:

                //kullanıcının yyeri değiştiğinde o yeri elde etme



                enlem=location.getLatitude();
                boylam=location.getLongitude();

            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override

            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };
        //Harita açıldığında olaylar olacak ondan dolayı bağladık.Amacımız marker koymak ve bunu yapabileceğimiz yer
        //onMapReady()
        mMap.setOnMapLongClickListener(this); //haritayla LongListener'ı bağladık

        //ContextCompat ve ActivityCompat kullandık önlem olarak SDK 23 ten öncesi için oki
        if(Build.VERSION.SDK_INT>=23) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION}, requestcode);
            } else {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 500, locationListener);
                mMap.setMyLocationEnabled(true);
                UiSettings uiSettings=googleMap.getUiSettings();
                uiSettings.setZoomControlsEnabled(true);
                uiSettings.setMyLocationButtonEnabled(true);
                uiSettings.setCompassEnabled(true);
                //Zaten kullanıcı izinleri varsa İLK BAŞTA
                //BİZİM GPS servisimizin bunu sağlaması gerekiyor...Sonra zaman ve uzaklık..Bu bize kaç süre
                //geçtiğinde güncellemenin yapılması gerektiğini söylüyor.
              //  locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,10000,500,locationListener);
                //gps sağlayıcıdan son bilinen lokasyonu al....Bunu yeni bir lokasyonmuş gibi
                //tanımlıyoruz ki kullanıcı ekran açıldığında buraya gelsin...
                Location lastKnownLocation=locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                //bu lstlang tipinde olmalıııı



            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(grantResults.length>0)
        {
            if(requestCode==requestcode)
            {
                if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)== PackageManager.PERMISSION_GRANTED){
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);

                }

            }
        }



        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }
    //Kullanıcının tıkladığı yerin LatLng'a çevrilmiş halini verir.

    @Override
    public void onMapLongClick(LatLng latLng) {
        mMap.clear();

        Geocoder geocoder=new Geocoder(MapsActivity.this, Locale.getDefault());

        try {
            List<Address> addressList=geocoder.getFromLocation(latLng.latitude,latLng.longitude,1);
            //burdaki kontrol kullanıcı denizde de tıklayabilir
            //onu önlemek için
            if(addressList!=null && addressList.size()>0) {
                if( addressList.get(0).getThoroughfare()!=null) {
                    //.getThoroughfare cadde adını almak için
                    address +=addressList.get(0).getThoroughfare();
                }             //Oranın numarasını almak için.Genelde Hollanda'da adres böyle alınırmış.
                if( addressList.get(0).getSubThoroughfare()!=null) {
                    //.getThoroughfare cadde adını almak için
                    address +=addressList.get(0).getSubThoroughfare();
                }
            }
            //bastığımız yerdeki adresi göstersin.
            mMap.addMarker(new MarkerOptions().position(latLng).title(address));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));


             lt=latLng.latitude;
//Database'e kaydedip oradan koordinatları geri çekiyoruz.
             ln=latLng.longitude;
            coord=lt+";"+ln;
             tıktık++;
            ///////////////////
            //SharedPreference sınıfına bu address i ekleyecez.

        }catch (IOException e) {
            e.printStackTrace();
        }

    }
    public static double distance(double lat1, double lng1, double lat2, double lng2) {

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
    //geldiyse true döndürecek ve kullanıcının ayarladığı süre iptal olup direk bitiş ekranına kullanıcı yönlenecek.
    public static boolean vardınMı(double kayıtlıLat,double kayıtlıLot,double enlem12,double boylam12 ) {
        if (distance(kayıtlıLat, kayıtlıLot, enlem12, boylam12) < 0.1 && enlem12!=0 && boylam12!=0 && kayıtlıLat!=0 && kayıtlıLot!=0) {
            return true;
        }
        return false;
    }
}