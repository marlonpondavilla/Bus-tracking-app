package bsu.meneses.it304busreservationandtracking;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;

public class FirebaseUI extends AppCompatActivity {

    // Firebase sign-in launcher
    private ActivityResultLauncher<Intent> signInLauncher;
    private Button locationPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firebase_ui);

        locationPage = findViewById(R.id.btn_location_page);

        locationPage.setOnClickListener(v -> {
            Intent intent = new Intent(FirebaseUI.this, MainActivity.class);
            startActivity(intent);
        });

        // Initialize the sign-in launcher
        signInLauncher = registerForActivityResult(
                new FirebaseAuthUIActivityResultContract(),
                result -> {
                    IdpResponse response = result.getIdpResponse();
                    if (result.getResultCode() == RESULT_OK) {
                        // Sign-in successful
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                        //redirect to main page if sign in successful check setContentView(R.layout.activity_firebase_ui);

                        if (user != null) {
                            // Display the user's email after successful login
                            String email = user.getEmail();
                            Toast.makeText(FirebaseUI.this, "Welcome, " + email, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // Sign-in failed or canceled, handle the error
                        if (response != null && response.getError() != null) {
                            // Log or show the error
                            Toast.makeText(FirebaseUI.this, "Error: " + response.getError().getMessage(), Toast.LENGTH_SHORT).show();
                        } else {
                            // Canceled case
                            Toast.makeText(FirebaseUI.this, "Sign-in canceled", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        // Start the sign-in process
        startSignIn();
    }

    // Method to start the FirebaseUI sign-in flow
    private void startSignIn() {
        // Build the sign-in intent with Google and Email providers
        Intent signInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(
                        Arrays.asList(
                                new AuthUI.IdpConfig.GoogleBuilder().build(), // Google Sign-In
                                new AuthUI.IdpConfig.EmailBuilder().build()  // Email Sign-In
                        )
                )
                .build();

        // Launch the sign-in activity
        signInLauncher.launch(signInIntent);
    }
}
