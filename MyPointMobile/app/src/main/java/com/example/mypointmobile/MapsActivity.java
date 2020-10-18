package com.example.mypointmobile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.audiofx.BassBoost;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends AppCompatActivity implements GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener,
        OnMapReadyCallback {

    private LatLng mOrigin;
    private LatLng mDestination;
    private Polyline mPolyline;
    ArrayList<LatLng> mMarkerPoints;

    private static GoogleMap mMap;
    public TextView textView, textView2, textView3;
    public EditText editText;
    public Button find_bt;
    public Sensor acc, mag;
    private SensorManager sensorManager;
    public static final int REQUEST_ID_ACCESS_COURSE_FINE_LOCATION = 100;
    private Handler mHandler = new Handler();

    public float[] gravityData = new float[3];
    public float[] geomagneticData = new float[3];
    public static double rotationInDegrees;
    public boolean hasGravityData, hasGeomagneticData;
    public static Geocoder geocoder;
    public LocationManager locationManager;
    public LocationListener locationListener;
    private static LatLng finalCords = null;
    private TextToSpeech tts;

    public static Location moment_loc;
    Context mContext;
    public Polyline polyline;

    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        fab = findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MapsActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(getLastKnownLocation().getLatitude(), getLastKnownLocation().getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        System.out.println("%%%%%%%%%%%%%%%% Test");
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        try {
            //System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$ "+geocoder.getFromLocationName("premise",1,mMap.getMyLocation().getLatitude(),mMap.getMyLocation().getLongitude(),mMap.getMyLocation().getLatitude(),mMap.getMyLocation().getLongitude()));
            System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$ " + location);

            //mMap.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude()-0.005, location.getLongitude()-0.005)).title("test"));
            //mMap.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude()+0.0005f, location.getLongitude()+0.0005f)).title("test"));

            double t_1 = mMap.getMyLocation().getLatitude();// - 0.0005;
            double t_2 = mMap.getMyLocation().getLongitude();// - 0.0005;

            //mMap.addMarker(new MarkerOptions().position(new LatLng(t_1, t_2)).title("test"));

            List<Address> address = geocoder.getFromLocation(t_1, t_2, 5);
            System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$ " + address);

            //double res[] = calcDist(location.getLatitude(), location.getLongitude(), address.getLatitude(), address.getLongitude());

            //Toast.makeText(this, address.getAddressLine(0) + "\n"+ res[0] + "m\n" + res[1] + "deg\n", Toast.LENGTH_LONG).show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Location getLastKnownLocation() {
        locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        List<String> providers = locationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
}