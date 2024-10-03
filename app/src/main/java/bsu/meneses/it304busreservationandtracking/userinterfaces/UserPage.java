package bsu.meneses.it304busreservationandtracking.userinterfaces;

import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import bsu.meneses.it304busreservationandtracking.R;

public class UserPage extends AppCompatActivity {

    TextView tv_name, tv_email, tv_phone, tv_uid;
    ImageView userPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_page);

        // Initialize TextViews
        tv_name = findViewById(R.id.user_name);
        tv_email = findViewById(R.id.user_email);
        tv_phone = findViewById(R.id.user_phone);
        tv_uid = findViewById(R.id.user_id);

        userPhoto = findViewById(R.id.user_image);

        // Get the data from Intent
        String name = getIntent().getStringExtra("name");
        String email = getIntent().getStringExtra("email");
        String phoneNo = getIntent().getStringExtra("phone");
        String uid = getIntent().getStringExtra("uid");
        String photoURL = getIntent().getStringExtra("photo");

        // Set the data to TextViews
        if (name != null) {
            tv_name.setText(name);
        }

        if (email != null) {
            tv_email.setText(email);
        }

        if (phoneNo != null) {
            tv_phone.setText(phoneNo); // Set phoneNo instead of email
        }

        if (uid != null) {
            tv_uid.setText(uid); // Set uid instead of email
        }

        // Load the user's photo using Glide
        if (photoURL != null && !photoURL.isEmpty()) {
            Glide.with(this)
                    .load(Uri.parse(photoURL))
                    .into(userPhoto);
        } else {
            userPhoto.setImageResource(R.drawable.ic_launcher_background); // Default profile image if no URL
        }
    }
}
