package bsu.meneses.it304busreservationandtracking;

import android.app.Application;
import android.location.Location;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class MyApplication extends Application {
    private static MyApplication singleton;
    private List<Location> myLocations;

    @Override
    public void onCreate() {
        super.onCreate();
        singleton = this;
        myLocations = new ArrayList<>();

        // Initialize Firebase
        FirebaseApp.initializeApp(MyApplication.this);
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }

    public static MyApplication getInstance() {
        return singleton;
    }

    public List<Location> getMyLocations() {
        return myLocations;
    }

    public void setMyLocations(List<Location> myLocations) {
        this.myLocations = myLocations;
    }
}