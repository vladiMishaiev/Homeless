package com.homeless;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UserDashboard extends AppCompatActivity {
    private static final String TAG = "UserDashboardActivity";
    private FirebaseAuth mAuth;
    private Button btnSignOut,editAccount,btnMap,btnMyReview;
    private FirebaseAuth.AuthStateListener mAuthListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_dashboard);

        mAuth = FirebaseAuth.getInstance();
        setAuthListner();
        initUI();
        setButtons();
    }

    private void initUI(){
        btnSignOut = (Button) findViewById(R.id.user_signout_button);
        editAccount = (Button) findViewById(R.id.user_edit_button);
        btnMap = (Button) findViewById(R.id.user_map_button);
        btnMyReview = (Button) findViewById(R.id.user_reviews_button);
    }

    private void setButtons(){
        btnMyReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserDashboard.this, MyReviewsActivity.class);
                startActivity(intent);
            }
        });
        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserDashboard.this, MapActivity.class);
                startActivity(intent);
            }
        });
        editAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserDashboard.this, UserDetails.class);
                startActivity(intent);
            }
        });
        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                toastMessage("Signing Out...");
                Intent intent = new Intent(UserDashboard.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private void toastMessage(String message){
        Toast.makeText(this,message, Toast.LENGTH_SHORT).show();
    }
    private void setAuthListner(){
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    toastMessage("Successfully signed in with: " + user.getEmail());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    toastMessage("Successfully signed out.");
                }
                // ...
            }
        };
    }
}
