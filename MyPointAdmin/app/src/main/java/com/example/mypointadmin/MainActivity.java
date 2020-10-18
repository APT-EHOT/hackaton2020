package com.example.mypointadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.example.mypointadmin.Gps.APP_PREFERENCES;
import static com.example.mypointadmin.Gps.REQUEST_ID_ACCESS_COURSE_FINE_LOCATION;

public class MainActivity extends AppCompatActivity implements GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener,
        OnMapReadyCallback {

    private GoogleMap mMap;
    public static double rotationInDegrees;
    public boolean hasGravityData, hasGeomagneticData;
    public static Geocoder geocoder;
    public static LocationManager locationManager;
    public LocationListener locationListener;
    private static LatLng finalCords = null;
    private TextToSpeech tts;
    private static Context context;
    private static String savedText = "False", str;
    private final OkHttpClient client = new OkHttpClient();

    private String arrayList;
    private ArrayList<String> locNames = new ArrayList<>();
    public static Location moment_loc;

    private EditText destinationText;
    private Button buttonAddOrder;
    private Button button;
    private ListView listView;

    class IOAsyncTask extends AsyncTask<Location, Void, String> {

        @Override
        protected String doInBackground(Location... params) {
            Geocoder geocoder = new Geocoder(context);
            List<Address> latLng = null;
            try {
                System.out.println(destinationText.getText().toString());
                latLng = geocoder.getFromLocationName(destinationText.getText().toString(),1);
                arrayList = latLng.get(0).getLatitude()+","+latLng.get(0).getLongitude();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return sendData(arrayList);
        }

        @Override
        protected void onPostExecute(String response) {
            destinationText.setText("");
            str = response;
            Log.d("DDD",response);
            //Toast.makeText(SettingsActivity.this, response, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        int permissionStatus = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);

        if (permissionStatus == PackageManager.PERMISSION_GRANTED) {
        } else {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_ID_ACCESS_COURSE_FINE_LOCATION);
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        buttonAddOrder = findViewById(R.id.buttonAddOrder);
        listView = findViewById(R.id.listView);
        destinationText = findViewById(R.id.destinationText);
        button = findViewById(R.id.button);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = ll;
        context = this;
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        buttonAddOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 new IOAsyncTask().execute();
            }
        });
    }

    @Override
    public boolean onMyLocationButtonClick() {
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(moment_loc.getLatitude(), moment_loc.getLongitude()), 14);
        mMap.animateCamera(cameraUpdate);
        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {

    }

    public static Location getLastKnownLocation(Context context) {
        locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
        List<String> providers = locationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return null;
            }
            Location l = locationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                // Found best last known location: %s", l);
                bestLocation = l;
            }
        }
        return bestLocation;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        geocoder = new Geocoder(this);
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        // TODO: Before enabling the My Location layer, you must request
        // location permission from the user. This sample does not include
        // a request for location permission.
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        }
        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationClickListener(this);

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {

            }
        });

        Location location = getLastKnownLocation(context);
        if (location != null) {
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 14);
            mMap.animateCamera(cameraUpdate);
            moment_loc = location;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

    }

    public String sendData(String arrayList){
        try {
            String s = "";
            s += arrayList;
            RequestBody formBody = new FormBody.Builder()
                    .add("Goods", s)
                    .build();

            Request request = new Request.Builder()
                    .url("http://192.168.31.187:8080/api/desctop")
                    .post(formBody)
                    .build();
            Response response = client.newCall(request).execute();

            return response.body().string();
        } catch (IOException e) {
            return "Error: " + e.getMessage();
        }
    }

    final LocationListener ll = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            moment_loc = location;
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

}