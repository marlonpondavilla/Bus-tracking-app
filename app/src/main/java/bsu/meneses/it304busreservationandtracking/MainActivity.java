package bsu.meneses.it304busreservationandtracking;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

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

    TextView lati, longi, alt, accuracy, speed, address, wayPointCounts;
    Switch swUpdates, swGPS;
    Button newWayPointBtn, showWayPointBtn, showMapBtn;

    // Current location
    Location currentLocation;

    // List of saved locations
    List<Location> savedLocations;

    // Google API client
    FusedLocationProviderClient fusedLocationProviderClient;

    // Location request and callback
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
        wayPointCounts = findViewById(R.id.tv_countOfCrumbs);

        swUpdates = findViewById(R.id.sw_updates);
        swGPS = findViewById(R.id.sw_gps);

        newWayPointBtn = findViewById(R.id.btn_newWayPoint);
        showWayPointBtn = findViewById(R.id.btn_showWayPoint);
        showMapBtn = findViewById(R.id.btn_showMap);

        // Configure LocationRequest
        locationRequest = new LocationRequest();
        locationRequest.setInterval(1000 * DEFAULT_UPDATE_INTERVAL);
        locationRequest.setFastestInterval(1000 * FAST_UPDATE_INTERVAL);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        // Initialize LocationCallback
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                Location location = locationResult.getLastLocation();
                if (location != null) {
                    updateUIValues(location);
                }
            }
        };


        swGPS.setOnClickListener(view -> {
            if (swGPS.isChecked()) {
                locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                swGPS.setText("Using GPS sensors");
            } else {
                locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
                swGPS.setText("off");
            }
        });

        swUpdates.setOnClickListener(view -> {
            if (swUpdates.isChecked()) {
                // Start tracking
                startLocationUpdates();
            } else {
                // Stop tracking
                stopLocationUpdates();
            }
        });

        newWayPointBtn.setOnClickListener(view -> {
            // Save the current location as a waypoint
            if (currentLocation != null) {
                MyApplication myApplication = MyApplication.getInstance();
                savedLocations = myApplication.getMyLocations();
                savedLocations.add(currentLocation);
                wayPointCounts.setText(Integer.toString(savedLocations.size()));
            } else {
                Toast.makeText(MainActivity.this, "Current location is not available.", Toast.LENGTH_SHORT).show();
            }
        });

        showWayPointBtn.setOnClickListener(view -> {
            Intent i = new Intent(MainActivity.this, ShowSavedLocationList.class);
            startActivity(i);

        });

        showMapBtn.setOnClickListener(view -> {
            Intent i = new Intent(MainActivity.this, MapsActivity.class);
            startActivity(i);
        });

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        checkPermissionsAndUpdateGPS();
    }

    private void checkPermissionsAndUpdateGPS() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Permissions are granted
            fusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(this, location -> {
                        if (location != null) {
                            updateUIValues(location);
                            currentLocation = location;
                        } else {
                            Toast.makeText(MainActivity.this, "Unable to get location, please turn on your location", Toast.LENGTH_SHORT).show();
                            Toast.makeText(MainActivity.this, "This app might not work properly.", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            // Request permissions
            requestLocationPermission();
        }
    }

    private void requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            new AlertDialog.Builder(this)
                    .setTitle("Permission Needed")
                    .setMessage("This app requires location permission to function properly.")
                    .setPositiveButton("OK", (dialog, which) -> ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            PERMISSION_FINE_LOCATION))
                    .create()
                    .show();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION_FINE_LOCATION);
        }
    }

    private void startLocationUpdates() {
        swUpdates.setText("Location is being tracked");
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
            updateGPS();
        } else {
            requestLocationPermission();
        }
    }

    private void stopLocationUpdates() {
        swUpdates.setText("Location is NOT being tracked");
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

        if (requestCode == PERMISSION_FINE_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationUpdates();
                updateGPS();
            } else {
                Toast.makeText(this, "This app requires permission to work properly", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private void updateGPS() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(location -> {
                        if (location != null) {
                            updateUIValues(location);
                            currentLocation = location;
                        } else {
                            Toast.makeText(MainActivity.this, "Unable to get location.", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            requestLocationPermission();
        }
    }

    private void updateUIValues(Location location) {
        lati.setText(String.valueOf(location.getLatitude()));
        longi.setText(String.valueOf(location.getLongitude()));
        accuracy.setText(String.valueOf(location.getAccuracy()));

        alt.setText(location.hasAltitude() ? String.valueOf(location.getAltitude()) : "Not available");
        speed.setText(location.hasSpeed() ? String.valueOf(location.getSpeed()) : "Not available");

        Geocoder geocoder = new Geocoder(MainActivity.this);

        try {
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (addresses != null && !addresses.isEmpty()) {
                address.setText(addresses.get(0).getAddressLine(0));
            } else {
                address.setText("Address not available");
            }
        } catch (Exception e) {
            address.setText("Unable to get street address");
            Toast.makeText(this, "Cannot provide the street address", Toast.LENGTH_SHORT).show();
        }

        //get Instance pare wag new Instance to save the actual data in getter!!
        MyApplication myApplication = MyApplication.getInstance();
        savedLocations = myApplication.getMyLocations();
        wayPointCounts.setText(Integer.toString(savedLocations.size()));
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (swUpdates.isChecked()) {
            startLocationUpdates();
        }
    }
}
