package com.homeless;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.homeless.Logic.Review;
import com.homeless.Logic.ReviewListAdapter;

import java.util.ArrayList;
import java.util.List;

public class StreeReviewsActivity extends AppCompatActivity {
    private static final String TAG = "StreetReviewsActivity";
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private ArrayList<Review> reviews;
    private ListView reviewsListView;
    private String city,street;
    private TextView avgScore,pricePerM,numOfReviews;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stree_reviews);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        initUI();
        setListViewLitener();

        //extract street and city
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            city = extras.getString("City");
            street = extras.getString("Street");
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        loadReviewsFromDB();

    }

    private void setListViewLitener(){
        reviewsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(StreeReviewsActivity.this, ReviewActivity.class);
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
        reviewsListView = (ListView)findViewById(R.id.reviewsListStreet);
        avgScore = (TextView)findViewById(R.id.avgScoreInput);
        pricePerM = (TextView)findViewById(R.id.pricePerMInput);
        numOfReviews = (TextView)findViewById(R.id.numOfReviewsInput);
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
                    double sumScore = 0,sumAvgPriceSize = 0;
                    for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                        Review itr = postSnapshot.getValue(Review.class);
                        if (itr.getCity().compareTo(city)==0 && itr.getStreet().compareTo(street)==0){
                            myReviews.add(itr);
                            sumScore+=itr.getScore();
                            if (itr.getSize()!=0 && itr.getPrice()!=0){
                                sumAvgPriceSize += (itr.getPrice()/itr.getSize());
                            }
                            Log.d(TAG, "onDataChange: review"+ itr.getId()+" loaded");
                        }
                    }
                    reviews = (ArrayList<Review>)myReviews;
                    numOfReviews.setText(reviews.size()+"");
                    pricePerM.setText(String.format("%.2f", sumAvgPriceSize/reviews.size()));
                    avgScore.setText(String.format("%.2f",sumScore/reviews.size()));
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
