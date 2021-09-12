package com.londonappbrewery.climapm;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;


public class WeatherController extends AppCompatActivity {

    final int request_code=444;
    final String WEATHER_URL = "http://api.openweathermap.org/data/2.5/weather";
    final String APP_ID = "374b0d8f37f4aaeceed341999d9063de";
    final long MIN_TIME = 5000;
    final float MIN_DISTANCE = 1000;


    String LOCATION_PROVIDER = LocationManager.GPS_PROVIDER;

    TextView mCityLabel;
    ImageView mWeatherImage;
    TextView mTemperatureLabel;

    LocationManager mlocation;
    LocationListener mlistner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_controller_layout);

        // Linking the elements in the layout to Java code
        mCityLabel = (TextView) findViewById(R.id.locationTV);
        mWeatherImage = (ImageView) findViewById(R.id.weatherSymbolIV);
        mTemperatureLabel = (TextView) findViewById(R.id.tempTV);
        ImageButton changeCityButton = (ImageButton) findViewById(R.id.changeCityButton);


        // TODO: Add an OnClickListener to the changeCityButton here:
        changeCityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(WeatherController.this,changecity.class);
                startActivity(intent);
                
            }
        });

    }

    protected void onResume() {
        super.onResume();
        Intent myintent=getIntent();
        String city=myintent.getStringExtra("city");
        if(city!=null){
            getWeatherForNewCity(city);
        }else {
            getWeatherForCurrentLocation();
        }
    }


    // TODO: Add getWeatherForNewCity(String city) here:
    public void getWeatherForNewCity(String city){
        RequestParams params=new RequestParams();
        params.put("q",city);
        params.put("appid",APP_ID);
        letsDoSomeNetworking(params);
    }

    public void getWeatherForCurrentLocation() {
        mlocation = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mlistner = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                String latitude=String.valueOf(location.getLatitude());
                String longitude=String.valueOf(location.getLongitude());
                RequestParams params=new RequestParams();
                params.put("lat",latitude);
                params.put("long",longitude);
                params.put("id",APP_ID);
                letsDoSomeNetworking(params);
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
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.ACCESS_FINE_LOCATION},request_code);
            return;
        }
        mlocation.requestLocationUpdates(LOCATION_PROVIDER, MIN_TIME, MIN_DISTANCE, mlistner);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==request_code){

            if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                getWeatherForCurrentLocation();
            }
        }


    }
// TODO: Add letsDoSomeNetworking(RequestParams params) here:
        public void letsDoSomeNetworking(RequestParams params){
            AsyncHttpClient client = new AsyncHttpClient();
            client.get(WEATHER_URL,params,new JsonHttpResponseHandler(){
                public  void onSuccess(int statusCode, Header [] headers,JSONObject response){
                    WeatherDataModel model= WeatherDataModel.fromJson(response);
                    upate(model);
                }
                public void onFailure(int StatusCode, Header [] headers, Throwable e,JSONObject response){
                    Toast.makeText(WeatherController.this,"Request Failed",Toast.LENGTH_SHORT).show();
                }
            });
        }

    public void upate(WeatherDataModel model){
        mTemperatureLabel.setText(model.getTemperature());
        mCityLabel.setText(model.getCity());
        int resourseid=getResources().getIdentifier(model.getIconname(),"drawable",getPackageName());
        mWeatherImage.setImageResource(resourseid);
    }


    @Override
    protected void onPause() {
        super.onPause();
        if(mlocation!=null) mlocation.removeUpdates(mlistner);
    }
}
