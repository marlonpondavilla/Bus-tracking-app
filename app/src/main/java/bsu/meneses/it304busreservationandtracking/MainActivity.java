package bsu.meneses.it304busreservationandtracking;

import android.Manifest;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static final int DEFAULT_UPDATE_INTERVAL = 30;
    public static final int FAST_UPDATE_INTERVAL = 50;
    private static final int PERMISSION_FINE_LOCATION = 99;

    TextView lati, longi, alt, accuracy, speed, address;
    Switch swUpdates, swGPS;

    //google api pare
    FusedLocationProviderClient fusedLocationProviderClient;

    //all config files ng fused
    LocationRequest locationRequest;

    LocationCallback locationCallback;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        lati = findViewById(R.id.lat_coordinate);
        longi = findViewById(R.id.long_coordinate);
        alt = findViewById(R.id.alt_coordinate);
        accuracy = findViewById(R.id.accur_coordinate);
        speed = findViewById(R.id.spd_coordinate);
        address = findViewById(R.id.address);
        swUpdates = findViewById(R.id.sw_updates);
        swGPS = findViewById(R.id.sw_gps);

        locationRequest = new LocationRequest();
        locationRequest.setInterval(1000 * DEFAULT_UPDATE_INTERVAL);
        locationRequest.setFastestInterval(1000 * FAST_UPDATE_INTERVAL);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        //event triggered whenever update is met
        locationCallback = new LocationCallback() {

            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);

                //save location
                Location location = locationResult.getLastLocation();
                updateUIValues(location);
            }
        };


        swGPS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (swGPS.isChecked()) {
                    locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                    swGPS.setText("Using GPS sensors");
                } else {
                    locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
                    swGPS.setText("off");
                }
            }
        });


        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            //user accepted the permission

            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    // we get permissions
                    updateUIValues(location);

                }
            });
        } else {
            //permission not granted yet

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_FINE_LOCATION);
            }
        }

        swUpdates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (swUpdates.isChecked()) {
                    //turn on tracking
                    startLocationUpdates();
                } else {
                    //turn off tracking
                    stopLocationUpdates();
                }
            }
        });

        updateGPS();
    }//on create method end bracket

    private void startLocationUpdates() {
        swUpdates.setText("location is being tracked");
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
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
        updateGPS();
    }


    private void stopLocationUpdates() {
        swUpdates.setText("location is NOT being tracked");
        lati.setText("Not tracking location");
        longi.setText("Not tracking location");
        accuracy.setText("Not tracking location");
        speed.setText("Not tracking location");
        alt.setText("Not tracking location");
        address.setText("Not tracking location");

        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){
            case PERMISSION_FINE_LOCATION:
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    updateGPS();
                }
                else{
                    Toast.makeText(this, "This app requires permission to work properly", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
        }
    }


    private void updateGPS(){


    }
    private void updateUIValues(Location location) {
        lati.setText(String.valueOf(location.getLatitude()));
        longi.setText(String.valueOf(location.getLongitude()));
        accuracy.setText(String.valueOf(location.getAccuracy()));

        if(location.hasAltitude()){
            alt.setText(String.valueOf(location.getAltitude()));
        }
        else{
            alt.setText("Not available");
        }

        if(location.hasSpeed()){
            speed.setText(String.valueOf(location.getSpeed()));
        }
        else{
            speed.setText("Not available");
        }

        //this is it pancit shit
        Geocoder geocoder = new Geocoder(MainActivity.this);

        try{
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

            //change this na lang if we'll get other location stuff
            address.setText(addresses.get(0).getAddressLine(0) + ", " + addresses.get(0));
        }
        catch (Exception e){
            address.setText("Unable to get street adress");
            Toast.makeText(this, "Cannot provide the street address", Toast.LENGTH_SHORT).show();
        }

    }
}


