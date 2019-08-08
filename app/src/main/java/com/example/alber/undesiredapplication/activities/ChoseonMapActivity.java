package com.example.alber.undesiredapplication.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

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
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ChoseonMapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ChildEventListener mChildEventListener;
    private DatabaseReference mUsers;
    Marker marker;

    public double latpos;
    public double longpos;
    private Button save_btn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choseon_map);

        save_btn = findViewById(R.id.save_btn);

        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openUploadActivity();
                Toast.makeText(getApplicationContext(), latpos +" + "+longpos,
                        Toast.LENGTH_LONG).show();
            }
        });

        getSupportActionBar().hide(); //<< this


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_btn4);
        mapFragment.getMapAsync(this);
        ChildEventListener mChildEventListener;
        mUsers = FirebaseDatabase.getInstance().getReference("Buildings");
        mUsers.push().setValue(marker);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        //  mMap.setMinZoomPreference(10.0f);

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(41.3275, 19.81870), 13.0f));


        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {

            @Override
            public void onMapLongClick(LatLng point) {

                mMap.clear();

                MarkerOptions marker = new MarkerOptions().position(
                        new LatLng(point.latitude, point.longitude)).title("New Marker");
                mMap.addMarker(marker);

                latpos = point.latitude;
                longpos = point.longitude;
            }
        });
    }

    private void openUploadActivity() {
        Intent intent = new Intent(this, UploadActivity.class);

       Bundle b = new Bundle();
       b.putDouble("latpos", latpos);
       b.putDouble("longpos", longpos);
       intent.putExtras(b);
       startActivity(intent);

       finish();

    }


}
