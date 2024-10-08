package userinterfaces;

import static android.app.PendingIntent.getActivity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import bsu.meneses.it304busreservationandtracking.R;
import bsu.meneses.it304busreservationandtracking.databinding.ActivityUserPageBinding;


public class UserPage extends AppCompatActivity {

//    TextView tv_name, tv_email, tv_phone, tv_uid;
//    ImageView userPhoto;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityUserPageBinding binding;
        binding = ActivityUserPageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        replaceFragment(new HomeFragment());

        binding.navBar.setOnItemSelectedListener(item -> {

            if(item.getItemId() == R.id.home) {
                replaceFragment(new HomeFragment());
            } else if(item.getItemId() == R.id.ticket) {
                replaceFragment(new TicketFragment());
            }

            return true;
        });

    }

    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout,fragment,null);
        fragmentTransaction.commit();
    }
}


// Initialize TextViews
//        tv_name = findViewById(R.id.user_name);
//        tv_email = findViewById(R.id.user_email);
//        tv_phone = findViewById(R.id.user_phone);
//        tv_uid = findViewById(R.id.user_id);
//
//        userPhoto = findViewById(R.id.user_image);
//
//        // Get the data from Intent
//        String name = getIntent().getStringExtra("name");
//        String email = getIntent().getStringExtra("email");
//        String phoneNo = getIntent().getStringExtra("phone");
//        String uid = getIntent().getStringExtra("uid");
//        String photoURL = getIntent().getStringExtra("photo");
//
//        // Set the data to TextViews
//        if (name != null) {
//            tv_name.setText(name);
//        }
//
//        if (email != null) {
//            tv_email.setText(email);
//        }
//
//        if (phoneNo != null) {
//            tv_phone.setText(phoneNo); // Set phoneNo instead of email
//        }
//
//        if (uid != null) {
//            tv_uid.setText(uid); // Set uid instead of email
//        }
//
//        // Load the user's photo using Glide
//        if (photoURL != null && !photoURL.isEmpty()) {
//            Glide.with(this)
//                    .load(Uri.parse(photoURL))
//                    .into(userPhoto);
//        } else {
//            userPhoto.setImageResource(R.drawable.ic_launcher_background); // Default profile image if no URL
//        }