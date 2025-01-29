package com.example.anonymous.campussocialportal;

import android.*;
import android.*;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by User on 10/2/2017.
 */

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {
   // ArrayList<LatLng> listPoints;

    @Override
    public void onMapReady(GoogleMap googleMap) {

        Toast.makeText(this, "Map is Ready", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onMapReady: map is ready");
        mMap = googleMap;

        if (mLocationPermissionsGranted) {
            getDeviceLocation();

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);





        }
    }

    private static final String TAG = "MapActivity";

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 15f;

    double dLong;
    double dLat;
    Location currentLocation;


   // LocationManager locationManager;
   // Criteria criteria;
  //  String bestProvider;

    //vars
    private Boolean mLocationPermissionsGranted = false;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        Log.d(TAG,"on create");

        Intent intent = getIntent();
        String route = intent.getStringExtra("route");


       // listPoints = new ArrayList<>();

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Location");



        DatabaseReference Drivers = myRef.child("drivers");
        DatabaseReference Students = myRef.child("students");
        DatabaseReference Route1 = Drivers.child("route1");
        DatabaseReference Route2 = Drivers.child("route2");
        DatabaseReference Route3 = Drivers.child("route3");
        DatabaseReference Route4 = Drivers.child("route4");
        DatabaseReference click = null;
        if(route.equals("1")){
            click=Route1;

        }
       else if(route.equals("2")){
            click=Route2;

        }
        else if(route.equals("3")){
            click=Route3;

        }
        else if(route.equals("4")){
            click=Route4;

        }


       click.addValueEventListener(new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot dataSnapshot){
                String value= dataSnapshot.getValue(String.class);

                double dlatorigin;
                double dlongorigin;
                if(value!=null){
                    String [] separated = value.split(",");
                    dLat  = Double.parseDouble(separated[0].trim());
                    dLong = Double.parseDouble(separated[1].trim());
                   // mMap.clear();

                    moveCamera(new LatLng(dLat, dLong), DEFAULT_ZOOM,"My Bus Location");

                }



                if(currentLocation!=null) {
                    dlatorigin = currentLocation.getLatitude();
                    dlongorigin = currentLocation.getLongitude();


                    String url = getRequestUrl(dlatorigin, dlongorigin, dLat, dLong);
                    TaskRequestDirections taskRequestDirections = new TaskRequestDirections();
                    taskRequestDirections.execute(url);
                }
               // Toast.makeText(MapsActivity.this,"Lat="+dLat,Toast.LENGTH_LONG).show();


            }
            @Override
            public void onCancelled(DatabaseError databaseError){

            }



        });

        getLocationPermission();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getLocationPermission();

    }

    public void buslocation(View view){
    moveCamera(new LatLng(dLat, dLong), DEFAULT_ZOOM,"My Bus Location");


    }



    private void getDeviceLocation(){
        Log.d(TAG, "getDeviceLocation: getting the devices current location");

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try{
            if(mLocationPermissionsGranted){


               // locationManager = (LocationManager)  this.getSystemService(Context.LOCATION_SERVICE);

                final Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful()&& task.getResult() != null) {
                            Log.d(TAG, "onComplete: found location!");
                            currentLocation = (Location) task.getResult();

                           // criteria = new Criteria();

                          //  bestProvider = String.valueOf(locationManager.getBestProvider(criteria, true)).toString();
                            //  Location currentLocation2 = (Location) task.getResult();

                            //Create the URL to get request from first marker to second marker
                            double dlatorigin;// = currentLocation.getLatitude();
                            double dlongorigin;// = currentLocation.getLongitude();

                            if (currentLocation!=null){



                            moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
                                    DEFAULT_ZOOM, "My Location");
                        }


                        }else{
                            Log.d(TAG, "onComplete: current location is null");
                            Toast.makeText(MapsActivity.this, "unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }catch (SecurityException e){
            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage() );
        }
    }

    private void moveCamera(LatLng latLng, float zoom,String title){
        Log.d(TAG, "moveCamera: moving the camera to: lat: " + latLng.latitude + ", lng: " + latLng.longitude );



if(mMap!=null && title.equals("My Location")){
    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

}




        if(!title.equals("My Location") &&mMap!=null){
    if(latLng.latitude!=0.0 && latLng.longitude!=0.0) {


        if (currentLocation != null) {

            mMap.clear();
            double dlatorigin = latLng.latitude;
            double dlongorigin = latLng.longitude;
            dlatorigin = currentLocation.getLatitude();
            dlongorigin = currentLocation.getLongitude();


            String url = getRequestUrl(dlatorigin, dlongorigin, dLat, dLong);
            TaskRequestDirections taskRequestDirections = new TaskRequestDirections();
            taskRequestDirections.execute(url);
            // mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
            //LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());


            MarkerOptions options = new MarkerOptions()
                    .position(latLng)
                    .title(title).icon(BitmapDescriptorFactory.fromResource(R.drawable.bus));
            float results[] = new float[10];
            Location.distanceBetween(currentLocation.getLatitude(), currentLocation.getLongitude(), dLat, dLong, results);
            float km;
            if(results[0] / 1000>0.7)
            {
                km=results[0] / 1000;
                options.snippet("Distance = " + km + " km");
            }
            else{
                options.snippet("  Very Near  ");
            }
            mMap.addMarker(options);
            float camzoom;
            if(mMap.getCameraPosition().zoom>10f){
                camzoom = mMap.getCameraPosition().zoom;
            }
            else{
                camzoom=DEFAULT_ZOOM;
            }


            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, camzoom);
            mMap.getCameraPosition();
            mMap.animateCamera(cameraUpdate);
            //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

        }
    }
    else{
        mMap.clear();
        Toast.makeText(this,"Your Bus Is Not Available Right Now",Toast.LENGTH_SHORT).show();
    }

        }
    }

    private void initMap(){
        Log.d(TAG, "initMap: initializing map");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(MapsActivity.this);
    }

    private void getLocationPermission(){
        Log.d(TAG, "getLocationPermission: getting location permissions");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                mLocationPermissionsGranted = true;
                initMap();

            }else{
                ActivityCompat.requestPermissions(this,
                        permissions,
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        }else{
            ActivityCompat.requestPermissions(this,
                    permissions,
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: called.");
        mLocationPermissionsGranted = false;

        switch(requestCode){
            case LOCATION_PERMISSION_REQUEST_CODE:{
                if(grantResults.length > 0){
                    for(int i = 0; i < grantResults.length; i++){
                        if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
                            mLocationPermissionsGranted = false;
                            Log.d(TAG, "onRequestPermissionsResult: permission failed");
                            return;
                        }
                    }
                    Log.d(TAG, "onRequestPermissionsResult: permission granted");
                    mLocationPermissionsGranted = true;
                    //initialize our map
                    initMap();
                }
            }
        }
    }







    private String getRequestUrl(double originlat,double originlong, double destlat,double destlong) {
        //Value of origin
        String str_org = "origin=" + originlat +","+originlong;
        //Value of destination
        String str_dest = "destination=" + destlat+","+destlong;
        //Set value enable the sensor
        String sensor = "sensor=false";
        //Mode for find direction
        String mode = "mode=driving";
        //Build the full param
        String param = str_org +"&" + str_dest + "&" +sensor+"&" +mode;
        //Output format
        String output = "json";
        //Create url to request
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + param;
        //Toast.makeText(this,url,Toast.LENGTH_LONG).show();
        return url;
    }








    private String requestDirection(String reqUrl) throws IOException {
        String responseString = "";
        InputStream inputStream = null;
        HttpURLConnection httpURLConnection = null;
        try{
            URL url = new URL(reqUrl);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.connect();

            //Get the response result
            inputStream = httpURLConnection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            StringBuffer stringBuffer = new StringBuffer();
            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                stringBuffer.append(line);
            }

            responseString = stringBuffer.toString();
            bufferedReader.close();
            inputStreamReader.close();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
            httpURLConnection.disconnect();
        }
        return responseString;
    }











    public class TaskRequestDirections extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            String responseString = "";
            try {
                responseString = requestDirection(strings[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return  responseString;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //Parse json here
            TaskParser taskParser = new TaskParser();
            taskParser.execute(s);
        }
    }







    public class TaskParser extends AsyncTask<String, Void, List<List<HashMap<String, String>>> > {

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... strings) {
            JSONObject jsonObject = null;
            List<List<HashMap<String, String>>> routes = null;
            try {
                jsonObject = new JSONObject(strings[0]);
                DirectionsParser directionsParser = new DirectionsParser();
                routes = directionsParser.parse(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> lists) {
            if(lists!=null){
                ArrayList points = null;

                PolylineOptions polylineOptions = null;

                for (List<HashMap<String, String>> path : lists) {
                    points = new ArrayList();
                    polylineOptions = new PolylineOptions();

                    for (HashMap<String, String> point : path) {
                        double lat = Double.parseDouble(point.get("lat"));
                        double lon = Double.parseDouble(point.get("lon"));

                        points.add(new LatLng(lat,lon));
                    }

                    polylineOptions.addAll(points);
                    polylineOptions.width(10);
                    polylineOptions.color(Color.GREEN);
                    polylineOptions.geodesic(true);
                }

                if (polylineOptions!=null) {
                    mMap.addPolyline(polylineOptions);
                } else {
                    Toast.makeText(getApplicationContext(), "Direction not found!", Toast.LENGTH_SHORT).show();
                }

            }
            //Get list route and display it into the map



        }
    }


}













