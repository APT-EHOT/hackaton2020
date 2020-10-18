package com.example.mypointmobile;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.textclassifier.TextLinks;
import android.widget.Button;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SettingsActivity extends AppCompatActivity {

    public String str;
    private Button button;
    private final OkHttpClient client = new OkHttpClient();

    SharedPreferences sPref;
    public static final String APP_PREFERENCES = "mysettings";

    class IOAsyncTask extends AsyncTask<Location, Void, String> {

        @Override
        protected String doInBackground(Location... params) {
            return sendData(params[0].getLatitude(), params[0].getLongitude());
        }

        @Override
        protected void onPostExecute(String response) {
            str = response;
            SharedPreferences.Editor editor = sPref.edit();
            editor.putString(APP_PREFERENCES, str);
            editor.apply();
            Log.d("DDD",response);
            //Toast.makeText(SettingsActivity.this, response, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        button = findViewById(R.id.button);
        sPref = getPreferences(MODE_PRIVATE);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Location current = Gps.getLastKnownLocation(SettingsActivity.this);
                System.out.println("@@@@@@@ "+current.getLatitude()+" "+current.getLongitude());
                new IOAsyncTask().execute(current);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(SettingsActivity.this, Gps.class);
        intent.putExtra("q", str);
        startActivity(intent);
    }

    public String sendData(double latitude, double longitude){
        try {
            RequestBody formBody = new FormBody.Builder()
                    .add("LatLong", Double.toString(latitude)+","+Double.toString(longitude))
                    .build();

            Request request = new Request.Builder()
                    .url("http://192.168.31.187:8080/api")
                    .post(formBody)
                    .build();

            Response response = client.newCall(request).execute();
            return response.body().string();
        } catch (IOException e) {
            return "Error: " + e.getMessage();
        }
    }
}