package bsu.meneses.it304busreservationandtracking;

import android.Manifest;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

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
        locationRequest.setInterval(30000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);


        swGPS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(swGPS.isChecked()){
                    locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                    swGPS.setText("Using GPS sensors");
                }
                else{
                    locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
                    swGPS.setText("off");
                }
            }
        });


        fusedLocationProviderClient  = LocationServices.getFusedLocationProviderClient(this);

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            //user accepted the permission

            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    // we get permissions
                    updateUIValues(location);

                }
            });
        }
        else{
            //permission not granted yet

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_FINE_LOCATION);
            }
        }

        updateGPS();
    }//on create method end bracket

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

    }
}


