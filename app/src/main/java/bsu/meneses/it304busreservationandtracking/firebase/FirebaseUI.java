package bsu.meneses.it304busreservationandtracking.firebase;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;

import bsu.meneses.it304busreservationandtracking.R;
import userinterfaces.UserPage;

public class FirebaseUI extends AppCompatActivity {

    // Firebase sign-in launcher
    private ActivityResultLauncher<Intent> signInLauncher;
    TextView userName, userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_page);

        // Initialize the TextViews
//        userName = findViewById(R.id.user_name);
//        userEmail = findViewById(R.id.user_email);  // This was previously incorrect

        // Initialize the sign-in launcher
        signInLauncher = registerForActivityResult(
                new FirebaseAuthUIActivityResultContract(),
                result -> {
                    IdpResponse response = result.getIdpResponse();
                    if (result.getResultCode() == RESULT_OK) {
                        // Sign-in successful
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                        if (user != null) {
                            // Pass the user data (name and email) to UserPage
                            Intent intent = new Intent(FirebaseUI.this, UserPage.class);
                            intent.putExtra("uid", user.getUid());
                            intent.putExtra("name", user.getDisplayName());
                            intent.putExtra("email", user.getEmail());
                            intent.putExtra("phone", user.getPhoneNumber());
                            intent.putExtra("photo", user.getPhotoUrl().toString());
                            startActivity(intent);

                            // Optionally show a welcome message
                            Toast.makeText(FirebaseUI.this, "Welcome, " + user.getEmail(), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // Sign-in failed or canceled, handle the error
                        if (response != null && response.getError() != null) {
                            Toast.makeText(FirebaseUI.this, "Error: " + response.getError().getMessage(), Toast.LENGTH_SHORT).show();
                        } else {
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

    // Method to sign out the current user
    private void signOut() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(task -> {
                    Toast.makeText(FirebaseUI.this, "Sign-out successful", Toast.LENGTH_SHORT).show();
                });
    }
}
