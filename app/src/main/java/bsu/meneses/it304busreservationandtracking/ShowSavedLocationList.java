
package bsu.meneses.it304busreservationandtracking;

import android.location.Location;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class ShowSavedLocationList extends AppCompatActivity {

    ListView lv_savedLocations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_show_saved_location_list);

        lv_savedLocations = findViewById(R.id.lv_waypoints);

        MyApplication myApplication = (MyApplication) getApplicationContext();
        List<Location> savedLocations = myApplication.getMyLocations();

        if (savedLocations != null) {
            lv_savedLocations.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, savedLocations));
        } else {
            // Handle the case where there are no saved locations
            lv_savedLocations.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new String[]{"No saved locations"}));
        }
    }
}