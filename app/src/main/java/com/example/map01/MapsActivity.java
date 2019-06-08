package com.example.map01;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener {

    private GoogleMap mMap;
    private Marker userMarker, destinationMarker;
    private Location userLocation, destinationLocation;
    private TextView txtShow;
    private static final int REQUEST_CODE = 101;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        txtShow = (TextView) findViewById(R.id.txt_distance_id);
        toolbar = (Toolbar) findViewById(R.id.toolbar_id);



        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

    }

    private void setupLocationManager() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (locationManager != null) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION
                        ,Manifest.permission.ACCESS_COARSE_LOCATION},REQUEST_CODE);
                    }
                    return;
                }
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                onLocationChanged(location);
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,4000,5,this);
            }
        }
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        this.mMap = googleMap;
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                destinationLocation = new Location(LocationManager.GPS_PROVIDER);
                destinationLocation.setLatitude(latLng.latitude);
                destinationLocation.setLongitude(latLng.longitude);
                if (destinationMarker != null){
                    destinationMarker.remove();
                }else {
                    txtShow.setVisibility(View.VISIBLE);
                }
                txtShow.setText(getResources().getString(R.string.distance_label) + String.valueOf(userLocation.distanceTo(destinationLocation)/1000) +
                        getResources().getString(R.string.distance_metric));
                destinationMarker = mMap.addMarker(new MarkerOptions().position(latLng).title("Your Destination"));
            }
        });
        setupLocationManager();
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null && mMap !=null){
            userLocation = location;
            LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
            if (userMarker != null){
                userMarker.remove();
            }else{
                userMarker = mMap.addMarker(new MarkerOptions().position(latLng).title("Your Location"));
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng,15);
                mMap.animateCamera(cameraUpdate);
            }
        }
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE){
            if (grantResults[0] == 0){
                setupLocationManager();
            }
        }
    }
}
