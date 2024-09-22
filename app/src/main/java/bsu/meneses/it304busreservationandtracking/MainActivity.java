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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static final int DEFAULT_UPDATE_INTERVAL = 30;
    public static final int FAST_UPDATE_INTERVAL = 50;
    private static final int PERMISSION_FINE_LOCATION = 99;
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;

    // Firebase
    private DatabaseReference databaseReference;

    TextView lati, longi, alt, accuracy, speed, address, wayPointCounts;
    Switch swUpdates, swGPS;
    Button showWayPointBtn, showMapBtn;

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

        // Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference("locations");

        lati = findViewById(R.id.lat_coordinate);
        longi = findViewById(R.id.long_coordinate);
        alt = findViewById(R.id.alt_coordinate);
        accuracy = findViewById(R.id.accur_coordinate);
        speed = findViewById(R.id.spd_coordinate);
        address = findViewById(R.id.address);
        wayPointCounts = findViewById(R.id.tv_countOfCrumbs);

        swUpdates = findViewById(R.id.sw_updates);
        swGPS = findViewById(R.id.sw_gps);

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

        showWayPointBtn.setOnClickListener(view -> {
            Intent i = new Intent(MainActivity.this, ShowSavedLocationList.class);
            startActivity(i);
        });

        showMapBtn.setOnClickListener(view -> {
            // Save the current location as a waypoint
            if (currentLocation != null) {
                MyApplication myApplication = MyApplication.getInstance();
                savedLocations = myApplication.getMyLocations();
                savedLocations.add(currentLocation);
                wayPointCounts.setText(Integer.toString(savedLocations.size()));
            } else {
                Toast.makeText(MainActivity.this, "Current location is not available.", Toast.LENGTH_SHORT).show();
            }

            Intent i = new Intent(MainActivity.this, MapsActivity.class);
            startActivity(i);
        });

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        checkPermissionsAndUpdateGPS();

        // Check if the RECORD_AUDIO permission is already granted
        if (!hasMicrophonePermission()) {
            promptMicrophonePermission();
        }

        // Check if location services are enabled
        if (!isLocationServiceEnabled()) {
            promptUserToEnableLocation();
        }
    }

    private boolean isLocationServiceEnabled() {
        int locationMode = 0;
        try {
            locationMode = Settings.Secure.getInt(getContentResolver(), Settings.Secure.LOCATION_MODE);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        return locationMode != Settings.Secure.LOCATION_MODE_OFF;
    }

    private void promptUserToEnableLocation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Location services are disabled. Do you want to enable them?")
                .setPositiveButton("Yes", (dialog, id) -> {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                })
                .setNegativeButton("No", (dialog, id) -> dialog.dismiss())
                .create()
                .show();
    }

    private boolean hasMicrophonePermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED;
    }

    private void promptMicrophonePermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_RECORD_AUDIO_PERMISSION);
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
        } else if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with recording audio
            } else {
                // Permission denied, handle accordingly
                Toast.makeText(this, "RECORD_AUDIO permission is required for this feature.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void updateGPS() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(location -> {
                        if (location != null) {
                            updateUIValues(location);
                            currentLocation = location; // Store the last known location
                            saveLocation(currentLocation); // Save using CustomLocation
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
                retrieveLocations();
            } else {
                address.setText("Address not available");
            }
        } catch (Exception e) {
            address.setText("Unable to get street address");
            Toast.makeText(this, "Cannot provide the street address", Toast.LENGTH_SHORT).show();
        }

        // Get instance to save the actual data
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

    // Firebase methods
    private void saveLocation(Location location) {
        String locationId = databaseReference.push().getKey();
        if (locationId != null) {
            CustomLocation customLocation = new CustomLocation(
                    location.getLatitude(),
                    location.getLongitude(),
                    (float) location.getAltitude(),
                    location.getAccuracy(),
                    location.getSpeed()
            );

            databaseReference.child(locationId).setValue(customLocation)
                    .addOnSuccessListener(aVoid -> Toast.makeText(MainActivity.this, "Location saved", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(MainActivity.this, "Failed to save location", Toast.LENGTH_SHORT).show());
        }
    }


    private void retrieveLocations() {
        databaseReference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (DataSnapshot snapshot : task.getResult().getChildren()) {
                    CustomLocation customLocation = snapshot.getValue(CustomLocation.class);
                    if (customLocation != null) {
                        // Handle the retrieved customLocation, e.g., add it to a list or update UI
                        // Example: savedLocations.add(customLocation);
                    }
                }
            } else {
                Toast.makeText(MainActivity.this, "Failed to retrieve locations", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
