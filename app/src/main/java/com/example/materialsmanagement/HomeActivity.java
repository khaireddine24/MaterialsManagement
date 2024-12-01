package com.example.materialsmanagement;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class HomeActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private ImageView userProfileImage;
    private TextView usernameText;
    private TextView emailText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        userProfileImage = findViewById(R.id.userProfileImage);
        usernameText = findViewById(R.id.usernameText);
        emailText = findViewById(R.id.emailText);

        updateUserProfile();

        findViewById(R.id.materialCard).setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, MaterialManagement.class);
            startActivity(intent);
        });

        findViewById(R.id.classCard).setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, ClassManagementActivity.class);
            startActivity(intent);
        });

        Button logoutButton = findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(v -> logout());
    }

    private void updateUserProfile() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            userProfileImage.setImageResource(R.drawable.user_icon);

            db.collection("users")
                    .document(currentUser.getUid())
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String userName = documentSnapshot.getString("username");
                            if (userName != null) {
                                usernameText.setText(userName);
                            } else {
                                usernameText.setText("User");
                            }
                        } else {
                            usernameText.setText("User");
                        }
                    })
                    .addOnFailureListener(e -> {
                        usernameText.setText("User");
                        Log.e("HomeActivity", "Error getting username from Firestore: " + e.getMessage());
                    });

            emailText.setText(currentUser.getEmail());
        }
    }
    private void logout() {
        mAuth.signOut();
        Intent intent = new Intent(HomeActivity.this, Login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}