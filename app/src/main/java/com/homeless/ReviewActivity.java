package com.homeless;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Spinner;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.homeless.Logic.Review;
import com.homeless.Logic.User;

public class ReviewActivity extends AppCompatActivity {

    private EditText addressField,userReview;
    private Spinner numOfRooms,floor;
    private EditText size,price;
    private CheckBox AC,bars,parking,elevators,accessFoDisabled,safetyRoom,terrace,sunTerrace,storage,renovated;
    private CheckBox shared,petsAllowed,isLongTermLease,isUnit,furnished,boiler;
    private RatingBar scoreBar;
    private Button submit;
    private Review myReview;

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        initUI();
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            int value = extras.getInt("ReviewID");
            if (value!=0)
                loadReview(value);
            else {
                setAddressDetails(extras.getDouble("Lat"),extras.getDouble("Lon"),extras.getString("City"),
                        extras.getString("Street"),extras.getString("Address"));
            }
        }

        setButtons();
    }

    private void setAddressDetails(double lat, double lon, String city, String street, String address) {
        myReview = new Review();
        myReview.setLat(lat);
        myReview.setLon(lon);
        myReview.setCity(city);
        myReview.setStreet(street);
        addressField.setText(address);
    }

    private void loadReview(final int id){
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                myReview = dataSnapshot.child("Reviews").child(id+"").getValue(Review.class);
                updateReviewUI();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private int getNextReviewID (){
        SharedPreferences settings = getApplicationContext().getSharedPreferences("AppInfo", MODE_PRIVATE);
        int reviewID = settings.getInt("ReviewIDCounter", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt("ReviewIDCounter",reviewID+1);
        editor.apply();

        return reviewID+1;
    }

    private void setButtons(){
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUser user = mAuth.getCurrentUser();
                //make new review
                if (myReview==null){
                    int id = getNextReviewID();
                    myReview = new Review(id,user.getUid());
                }else if (myReview.getId()==0){
                    int id = getNextReviewID();
                    myReview.setId(id);
                    myReview.setUserIdl(user.getUid());
                }
                updateReviewProps();

                //save review
                mDatabase.child("Reviews").child(myReview.getId()+"").setValue(myReview);

                //Go to Map or User dashboard
                Intent intent = new Intent(ReviewActivity.this, UserDashboard.class);
                startActivity(intent);
            }
        });
    }

    private void updateReviewUI(){
        addressField.setText(myReview.getAddress());
        numOfRooms.setSelection(getIndex(numOfRooms,myReview.getNumOfRooms()+""));
        floor.setSelection(getIndex(floor,myReview.getFloor()+""));

        size.setText(myReview.getSize()+"");
        price.setText(myReview.getPrice()+"");

        AC.setChecked(myReview.isAC());
        bars.setChecked(myReview.isBars());
        parking.setChecked(myReview.isParking());
        elevators.setChecked(myReview.isElevators());
        accessFoDisabled.setChecked(myReview.isAccessFoDisabled());
        safetyRoom.setChecked(myReview.isSafetyRoom());
        terrace.setChecked(myReview.isTerrace());
        storage.setChecked(myReview.isStorage());
        shared.setChecked(myReview.isShared());
        petsAllowed.setChecked(myReview.isPetsAllowed());
        isLongTermLease.setChecked(myReview.isLongTermLease());
        isUnit.setChecked(myReview.isUnit());
        furnished.setChecked(myReview.isFurnished());
        boiler.setChecked(myReview.isBoiler());

        userReview.setText(myReview.getReview());
        scoreBar.setRating((float)myReview.getScore());
    }

    private void updateReviewProps(){
        myReview.setAddress(addressField.getText().toString());
        myReview.setNumOfRooms(Double.parseDouble(numOfRooms.getSelectedItem().toString()));
        myReview.setFloor(Integer.parseInt(floor.getSelectedItem().toString()));
        myReview.setSize(Double.parseDouble(size.getText().toString()));
        myReview.setPrice(Double.parseDouble(price.getText().toString()));

        myReview.setAC(AC.isChecked());
        myReview.setBars(bars.isChecked());
        myReview.setParking(parking.isChecked());
        myReview.setElevators(elevators.isChecked());
        myReview.setAccessFoDisabled(accessFoDisabled.isChecked());
        myReview.setSafetyRoom(safetyRoom.isChecked());
        myReview.setTerrace(terrace.isChecked());
        myReview.setSafetyRoom(safetyRoom.isChecked());
        myReview.setStorage(storage.isChecked());
        myReview.setShared(shared.isChecked());
        myReview.setPetsAllowed(petsAllowed.isChecked());
        myReview.setLongTermLease(isLongTermLease.isChecked());
        myReview.setUnit(isUnit.isChecked());
        myReview.setFurnished(furnished.isChecked());
        myReview.setBoiler(boiler.isChecked());

        myReview.setReview(userReview.getText().toString());
        myReview.setScore(scoreBar.getRating());
    }
    private void initUI(){
        addressField = (EditText)findViewById(R.id.ReviewAddress_Input);
        userReview = (EditText)findViewById(R.id.ReviewInput);

        numOfRooms = (Spinner)findViewById(R.id.Rooms_Spinner);
        Double[] items1 = new Double[]{1.0,1.5,2.0,2.5,3.0,3.5,4.0,4.5,5.0,5.5};
        ArrayAdapter<Double> adapter1 = new ArrayAdapter<Double>(this,android.R.layout.simple_spinner_item, items1);
        numOfRooms.setAdapter(adapter1);

        floor = (Spinner)findViewById(R.id.Floor_Spinner);
        Integer[] items2 = new Integer[]{0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30};
        ArrayAdapter<Integer> adapter2 = new ArrayAdapter<Integer>(this,android.R.layout.simple_spinner_item, items2);
        floor.setAdapter(adapter2);

        size = (EditText)findViewById(R.id.Size_Input);
        price = (EditText)findViewById(R.id.PriceInput);

        AC = (CheckBox)findViewById(R.id.AC_box);
        bars = (CheckBox)findViewById(R.id.Bars_Box);
        parking = (CheckBox)findViewById(R.id.Parking_Box);
        elevators = (CheckBox)findViewById(R.id.Elevator_Box);
        accessFoDisabled = (CheckBox)findViewById(R.id.AccessForDisabled_Box);
        safetyRoom = (CheckBox)findViewById(R.id.Safety_Room_Box);
        terrace = (CheckBox)findViewById(R.id.Terrace_Box);
        sunTerrace = (CheckBox)findViewById(R.id.Sun_Terrace_Box);
        storage = (CheckBox)findViewById(R.id.Storage_Box);
        renovated = (CheckBox)findViewById(R.id.Renovated_Box);
        shared = (CheckBox)findViewById(R.id.Shared_Box);
        petsAllowed = (CheckBox)findViewById(R.id.Pets_Box);
        isLongTermLease = (CheckBox)findViewById(R.id.LongTermLease_Box);
        isUnit = (CheckBox)findViewById(R.id.Unit_Box);
        furnished = (CheckBox)findViewById(R.id.Furnished_Box);
        boiler = (CheckBox)findViewById(R.id.Boiler_Box);

        scoreBar = (RatingBar)findViewById(R.id.ratingBar);
        submit = (Button)findViewById(R.id.review_submit_button);
    }

    private int getIndex(Spinner spinner, String myString)
    {
        int index = 0;

        for (int i=0;i<spinner.getCount();i++){
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)){
                index = i;
                break;
            }
        }
        return index;
    }
}
