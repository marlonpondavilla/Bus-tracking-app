package bsu.meneses.it304busreservationandtracking;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.location.Location;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import bsu.meneses.it304busreservationandtracking.databinding.ActivityMapsBinding;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;

    List<Location> savedLocations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Get the singleton instance of MyApplication
        MyApplication myApplication = MyApplication.getInstance();
        savedLocations = myApplication.getMyLocations();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        BitmapDescriptor customIcon = BitmapDescriptorFactory.fromResource(R.drawable.img);
//        BitmapDescriptor defaultIcon = BitmapDescriptorFactory.defaultMarker();


        LatLng bulakan = new LatLng(-34, 151);
        LatLng lastLocationPlaced = bulakan;

        if (savedLocations != null && !savedLocations.isEmpty()) {
            for (Location location : savedLocations) {
                LatLng myLocation = new LatLng(location.getLatitude(), location.getLongitude());

                mMap.addMarker(new MarkerOptions().position(myLocation).title("Bussing 😝").icon(customIcon));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(myLocation));
                lastLocationPlaced = myLocation;
            }
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(lastLocationPlaced, 15));
            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(@NonNull Marker marker) {
                    //count the number of clicks
                    Integer clicks = (Integer) marker.getTag();
                    if(clicks == null){
                        clicks = 0;
                    }
                    else {
                        clicks++;
                        marker.setTag(clicks);
                        Toast.makeText(MapsActivity.this, "Bussing was clicked " + marker.getTag(), Toast.LENGTH_SHORT).show();
                    }

                    return false;
                }
            });
        } else {
            // default cam
            mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(0, 0))); // Move camera to a default position
        }
    }
}
