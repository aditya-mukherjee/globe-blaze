package com.example.google.playservices.placepicker;

import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.text.format.Time;
import android.util.Log;
import android.widget.Button;
import android.widget.Chronometer;

//import com.google.maps.android.SphericalUtil;
import java.lang.Math;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class TrackActivity extends FragmentActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, OnMapReadyCallback, LocationListener{

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    public static final String TAG=TrackActivity.class.getSimpleName();
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private LocationRequest mLocationRequest;
    public int totaltime;
    private static final double LOCAL_PI=3.1415926535897932385;
    Chronometer focus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track);

//        Chronometer c=new Chronometer(getApplicationContext());
//        c.start();

        focus=(Chronometer) findViewById(R.id.chronometer1);
        focus.start();

        mGoogleApiClient=new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();



        // Create the LocationRequest object
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(100)
                .setFastestInterval(100);


    }

    @Override
    public void onConnected(@Nullable Bundle bundle)
    {
        Log.i(TAG, "Location services connected.");
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (location == null) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
        else {
            handleNewLocation(location);
        }
    }

    @Override
    public void onConnectionSuspended(int i)
    {
        Log.i(TAG, "Location services suspended. Please reconnect.");
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult)
    {
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            Log.i(TAG, "Location services connection failed with code " + connectionResult.getErrorCode());
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

    }

    private void handleNewLocation(Location location) {
        Log.d(TAG, location.toString());
        double currentLatitude = location.getLatitude();
        double currentLongitude = location.getLongitude();
        LatLng latLng = new LatLng(currentLatitude, currentLongitude);

        Intent intent=getIntent();
        LatLng ll=(LatLng)intent.getParcelableExtra("LattLong");

//        SphericalUtil su=null;
//        double dist=su.computeDistanceBetween(latLng,ll);
        double nearbylat=ll.latitude;
        double nearbylong=ll.longitude;
        double dist=DirectDistance(currentLatitude,currentLongitude,nearbylat,nearbylong);

        if(dist<=25.00)
        {
            Time now=new Time();
            now.setToNow();
            int endminute=now.minute;
            int endsecond=now.second;
            int startminute=intent.getIntExtra("StartMinute",0);
            int startsecond=intent.getIntExtra("StartSecond",0);
            if(startsecond<=endsecond) {
                int totaltime = ((endminute - startminute) * 60) + (endsecond - startsecond);
            }
            else{
                int totaltime = ((endminute - startminute - 1) * 60) + (60 + endsecond - startsecond);
            }

            Intent i=new Intent(this, ResultActivity.class);
            String success="Congratulations! You have successfully completed your challenge in : ";
            //String time="You completed the challenge in ";
            String time=Integer.toString(totaltime);
            time+=" seconds!";
            i.putExtra("Success",success);
            i.putExtra("Time",time);
            startActivity(i);
        }
        else
        {
            MarkerOptions options = new MarkerOptions()
                    .position(latLng)
                    .title("You are here!");
            MarkerOptions options2 = new MarkerOptions()
                    .position(ll)
                    .title("You have to reach here!");
            mMap.addMarker(options);
            mMap.addMarker(options2);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(ll));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
        mGoogleApiClient.connect();
    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    private void setUpMap() {
        mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        handleNewLocation(location);
    }

    double ToRadians(double degrees)
    {
        double radians = degrees * LOCAL_PI / 180;
        return radians;
    }

    double DirectDistance(double lat1, double lng1, double lat2, double lng2)
    {
        double earthRadius = 3958.75;
        double dLat = ToRadians(lat2-lat1);
        double dLng = ToRadians(lng2-lng1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(ToRadians(lat1)) * Math.cos(ToRadians(lat2)) *
                        Math.sin(dLng/2) * Math.sin(dLng/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double dist = earthRadius * c;
        double meterConversion = 1609.00;
        return dist * meterConversion;
    }
}
