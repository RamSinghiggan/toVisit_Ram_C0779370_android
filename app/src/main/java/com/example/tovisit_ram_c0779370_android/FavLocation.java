package com.example.tovisit_ram_c0779370_android;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class FavLocation extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerDragListener {

    GoogleMap mMap;
    public int i;
    public String isVisited = "NotVisited";




    private final int REQUEST_CODE = 1;

    // get user location
    private FusedLocationProviderClient fusedLocationProviderClient;
    LocationCallback locationCallback;
    LocationRequest locationRequest;
    ArrayList<FavModel> arrayList;



    // latitude, longitude
    double latitude, longitude;
    double fav_lat , fav_long, dirc_lat, direc_long;

    final int RADIUS = 1500;
    Geocoder geocoder;
    String place_name;
    FavModel model;
    public static boolean directionRequested;

    DatabaseHelper mDatabase;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fav_location);
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
                            .title("your location")
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
        mMap.setOnMarkerDragListener((this));
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {

                Log.i("tag", "onMapLongClick: " + latLng.latitude + "..." + latLng.longitude);

                Location location = new Location("your destination");
                location.setLongitude(latLng.latitude);
                location.setLongitude(latLng.longitude);

                Double  dest_lat = latLng.latitude;
                Double  dest_long = latLng.longitude;

                dirc_lat = dest_lat;
                direc_long = dest_long;

                MarkerOptions options = new MarkerOptions().position(latLng).title(place_name).snippet("you are going here").draggable(true)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                mMap.addMarker(options);
            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(final Marker marker) {
                AlertDialog.Builder builder = new AlertDialog.Builder(FavLocation.this);
                builder.setTitle("Are you sure");
                builder.setPositiveButton("set as favoutite destination", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        fav_lat =  marker.getPosition().latitude;
                        fav_long = marker.getPosition().longitude;


                        dirc_lat = fav_lat;
                        direc_long = fav_long;

                    }
                });

                builder.setNegativeButton("set as source", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        latitude = marker.getPosition().latitude;
                        longitude = marker.getPosition().longitude;

                    }
                });



                builder.create().show();
                try {

                    List<Address> addresses = geocoder.getFromLocation(fav_lat,fav_long,1);
                    if(addresses != null && addresses.size() >0) {
                        if (addresses.get(0).getSubThoroughfare() != null) {
                            place_name += addresses.get(0).getSubThoroughfare() + "";
                        }
                        if (addresses.get(0).getThoroughfare() != null) {
                            place_name += addresses.get(0).getThoroughfare() + "";
                        }
                        if (addresses.get(0).getLocality() != null) {
                            place_name += addresses.get(0).getLocality() + "";
                        }
                        model = new FavModel(fav_lat,fav_long,place_name);
                    }


                } catch (IOException e) {
                    e.printStackTrace();
                }
                return false;
            }
        });

        Intent intent = getIntent();
        i = intent.getExtras().getInt("id");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        System.out.println("intent passesd");

        System.out.println(i);
        System.out.println(FavModel.FavLoc.size());

        dirc_lat = FavModel.FavLoc.get(i).getLatitude();
        direc_long = FavModel.FavLoc.get(i).getLongitude();

        LatLng Favloc = new LatLng(FavModel.FavLoc.get(i).getLatitude(), FavModel.FavLoc.get(i).getLongitude());


        MarkerOptions options = new MarkerOptions().position(Favloc).title(FavModel.FavLoc.get(i).getAddress()).snippet("you are going here").draggable(true)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));


        mMap.addMarker(options);


    }

    public void addToFvt(View view) {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dformat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
        String sdate = dformat.format(calendar.getTime());
        if (place_name == null){
            place_name = "Address";
        }

        if (mDatabase.addlocation(fav_lat, fav_long, place_name, sdate,  isVisited)) {
            Toast.makeText(this, "add", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Employee not added", Toast.LENGTH_SHORT).show();
        }


        FavModel.FavLoc.add(model);
        Toast.makeText(this, "Added to fvt", Toast.LENGTH_SHORT).show();

    }
    public void btnClick(View view) {
        Object[] dataTransfer;
        String url;
        switch (view.getId()) {

            case R.id.btn_restaurant:
                url = getUrl(latitude, longitude, "restaurant");
                dataTransfer = new Object[2];
                dataTransfer[0] = mMap;
                dataTransfer[1] = url;
                NearPlaces getNearbyPlaceData = new NearPlaces();
                getNearbyPlaceData.execute(dataTransfer);
                Toast.makeText(this, "Restaurants", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_museums:
                mMap.clear();
                url = getUrl(latitude, longitude, "museums");
                dataTransfer = new Object[2];
                dataTransfer[0] = mMap;
                dataTransfer[1] = url;
                NearPlaces getNearbyPlaceData1 = new NearPlaces();
                getNearbyPlaceData1.execute(dataTransfer);
                Toast.makeText(this, "Museums", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_school:
                mMap.clear();
                url = getUrl(latitude, longitude, "school");
                dataTransfer = new Object[2];
                dataTransfer[0] = mMap;
                dataTransfer[1] = url;
                NearPlaces getNearbyPlaceData2 = new NearPlaces();
                getNearbyPlaceData2.execute(dataTransfer);
                Toast.makeText(this, "Schools", Toast.LENGTH_SHORT).show();
                break;

            case R.id.go_btn:
                //case R.id.btn_directions:
                dataTransfer = new Object[3];
                url = getDirectionUrl();
                dataTransfer[0] = mMap;
                dataTransfer[1] = url;
                dataTransfer[2] = new LatLng(dirc_lat, direc_long);

                GetDirections getDirections = new GetDirections();
                // execute asynchronously
                getDirections.execute(dataTransfer);
                if (view.getId() == R.id.go_btn){
                    Log.i("CHECK", "btnClick: go button clicked "); directionRequested = false;}
                else{
                    directionRequested = true; Log.i("CHECK", "btnClick: direction clicked ");}


        }
    }
    private String getUrl(double latitude, double longitude, String nearbyPlace) {
        StringBuilder placeUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        placeUrl.append("location="+latitude+","+longitude);
        placeUrl.append("&radius="+RADIUS);
        placeUrl.append("&type="+nearbyPlace);
        placeUrl.append("&key="+getString(R.string.api_key_places));
        return placeUrl.toString();
    }
    private String getDirectionUrl() {
        StringBuilder googleDirectionUrl = new StringBuilder("https://maps.googleapis.com/maps/api/directions/json?");
        googleDirectionUrl.append("origin="+latitude+","+longitude);
        googleDirectionUrl.append("&destination="+dirc_lat+","+direc_long);
        googleDirectionUrl.append("&key="+getString(R.string.api_key_places));
        Log.d("", "getDirectionUrl: "+googleDirectionUrl);
        return googleDirectionUrl.toString();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater  = getMenuInflater();
        inflater.inflate(R.menu.map_menu,menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {



        switch (item.getItemId()){

            case R.id.satellite:
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                Toast.makeText(this, "Satellite map is selected", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.normal:
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                Toast.makeText(this, "Normal map is selected", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.none:
                mMap.setMapType(GoogleMap.MAP_TYPE_NONE);
                Toast.makeText(this, "None map is selected", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.hybrid:
                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                Toast.makeText(this, "hybrid map is selected", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.clear:
                mMap.clear();
            default:
                return super.onOptionsItemSelected(item);

        }


    }

    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        Double  dest_lat = marker.getPosition().latitude;
        Double  dest_long = marker.getPosition().longitude;
        Location location = new Location("");
        location.setLatitude(dest_lat);
        location.setLongitude(dest_long);

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dformat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
        String sdate = dformat.format(calendar.getTime());
        try {

            List<Address> addresses = geocoder.getFromLocation(fav_lat,fav_long,1);
            if(addresses != null && addresses.size() >0) {
                if (addresses.get(0).getSubThoroughfare() != null) {
                    place_name += addresses.get(0).getSubThoroughfare() + "";
                }
                if (addresses.get(0).getThoroughfare() != null) {
                    place_name += addresses.get(0).getThoroughfare() + "";
                }
                if (addresses.get(0).getLocality() != null) {
                    place_name += addresses.get(0).getLocality() + "";
                }
//                model = new FavModel(fav_lat,fav_long,place_name,sdate,isVisited,1);
            }


        } catch (IOException e) {
            e.printStackTrace();
            place_name = "address";
        }


        if(mDatabase.updateLoaction(FavModel.FavLoc.get(i).getId(),dest_lat,dest_long,place_name,sdate,isVisited)){
            Toast.makeText(FavLocation.this, "location update", Toast.LENGTH_SHORT).show();
            loadLocation();
        }
    }

    private  void loadLocation() {
        Cursor cursor = mDatabase.getAllLocation();
        if(cursor.moveToFirst()) {
            FavModel.FavLoc.clear();
        }
        do{
            FavModel.FavLoc.add(new FavModel(cursor.getInt(0),cursor.getDouble(1),cursor.getDouble(2),cursor.getString(3),
                    cursor.getString(4),cursor.getString(5)));

            Log.i("tag", "loadLocation: " + cursor.getInt(0));
        } while(cursor.moveToNext());
        cursor.close();
    }

}
