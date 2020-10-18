package com.example.mypointmobile;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.cs.googlemaproute.DrawRoute;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Gps extends AppCompatActivity
        implements GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener,
        OnMapReadyCallback, DrawRoutte.onDrawRoutte{

    String debug = "DDD";

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
    public static LocationManager locationManager;
    public LocationListener locationListener;
    private static LatLng finalCords = null;
    private TextToSpeech tts;
    private static Context context;
    private static String savedText = "False";

    public static Location moment_loc;
    public String allAdr;
    ArrayList all_geo_points;
    Context mContext;
    public Polyline polyline;

    SharedPreferences sPref;
    public static final String APP_PREFERENCES = "mysettings";

    final String SAVED_TEXT = "saved_text";

    private final OkHttpClient client = new OkHttpClient();

    FloatingActionButton fab;

    @Override
    public void afterDraw(String result) {
        Log.d(debug,""+result);
    }

    class IOAsyncTask extends AsyncTask<String, Void, ArrayList> {

        @Override
        protected ArrayList doInBackground(String... strings) {
            return getDirections(17.3849, 78.4866, 28.63491, 77.22461);
        }

        @Override
        protected void onPostExecute(ArrayList response) {
            Log.d(debug,""+response);
            //Toast.makeText(SettingsActivity.this, response, Toast.LENGTH_LONG).show();
        }

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        int permissionStatus = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionStatus == PackageManager.PERMISSION_GRANTED) {
        } else {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_ID_ACCESS_COURSE_FINE_LOCATION);
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Bundle arguments = getIntent().getExtras();
        if(arguments != null) {
            savedText = arguments.get("q").toString();
        }

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = ll;

        mMarkerPoints = new ArrayList<>();

        fab = findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Gps.this, SettingsActivity.class);
                startActivity(intent);
            }
        });
        context = this;

        //new IOAsyncTask().execute();
        Runnable runnable = new Runnable() {
            public void run() {


            }
        };
        Thread thread = new Thread(runnable);
        thread.start();

    }

    public static void onDrawMarker() {
        if (finalCords != null) {
            mMap.addMarker(new MarkerOptions().position(finalCords).title("test"));
        }
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
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

        Bundle arguments = getIntent().getExtras();
        if(arguments != null) {
            allAdr = arguments.get("q").toString();
        }

        if((allAdr != null) && !allAdr.equals("")){
            allAdr = "{"+"\"phoneNumbers\": "+allAdr+"}";
            try {
                JSONObject obj = new JSONObject(allAdr);
                Geocoder geocoder = new Geocoder(context);
                System.out.println(obj.getJSONArray("phoneNumbers").get(1));
                Address startLoc = new Address(null);
                startLoc.setLatitude(moment_loc.getLatitude());
                startLoc.setLongitude(moment_loc.getLongitude());
                Address endLoc;
                for (int i = 1; i < obj.getJSONArray("phoneNumbers").length(); i++) {
                    System.out.println(obj.getJSONArray("phoneNumbers").get(i));
                    endLoc = geocoder.getFromLocationName(obj.getJSONArray("phoneNumbers").get(i).toString(),1).get(0);
                    DrawRoutte.getInstance(this,Gps.this).setFromLatLong(startLoc.getLatitude(),startLoc.getLongitude())
                            .setToLatLong(endLoc.getLatitude(),endLoc.getLongitude()).setGmapAndKey("AIzaSyCGxM42XnoNjyTCoSTKwMXeCvR8rp7Tt7Q",mMap).run();
                    mMap.addMarker(new MarkerOptions().position(new LatLng(endLoc.getLatitude(),endLoc.getLongitude())).title("test"));
                    startLoc = endLoc;

                }
            } catch (JSONException | IOException e) {
                e.printStackTrace();
            }
        }

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
                // Already two locations
                if (mMarkerPoints.size() > 1) {
                    mMarkerPoints.clear();
                    mMap.clear();
                }

                // Adding new item to the ArrayList
                mMarkerPoints.add(point);

                // Creating MarkerOptions
                MarkerOptions options = new MarkerOptions();

                // Setting the position of the marker
                options.position(point);

                /**
                 * For the start location, the color of marker is GREEN and
                 * for the end location, the color of marker is RED.
                 */
                if (mMarkerPoints.size() == 1) {
                    options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                } else if (mMarkerPoints.size() == 2) {
                    options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                }

                // Add new marker to the Google Map Android API V2
                //mMap.addMarker(options);

                // Checks, whether start and end locations are captured
                if (mMarkerPoints.size() >= 2) {
                    mOrigin = mMarkerPoints.get(0);
                    mDestination = mMarkerPoints.get(1);
                    drawRoute();
                }

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
        mHandler.removeCallbacks(timeUpdaterRunnable);
        mHandler.postDelayed(timeUpdaterRunnable, 100);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPreferences.Editor editor = sPref.edit();
        editor.putString(APP_PREFERENCES, "");
        editor.apply();
    }

    //This function is to parse the value of "points"
    public List<LatLng> decodeOverviewPolyLinePonts(String encoded) {
        List<LatLng> poly = new ArrayList<LatLng>();
        if (encoded != null && !encoded.isEmpty() && encoded.trim().length() > 0) {
            int index = 0, len = encoded.length();
            int lat = 0, lng = 0;

            while (index < len) {
                int b, shift = 0, result = 0;
                do {
                    b = encoded.charAt(index++) - 63;
                    result |= (b & 0x1f) << shift;
                    shift += 5;
                } while (b >= 0x20);
                int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
                lat += dlat;

                shift = 0;
                result = 0;
                do {
                    b = encoded.charAt(index++) - 63;
                    result |= (b & 0x1f) << shift;
                    shift += 5;
                } while (b >= 0x20);
                int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
                lng += dlng;

                LatLng p = new LatLng((((double) lat / 1E5)),
                        (((double) lng / 1E5)));
                poly.add(p);
            }
        }
        return poly;
    }

    public ArrayList getDirections(double lat1, double lon1, double lat2, double lon2) {
        String url = "https://maps.googleapis.com/maps/api/directions/json?origin="+"Москва, Вернадского 78"+"&destination="+"Москва, Вернадского 86"+"&key=AIzaSyCGxM42XnoNjyTCoSTKwMXeCvR8rp7Tt7Q";
        String tag[] = { "lat", "lng" };
        ArrayList list_of_geopoints = new ArrayList();
        try {
            Request request = new Request.Builder()
                    .url(url)
                    .build();
            Response response = client.newCall(request).execute();
            Gson gson = new GsonBuilder().create();
            String in = response.body().string();
            JSONObject obj = new JSONObject(in);

            if (obj != null) {
                JSONArray nl1, nl2;
                nl1 = obj.getJSONArray(tag[0]);
                nl2 = obj.getJSONArray(tag[1]);
                if (nl1.length() > 0) {
                    list_of_geopoints = new ArrayList();
                    for (int i = 0; i < nl1.length(); i++) {
                        Object node1 = nl1.get(i);
                        Object node2 = nl2.get(i);
                        Log.d(debug," "+node1+" "+node2);
//                        double lat = Double.parseDouble(node1.getTextContent());
//                        double lng = Double.parseDouble(node2.getTextContent());
//                        list_of_geopoints.add(new LatLng((int) (lat * 1E6), (int) (lng * 1E6)));
                    }
                } else {
                    // No points found
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(debug,e.toString());
        }
        return list_of_geopoints;
    }

    @Override
    public boolean onMyLocationButtonClick() {
        //Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(moment_loc.getLatitude(), moment_loc.getLongitude()), 14);
        mMap.animateCamera(cameraUpdate);
        SettingsActivity settingsActivity = new SettingsActivity();
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

            double[] res = get_home(address, location.getLatitude(), location.getLongitude(), rotationInDegrees);
            //double res[] = calcDist(location.getLatitude(), location.getLongitude(), address.getLatitude(), address.getLongitude());

            //Toast.makeText(this, address.getAddressLine(0) + "\n"+ res[0] + "m\n" + res[1] + "deg\n", Toast.LENGTH_LONG).show();

            if ((int) res[2] == -1) {
                Toast.makeText(this, "Can't find home", Toast.LENGTH_LONG).show();
                //mMap.clear();
            } else {
                Toast.makeText(this, "Successfully", Toast.LENGTH_LONG).show();
                //textView2.setText("Current location:\n" + location.getLatitude() + " " + location.getLongitude());
                //textView3.setText(address.get((int) res[2]).getAddressLine(0) + "\n\n" + res[0] + " m\n" + res[1] + " deg\n");
                //mMap.clear();
                mMap.addMarker(new MarkerOptions().position(new LatLng(address.get((int) res[2]).getLatitude(), address.get((int) res[2]).getLongitude())).title("test"));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
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
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 1) {
            if (permissions.length == 1 &&
                    permissions[0] == Manifest.permission.ACCESS_FINE_LOCATION &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                mMap.setMyLocationEnabled(true);
            } else {
                // Permission was denied. Display an error message.
            }
        }
    }

    private void drawRoute(){

        // Getting URL to the Google Directions API
        String url = getDirectionsUrl(mOrigin, mDestination);

        DownloadTask downloadTask = new DownloadTask();

        // Start downloading json data from Google Directions API
        downloadTask.execute(url);
    }

    public void get_location() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, locationListener, null);
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

    private String getDirectionsUrl(LatLng origin,LatLng dest){

        // Origin of route
        String str_origin = "origin="+origin.latitude+","+origin.longitude;

        // Destination of route
        String str_dest = "destination="+dest.latitude+","+dest.longitude;

        // Key
        String key = "key=" + getString(R.string.google_maps_key);

        // Building the parameters to the web service
        String parameters = str_origin+"&amp;"+str_dest+"&amp;"+key;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters;

        return url;
    }

    /** A method to download json data from url */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb  = new StringBuffer();

            String line = "";
            while( ( line = br.readLine())  != null){
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        }catch(Exception e){
            Log.d("Exception on download", e.toString());
        }finally{
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    /** A class to download data from Google Directions URL */
    private class DownloadTask extends AsyncTask<String, Void, String> {

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try{
                // Fetching the data from web service
                data = downloadUrl(url[0]);
                Log.d("DownloadTask","DownloadTask : " + data);
            }catch(Exception e){
                Log.d("Background Task",e.toString());
            }
            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);
        }
    }

    /** A class to parse the Google Directions in JSON format */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String,String>>> >{

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try{
                jObject = new JSONObject(jsonData[0]);

            }catch(Exception e){
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;

            // Traversing through all the routes
            for(int i=0;i<result.size();i++){
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for(int j=0;j<path.size();j++){
                    HashMap<String,String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(8);
                lineOptions.color(Color.RED);
            }

            // Drawing polyline in the Google Map for the i-th route
            if(lineOptions != null) {
                if(mPolyline != null){
                    mPolyline.remove();
                }
                mPolyline = mMap.addPolyline(lineOptions);

            }else
                Toast.makeText(getApplicationContext(),"No route is found", Toast.LENGTH_LONG).show();
        }
    }

    private Runnable timeUpdaterRunnable = new Runnable() {
        public void run() {
            if (moment_loc != null) {
            }
            mHandler.postDelayed(this, 500);
        }
    };

    public static double[] calcDist(double llat1, double llong1, double llat2, double llong2){
        //Math.PI;
        int rad = 6372795;
        double lat1 = llat1*Math.PI/180;
        double lat2 = llat2*Math.PI/180;
        double long1 = llong1*Math.PI/180;
        double long2 = llong2*Math.PI/180;

        double cl1 = Math.cos(lat1);
        double cl2 = Math.cos(lat2);
        double sl1 = Math.sin(lat1);
        double sl2 = Math.sin(lat2);
        double delta = long2 - long1;
        double cdelta = Math.cos(delta);
        double sdelta = Math.sin(delta);

        double y = Math.sqrt(Math.pow(cl2*sdelta,2)+Math.pow(cl1*sl2-sl1*cl2*cdelta,2));
        double x = sl1*sl2+cl1*cl2*cdelta;
        double ad = Math.atan2(y,x);
        double dist = ad*rad;

        x = (cl1*sl2) - (sl1*cl2*cdelta);
        y = sdelta*cl2;
        double z = Math.toDegrees(Math.atan(-y/x));

        if (x < 0){z = z+180;}

        double z2 = (z+180.) % 360. - 180;
        z2 -= Math.toRadians(z2);
        double anglerad2 = z2 - ((2*Math.PI)*Math.floor((z2/(2*Math.PI))));
        double angledeg = (anglerad2*180.)/Math.PI;

        System.out.println("!!!!!!!!!!!!!!! "+ dist + " m " + angledeg + " dig");

        double mass[] = {dist,angledeg,-1};
        return mass;
    }

    public double[] calcDist_radius(double llat1, double llong1, double llat2, double llong2){
        //Math.PI;
        int rad = 6372795;
        double lat1 = llat1*Math.PI/180;
        double lat2 = llat2*Math.PI/180;
        double long1 = llong1*Math.PI/180;
        double long2 = llong2*Math.PI/180;

        double cl1 = Math.cos(lat1);
        double cl2 = Math.cos(lat2);
        double sl1 = Math.sin(lat1);
        double sl2 = Math.sin(lat2);
        double delta = long2 - long1;
        double cdelta = Math.cos(delta);
        double sdelta = Math.sin(delta);

        double y = Math.sqrt(Math.pow(cl2*sdelta,2)+Math.pow(cl1*sl2-sl1*cl2*cdelta,2));
        double x = sl1*sl2+cl1*cl2*cdelta;
        double ad = Math.atan2(y,x);
        double dist = ad*rad;

        x = (cl1*sl2) - (sl1*cl2*cdelta);
        y = sdelta*cl2;
        double z = Math.toDegrees(Math.atan(-y/x));

        if (x < 0){z = z+180;}

        double z2 = (z+180.) % 360. - 180;
        z2 -= Math.toRadians(z2);
        double anglerad2 = z2 - ((2*Math.PI)*Math.floor((z2/(2*Math.PI))));
        double angledeg = (anglerad2*180.)/Math.PI;

        System.out.println("!!!!!!!!!!!!!!! "+ dist + " m " + angledeg + " dig");

        double mass[] = {dist,angledeg,-1};
        return mass;
    }

    public void shell_sort(ArrayList<double[]> array, int size) {
        int step, i, j;
        double[] tmp;

        for (step = size / 2; step > 0; step /= 2) {
            for (i = step; i < size; i++) {
                for (j = i - step; j >= 0 && array.get(j)[0] > array.get(j + step)[0]; j -= step)
                {
                    tmp = array.get(j);
                    array.set(j,array.get(j + step));
                    array.set(j + step,tmp);
                }
            }
        }
    }

    public double[] get_home(List<Address> addresses, double llat, double llong, double degree){
        double min_dist = 10000;
        int numb_min = -1;
        ArrayList<double[]> data_mass = new ArrayList<double[]>();
        for(int i=0;i<addresses.size();i++){
            data_mass.add(calcDist(llat, llong, addresses.get(i).getLatitude(), addresses.get(i).getLongitude()));
            data_mass.get(i)[2] = i;
        }

        shell_sort(data_mass, data_mass.size());

        for(int i=0;i<data_mass.size();i++){
            if ((data_mass.get(i)[1]>=degree-30)&&(data_mass.get(i)[1]<=degree+30)){
                return data_mass.get(i);
            }
        }
        return new double[]{0, 0, -1};
    }
}
