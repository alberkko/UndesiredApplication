package com.example.alber.undesiredapplication.activities;

import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.example.alber.undesiredapplication.R;
import com.example.alber.undesiredapplication.model.Buildings;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ProfileActivity2 extends AppCompatActivity implements OnMapReadyCallback {


    private String b_name;
    private String b_address;
    private String b_image;
    private String b_userId;
    private ChildEventListener mChildEventListener;
    private String b_desc;
    private String b_categ;

    private double longt;
    private double lat;

    private DatabaseReference mUsers;
    private GoogleMap mMap;
    Marker marker;

    private TextView Building_desc;
    private TextView Building_categ;
    private TextView mUid;
    private TextView mlongt;
    private TextView mlat;
    private TextView Building_name;
    private TextView Building_address;
    private ImageView thumbnail;
    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().hide(); //<< this

        setContentView(R.layout.activity_profile2);

        mUid = findViewById(R.id.text_uid);
        mlongt = findViewById(R.id.long_edit);
        mlat = findViewById(R.id.lat_edit);
        Building_address = findViewById(R.id.aa_address);
        Building_name = findViewById(R.id.aa_building_name);
        thumbnail = findViewById(R.id.aa_thumbnail);
        Building_categ = findViewById(R.id.aa_categorie);
        Building_desc = findViewById(R.id.aa_description);


        //BUNDLE ESHTE NJE MENYRE QE TE MARRESH INTENTIN NGA ANDEJ KETEJ,
        // SE AJO MENYRA TJETER NUK PO PUNONTE SE NUK DUA TI KTHEJ NE STRING
        // EDHE SHOULD PROBABLY
        //BE USED PER TE GJITHA PO QE UNE PO I LE CA KESHTU CA ASHTU SE PERTOJ TANI
        //BUT I'LL GET BACK TO THIS!!!!
        Bundle b = getIntent().getExtras();
        longt = b.getDouble("longt");
        lat = b.getDouble("lat");

        b_userId = getIntent().getExtras().get("b_userId").toString();
        b_name = getIntent().getExtras().get("b_name").toString();
        b_address = getIntent().getExtras().get("b_address").toString();
        b_desc = getIntent().getExtras().get("b_desc").toString();
        b_categ = getIntent().getExtras().get("b_categ").toString();

        Building_name.setText(b_name);
        Building_address.setText(b_address);
        Building_desc.setText(b_desc);
        Building_categ.setText(b_categ);

        //KTHEJ NE STRING QE TI DISPLAY ANESH HARTES
        String Stringtodoublelat = Double.toString(lat);
        String Stringtodoublelongt = Double.toString(longt);

        //limit the number of digits to 8 so it looks nice
        String cutStringLat = Stringtodoublelat.substring(0,7);
        String cutStringLong = Stringtodoublelongt.substring(0,7);

        mlongt.setText(cutStringLong);
        mlat.setText(cutStringLat);
        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();


            DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
            DatabaseReference name = ref.child("Users").child(b_userId).child("name");
            name.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String username = dataSnapshot.getValue(String.class);
                    mUid.setText(username);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });





       Intent intent = getIntent();
        b_image= String.valueOf(intent.getStringExtra("b_image_url"));

        Picasso.get()
                .load(b_image).into(thumbnail);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_btn2);
        mapFragment.getMapAsync(this);
        ChildEventListener mChildEventListener;
        mUsers= FirebaseDatabase.getInstance().getReference("Buildings");
        mUsers.push().setValue(marker);


    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        //  mMap.setMinZoomPreference(10.0f);

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat,longt), 13.0f    ));

        mUsers.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot s : dataSnapshot.getChildren()){
                    Buildings building = s.getValue(Buildings.class);
                    LatLng location=new LatLng(lat,longt);
                    mMap.addMarker(new MarkerOptions().position(location).title(b_name)).
                            setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }


    @Override
    public void onBackPressed(){

        Intent startIntent = new Intent(ProfileActivity2.this, MainActivity.class);
        startActivity(startIntent);
        finish();

    }

}


