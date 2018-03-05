package com.homeless;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.homeless.Logic.User;

public class UserDetails extends AppCompatActivity {
    private static final String TAG = "UserDetails";
    private boolean userSignedIn;
    private User theUser;
    private DatabaseReference mDatabase;
    // UI references.
    private EditText name,phone,mEmail, mPassword;
    private Button btnSubmit;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);
        initUi();
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        setAuthListner();

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!validateParams()) {
                    return;
                }
                if (theUser == null) {
                    initUser();
                    createUser(theUser.geteMail(), theUser.getPassword());
                }
                else {
                    saveUser();
                }
            }
        });
    }

    private void saveUser(){

    }
    private void setAuthListner(){
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };
    }
    private void initUser (){
        theUser = new User("",name.getText().toString(),phone.getText().toString()
                ,mPassword.getText().toString(),mEmail.getText().toString());
    }

    private boolean validateParams(){
        return true;
    }

    private void initUi(){
        name  = (EditText) findViewById(R.id.edit_name);
        phone = (EditText) findViewById(R.id.edit_phone);
        mEmail = (EditText) findViewById(R.id.edit_email);
        mPassword = (EditText) findViewById(R.id.edit_password);
        btnSubmit = (Button) findViewById(R.id.user_details_submit_button);
    }

    private void loadDetailsToScreen(User user){
        name.setText(user.getName());
        phone.setText(user.getPhone());
        mEmail.setText(user.geteMail());
        mPassword.setText(user.getPassword());
    }

    private void createUser(String email, String password){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            mDatabase.child("Users").child(user.getUid()).setValue(theUser);
                            Intent intent = new Intent(UserDetails.this, UserDashboard.class);
                            startActivity(intent);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(UserDetails.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if ( currentUser!= null) {
            final String currentUserID = currentUser.getUid();
            //theUser = mDatabase.child("Users").child(currentUser.getUid()).getDatabase();
            mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    theUser = dataSnapshot.child("Users").child(currentUserID).getValue(User.class);
                    //Log.e(TAG, "onDataChange: " + (User)(dataSnapshot.child("Users").child(currentUserID).getValue()));
                    loadDetailsToScreen(theUser);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

    }
}
