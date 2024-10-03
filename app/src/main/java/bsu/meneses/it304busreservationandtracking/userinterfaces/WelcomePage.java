package bsu.meneses.it304busreservationandtracking.userinterfaces;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import bsu.meneses.it304busreservationandtracking.R;
import bsu.meneses.it304busreservationandtracking.firebase.FirebaseUI;

public class WelcomePage extends AppCompatActivity {

    Button getStarted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_welcome_page);

        getStarted = findViewById(R.id.get_started_button);

        getStarted.setOnClickListener(v -> {
            // Redirect to FirebaseUI
            Intent intent = new Intent(WelcomePage.this, FirebaseUI.class);
            startActivity(intent);
        });

    }
}