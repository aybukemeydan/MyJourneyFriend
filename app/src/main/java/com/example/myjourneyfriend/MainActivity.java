
package com.example.myjourneyfriend;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import im.delight.android.location.SimpleLocation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;



public class MainActivity extends AppCompatActivity {
    ListView listView;
    static ArrayList<JSONObject> alarmListesi = new ArrayList<JSONObject>();
    static SharedPreferences shared;
    static String db_name = "com.example.myjourneyfriend";
    final int requestcode = 60;
    JSONArray jsonArray;
    //HavaDrumu   //////////////////////////////////////////////////////
    SimpleLocation location;
    public static String BaseUrl = "http://api.openweathermap.org/";
    public static String AppId = "4af8ab4af15ff51fcd3a67a18e643404";
    public static double lat;
    public static double lon;
    public static String units = "metric";
    public static String lang = "tr";
    private TextView weatherTemp;
    JSONObject jsonObject;
    static final String alarm_shared="alarms";
    static ListAdapter adapter;

    ///////***************////////////////////*******************//////
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.add_alarm);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, MapsActivity.class);
                startActivity(i);
            }
        });
        havaDurumuBilgileriniGetir();

         listView = findViewById(R.id.listView);

         shared = getApplicationContext()
                .getSharedPreferences(db_name, Context.MODE_PRIVATE);
         String jsArr = shared.getString(alarm_shared, null);



         if(jsArr == null) {
            JSONObject baska=new JSONObject();

            try{

                baska.put("alarm_name","Add new Alarm"); //sorun alarm_bilgi=null alıyor
            }
            catch(Exception e) {
                e.printStackTrace();
            }
             alarmListesi.add(baska);

        } else {

            try {
                 JSONArray a = new JSONArray(jsArr);
                for (int i = 0; i < a.length(); i++) {
                    jsonObject=a.optJSONObject(i);
                    alarmListesi.add(jsonObject);
                }

            }
               catch (Exception e) {
                e.printStackTrace();
            }

        }

        adapter=new ListAdapter(this,R.layout.list_layout,R.id.txttitle,alarmListesi);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                intent.putExtra("alarmId", position);
                startActivity(intent);
            }
        });
        //Listeden belirli bir alarmı kaldırma
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

                final int itemToRemove = position;

                new AlertDialog.Builder(MainActivity.this)
                        .setIcon(android.R.drawable.ic_delete)
                        .setTitle("are you sure ?")
                        .setMessage("do you want to delete this alarm ?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                dbBilgileriSil(itemToRemove);

                            }

                        }).setNegativeButton("No", null)
                        .show();
                return true;
            }
        });

    }

    //SharedPreference Bilgileri SİLMEK
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void dbBilgileriSil(final int itemToRemove) {
        shared.edit().remove(itemToRemove + "_Id").apply();
        alarmListesi.remove(itemToRemove);
        adapter.notifyDataSetChanged();
        JSONArray jsonArray = new JSONArray(MainActivity.alarmListesi);
        shared.edit().putString(alarm_shared, jsonArray.toString()).apply();

        shared.edit().remove(itemToRemove + "_duration").apply();
        shared.edit().remove(itemToRemove + "_address").apply();
        shared = getApplicationContext()
                .getSharedPreferences(db_name, Context.MODE_PRIVATE);


    }

    public void havaDurumuBilgileriniGetir() {

        location = new SimpleLocation(this);
//GPS özelliği aktif mi değil mi?
        // if we can't access the location yet
        if (!location.hasLocationEnabled()) {
            Toast.makeText(this, "Please open your location service", Toast.LENGTH_LONG).show();
            // ask the user to enable location access
            //konum ayarlarının yerini açtırıyoruz
            SimpleLocation.openSettings(this);
        } else
            //GPS açıksa artık kullanıcıdan izin isteyebilirim.
            //izin verilmemişse izin iste..
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, requestcode);
            } else {
                lat = location.getLatitude();
                lon = location.getLongitude();
                //Burda lokasyonu alırken hataya düşüyordu virgüllü alınca ondan böyle yaptıydık.
                try {
                    getCurrentData(lang, units);
                } catch (Exception ignored) {
                }
                location.setListener(new SimpleLocation.Listener() {
                    public void onPositionChanged() {
                        // new location data has been received and can be accessed
                        if (location != null) {
                            Toast.makeText(MainActivity.this, "Lokasyon güncellendi.", Toast.LENGTH_LONG).show();
                            lat = location.getLatitude();
                            lon = location.getLongitude();
                        }
                    }

                });


                location.beginUpdates();
            }


        weatherTemp = findViewById(R.id.tvSıcaklık3);
    }


    @Override
    protected void onPause() {

        location.endUpdates();
        super.onPause();
    }

    //Uygulama o an açıldığında olacaklar..
    @Override
    protected void onResume() {
        // make the device update its location
        location.beginUpdates();
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            alarmListesi.clear();
            adapter.notifyDataSetChanged();
            shared = getApplicationContext()
                    .getSharedPreferences(db_name, Context.MODE_PRIVATE);
            // HashSet<String> set = new HashSet<String>(MainActivity.alarms);

            // shared.edit().putStringSet("alarms", set).apply();
            shared.edit().clear().apply();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /////////////////////////HAVA DURUMU//////////////////*//////////////*////////////
    void getCurrentData(String lang, String units) throws UnsupportedEncodingException {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BaseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        HavadurumuServisi service = retrofit.create(HavadurumuServisi.class);
        Call<WeatherResponse> call = service.getCurrentWeatherData(String.valueOf(lat), String.valueOf(lon), AppId, lang, units);
        call.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(@NonNull Call<WeatherResponse> call, @NonNull Response<WeatherResponse> response) {


                WeatherResponse weatherResponse = response.body();
                //  assert weatherResponse != null;
                String desc = weatherResponse.weather.get(0).description;
                String coord = weatherResponse.name;
                weatherTemp.setText("Sıcaklık: " + weatherResponse.main.temp + "°C" + "\n" + desc + "\n" + coord);
                //   System.out.println("Sıcaklık: "+weatherResponse.main.temp);
                //assert weatherResponse != null;
                //System.out.println("Sonuc :"+weatherResponse);


            }

            @Override
            public void onFailure(@NonNull Call<WeatherResponse> call, @NonNull Throwable t) {
                weatherTemp.setText(t.getMessage());
                //   System.out.println(t.getMessage());
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length > 0) {
            if (requestCode == requestcode) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    lat = location.getLatitude();
                    lon = location.getLongitude();
                    //Burda lokasyonu alırken hataya düşüyordu virgüllü alınca ondan böyle yaptıydık.
                    try {
                        getCurrentData(lang, units);
                    } catch (Exception ignored) {
                        ignored.printStackTrace();
                    }
                    location.beginUpdates();
                    location.setListener(new SimpleLocation.Listener() {
                        public void onPositionChanged() {
                            // new location data has been received and can be accessed
                            if (location != null) {
                                Toast.makeText(MainActivity.this, "Lokasyon güncellendi.", Toast.LENGTH_LONG);
                                lat = location.getLatitude();
                                lon = location.getLongitude();
                            }
                        }

                    });
                }
            } else {
                Toast.makeText(this, "I can't find your location!", Toast.LENGTH_LONG);
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
