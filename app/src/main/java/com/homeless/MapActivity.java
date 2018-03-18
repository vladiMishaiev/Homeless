package com.homeless;

import android.app.Dialog;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.homeless.Logic.CustomInfoWindowAdapter;
import com.homeless.Logic.PlaceAutocompleteAdapter;
import com.homeless.Logic.Review;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener{
    private static final String TAG = "MapActivity";
    private static final int ERROR_DIALOG_REQUEST = 9001;
    private static final float DEFAULT_ZOOM = 15f;
    private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(
            new LatLng(-40, -168), new LatLng(71, 136));
    private Button newReviewBtn,toDashboardBtn;
    private GoogleMap gMap;
    private List<Marker> markers;
    private AutoCompleteTextView mSearchText;
    private PlaceAutocompleteAdapter mPlaceAutocompleteAdapter;
    private GoogleApiClient mGoogleApiClient;
    private Address address;
    private DatabaseReference mDatabase;
    private Marker lastOpenned = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        initUI();
        setButtons();

        if(isServicesOK()){
            initMap();
            init();
            setReviewsMarkers();
        }
    }

    private void setReviewsMarkers(){
        mDatabase.child("Reviews").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Review itr = postSnapshot.getValue(Review.class);
                    setMarker(itr);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "onCancelled: failed loading reviews from DB");
            }
        });
    }

    private void setMarker(Review review){
       /* Marker options = new Marker()
                .position(new LatLng(review.getLat(), review.getLon()))
                .title(review.getAddress());*/
        //gMap.addMarker(marker);
        gMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(MapActivity.this));
        Marker marker = gMap.addMarker(new MarkerOptions().
                position(new LatLng(review.getLat(), review.getLon())).
                title(review.getAddress()).
                snippet("Rooms: "+review.getNumOfRooms()
                        +", Floor: "+review.getFloor()
                        +", Size: " +review.getSize()+"\n"
                        +"Score: " + review.getScore()+ "\n"
                        +"Price: " + review.getPrice()+"\n"
                        +"Click for more details"));
        marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_action_home));
        marker.setTag(review);
    }

    private void initUI(){
        mSearchText = (AutoCompleteTextView) findViewById(R.id.input_search);
        newReviewBtn = (Button) findViewById(R.id.newReviewMapBtn);
        toDashboardBtn = (Button)findViewById(R.id.deshboardBtnMap);
    }

    private void setButtons (){
        newReviewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MapActivity.this, ReviewActivity.class);
                if (address != null) {
                    intent.putExtra("Lat", address.getLatitude());
                    intent.putExtra("Lon", address.getLongitude());
                    intent.putExtra("City", address.getLocality());
                    intent.putExtra("Street", address.getThoroughfare());
                    intent.putExtra("Address",mSearchText.getText().toString());
                }
                startActivity(intent);
            }
        });

        toDashboardBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MapActivity.this, UserDashboard.class);
                startActivity(intent);
            }
        });
    }

    private void init(){
        Log.d(TAG, "init: initializing");

        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();

        mPlaceAutocompleteAdapter = new PlaceAutocompleteAdapter(this, mGoogleApiClient,
                LAT_LNG_BOUNDS, null);

        mSearchText.setAdapter(mPlaceAutocompleteAdapter);

        mSearchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if(actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || keyEvent.getAction() == KeyEvent.ACTION_DOWN
                        || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER){

                    //execute our method for searching
                    geoLocate();
                }

                return false;
            }
        });
        /*mGps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: clicked gps icon");
                getDeviceLocation();
            }
        });*/
    }

    private void geoLocate(){
        Log.d(TAG, "geoLocate: geolocating");

        String searchString = mSearchText.getText().toString();

        Geocoder geocoder = new Geocoder(MapActivity.this);
        List<Address> list = new ArrayList<>();
        try{
            list = geocoder.getFromLocationName(searchString, 1);
        }catch (IOException e){
            Log.e(TAG, "geoLocate: IOException: " + e.getMessage() );
        }

        if(list.size() > 0){
            address = list.get(0);

            Log.d(TAG, "geoLocate: found a location: " + address.toString());
            //Toast.makeText(this, address.toString(), Toast.LENGTH_SHORT).show();
            moveCamera(new LatLng(address.getLatitude(), address.getLongitude()), DEFAULT_ZOOM,
                    address.getAddressLine(0));
        }
    }

    private void moveCamera(LatLng latLng, float zoom, String title){
        Log.d(TAG, "moveCamera: moving the camera to: lat: " + latLng.latitude + ", lng: " + latLng.longitude );
        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

        if(!title.equals("My Location")){
            MarkerOptions options = new MarkerOptions()
                    .position(latLng)
                    .title(title);
            gMap.addMarker(options);
        }
        //hideSoftKeyboard();
    }


    private void initMap(){
        android.app.FragmentManager fm = getFragmentManager();
        MapFragment mapFrag = (MapFragment) fm.findFragmentById(R.id.map);
        mapFrag.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                gMap = googleMap;
                //initMarkers();
                gMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 18f));
                        Review myRestoredData = (Review) marker.getTag();
                        return false;
                    }
                });

                gMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener(){
                    @Override
                    public void onInfoWindowClick(Marker marker) {
                        Review myRestoredData = (Review) marker.getTag();
                        Log.d(TAG, " * * * * * onInfoWindowClick: " + myRestoredData.getAddress());
                        Intent intent = new Intent(MapActivity.this, ReviewActivity.class);
                        intent.putExtra("ReviewID", myRestoredData.getId());
                        startActivity(intent);
                    }
                });
            }
        });
    }

    private boolean isServicesOK(){
        Log.d(TAG, "isServicesOK: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MapActivity.this);

        if(available == ConnectionResult.SUCCESS){
            //everything is fine and the user can make map requests
            Log.d(TAG, "isServicesOK: Google Play Services is working");
            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            //an error occured but we can resolve it
            Log.d(TAG, "isServicesOK: an error occured but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MapActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        }else{
            Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    /*private void initMarkers() {
        markers = new ArrayList<>();
        for(Record r : records) {
            markers.add(gMap.addMarker(new MarkerOptions().position(new LatLng(r.getLat(), r.getLang())).visible(true).title("Name: " + r.getName()).snippet("Score: " + r.getScore())));
        }
    }*/

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
