package com.example.tovisit_ram_c0779370_android;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;


public class AddLocationActivity extends AppCompatActivity implements OnMapReadyCallback {

    GoogleMap mMap;
    Marker fvt;
    String place_name;
    FavModel model;

    DatabaseHelper mDatabase;


    private final int REQUEST_CODE = 1;
    public static final String TAG = "MAPACTIVITY";

    // get user location
    private FusedLocationProviderClient fusedLocationProviderClient;
    LocationCallback locationCallback;
    LocationRequest locationRequest;


    // latitude, longitude
    double latitude, longitude;
    double dest_lat, dest_long;
    final int RADIUS = 1500;
    Geocoder geocoder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //CheckBox checkBox = findViewById(R.id.checkbox_visited);

//        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//
//
////                    Double  dest_lat = FavModel.FavLoc.get(i).getLatitude();
////                Double  dest_long = FavModel.FavLoc.get(i).getLongitude();
////                Location location = new Location("");
////                location.setLatitude(dest_lat);
////                location.setLongitude(dest_long);
////
////                Calendar calendar = Calendar.getInstance();
////                SimpleDateFormat dformat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
////                String sdate = dformat.format(calendar.getTime());
////                FavModel loc = new FavModel(dest_lat,dest_long,place_name,sdate,isVisited);
////                FavModel.FavLoc.remove(i);
////                FavModel.FavLoc.add(i,loc);
//            }
//        });
        mDatabase = new DatabaseHelper(this);
        geocoder = new Geocoder(this, Locale.getDefault());
        initMap();
        getUserLocation();

        if (!checkPermission())
            requestPermission();
        else
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
    }


    private void initMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void getUserLocation() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setSmallestDisplacement(10);
        setHomeMarker();
    }

    private void setHomeMarker() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {
                    LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());

                    latitude = userLocation.latitude;
                    longitude = userLocation.longitude;

                    CameraPosition cameraPosition = CameraPosition.builder()
                            .target(userLocation)
                            .zoom(15)
                            .bearing(0)
                            .tilt(45)
                            .build();
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                    mMap.addMarker(new MarkerOptions().position(userLocation)
                            .title("your location").draggable(true)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                }
            }


        };
    }

    private boolean checkPermission() {
        int permissionState = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setHomeMarker();
                fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {

                Log.i("tag", "onMapLongClick: " + latLng.latitude + "..." + latLng.longitude);

                Location location = new Location("your destination");
                location.setLongitude(latLng.latitude);
                location.setLongitude(latLng.longitude);

                dest_lat = latLng.latitude;
                dest_long = latLng.longitude;


                MarkerOptions options = new MarkerOptions().position(latLng).title("your Favorite").snippet("you are going here")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
//                Log.i("tag", "setMarker: added " + userLatLng.latitude + "..." +userLatLng.longitude);

                if (fvt != null) {

                    fvt.remove();

                }

                fvt = mMap.addMarker(options);


                try {


                    Log.i(TAG, "onMapLongClick: " + fvt.getPosition().latitude);
                    Log.i(TAG, "onMapLongClick: " + fvt.getPosition().longitude);


                    List<Address> addresses = geocoder.getFromLocation(fvt.getPosition().latitude, fvt.getPosition().longitude, 1);

                    System.out.println("try is working");
                    place_name = addresses.get(0).getAddressLine(0);



                    System.out.println(addresses.get(0).getAddressLine(0));
//                    if(addresses != null && addresses.size() >0) {
//                        if (addresses.get(0).getSubThoroughfare() != null) {
//                            place_name += addresses.get(0).getSubThoroughfare() + "";
//                        }
//                        if (addresses.get(0).getThoroughfare() != null) {
//                            place_name += addresses.get(0).getThoroughfare() + "";
//                        }
//                        if (addresses.get(0).getLocality() != null) {
//                            place_name += addresses.get(0).getLocality() + "";
//                        }
//
//
//
//
//
//                        Log.i("DEBUG", "onMapLongClick: " + place_name);
//                        System.out.println(place_name);
//                        model = new FavModel(fvt.getPosition().latitude, fvt.getPosition().longitude, place_name);


                } catch (IOException e) {

                    System.out.println("catch is working");
                    Log.i("DEBUG", "there is some problem: ");
                    //e.printStackTrace();


                }

            }
        });

    }


    public void addToFvt(View view) {

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dformat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
        String sdate = dformat.format(calendar.getTime());

        if (fvt == null) {
            Toast.makeText(this, "Select location", Toast.LENGTH_SHORT).show();

        } else {


            if (place_name == null){
                place_name = "Address";
            }
            if (mDatabase.addlocation(fvt.getPosition().latitude, fvt.getPosition().longitude, place_name, sdate, "")) {
                Toast.makeText(this, "add", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "location not added", Toast.LENGTH_SHORT).show();
            }

        }


    }
}
