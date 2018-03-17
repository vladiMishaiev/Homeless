package com.homeless;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.homeless.Logic.Review;
import com.homeless.Logic.ReviewListAdapter;
import com.homeless.Logic.User;

import java.util.ArrayList;
import java.util.List;

public class MyReviewsActivity extends AppCompatActivity {
    private static final String TAG = "MyReviewsActivity";
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private ArrayList<Review> reviews;
    private ListView reviewsListView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_reviews);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        initUI();
        setListViewLitener();
    }

    @Override
    public void onStart() {
        super.onStart();
        loadReviewsFromDB();

    }

    private void setListViewLitener(){
        reviewsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MyReviewsActivity.this, ReviewActivity.class);
                intent.putExtra("ReviewID", reviews.get(position).getId());
                startActivity(intent);
            }
        });
    }

    private void updateListUI(){
        ReviewListAdapter adapter = new ReviewListAdapter(this, R.layout.adapter_summary_view_layout, reviews);
        reviewsListView.setAdapter(adapter);
    }

    private void initUI(){
        reviewsListView = (ListView)findViewById(R.id.reviewsList);
    }

    private void loadReviewsFromDB(){
        final List<Review> myReviews = new ArrayList<>();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if ( currentUser!= null) {
            final String currentUserID = currentUser.getUid();
            //theUser = mDatabase.child("Users").child(currentUser.getUid()).getDatabase();
            mDatabase.child("Reviews").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                        Review itr = postSnapshot.getValue(Review.class);
                        if (itr.getUserIdl().compareTo(currentUserID)==0){
                            myReviews.add(itr);
                            Log.d(TAG, "onDataChange: review"+ itr.getId()+" loaded");
                        }
                    }
                    reviews = (ArrayList<Review>)myReviews;
                    updateListUI();
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e(TAG, "onCancelled: failed loading reviews from DB");
                }
            });
        }

    }


}
