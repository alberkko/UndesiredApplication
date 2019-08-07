package com.example.alber.undesiredapplication.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.alber.undesiredapplication.R;
import com.example.alber.undesiredapplication.adapters.RecyclerViewAdapter;
import com.example.alber.undesiredapplication.model.Buildings;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements RecyclerViewAdapter.OnItemClickListener {

    private RecyclerView mRecyclerView;
    private RecyclerViewAdapter mAdapter;
    private DatabaseReference mDatabaseRef;
    private List<Buildings> mUploads;
    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private FirebaseStorage mStorage;
    private ValueEventListener mDBListener;
    private TextView mlogout;
    private ImageView mthreedots;
    private EditText msearch;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemViewCacheSize(40);
        mRecyclerView.setDrawingCacheEnabled(true);
        mRecyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mUploads = new ArrayList<>();
        mStorage = FirebaseStorage.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Buildings");
        mAdapter = new RecyclerViewAdapter(MainActivity.this, mUploads);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(MainActivity.this);
        mlogout = findViewById(R.id.logout);
        mthreedots = findViewById(R.id.threedots);
        msearch = findViewById(R.id.search);

      //  mRecyclerView.setNestedScrollingEnabled(false);

        getSupportActionBar().hide();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);



        msearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if(!s.toString().isEmpty())
                {
                    search(s.toString());
                }
                else{
                    search("");
                }

            }
        });



        //GET NAME TO PUT ON THE SLIDE MENU

        if (mCurrentUser != null) {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
            DatabaseReference name = ref.child("Users").child(mCurrentUser.getUid()).child("name");
            name.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String username = dataSnapshot.getValue(String.class);

                    NavigationView navigationView = (NavigationView) findViewById(R.id.navmenuffs);
                    Menu menu = navigationView.getMenu();
                    MenuItem nav_profile = menu.findItem(R.id.nav_profile);
                    nav_profile.setTitle(username);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


        } else {
            NavigationView navigationView = (NavigationView) findViewById(R.id.navmenuffs);
            Menu menu = navigationView.getMenu();
            MenuItem nav_profile = menu.findItem(R.id.nav_profile);
            nav_profile.setTitle("GUEST");
        }

        //GET DATA FROM FIREBASE!!

        mDBListener = mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                mUploads.clear();

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Buildings upload = postSnapshot.getValue(Buildings.class);
                    upload.setKey(postSnapshot.getKey());
                    mUploads.add(upload);

                }

                mAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });


        //DISPLAY ON LOGIN ONLY

        if (mCurrentUser != null) {         //LOGGED IN
            mlogout.setText("Logout");
        } else {
            mlogout.setText("Login");       //LOGGED OUT
        }

        mlogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                sendToStart();
            }
        });



        mthreedots.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DrawerLayout navDrawer = findViewById(R.id.drawer_layout);
                // If the navigation drawer is not open then open it, if its already open then close it.
                if(!navDrawer.isDrawerOpen(Gravity.END)) navDrawer.openDrawer(Gravity.END);
                else navDrawer.closeDrawer(Gravity.START);
            }
        });


    }  //end of on create

    private void search(String s) {

        Query query = mDatabaseRef.orderByChild("name")
                .startAt(s).endAt(s + "\uf8ff");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.hasChildren()){

                    mUploads.clear();
                    for(DataSnapshot dss: dataSnapshot.getChildren()){
                        final Buildings buildings = dss.getValue(Buildings.class);
                        mUploads.add(buildings);
                    }

                    mAdapter.notifyDataSetChanged();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void profile(MenuItem item) {

        if (mCurrentUser != null) {
            Intent startIntent = new Intent(MainActivity.this, UserProfileActivity.class);
            startActivity(startIntent);
            finish();
        } else {
            Intent startIntent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(startIntent);
            finish();
        }
    }

    public void add(MenuItem item) {

        if (mCurrentUser != null) {
            openChoseMapActivity();
        } else
            Toast.makeText(MainActivity.this, "You have to be Signed In to upload", Toast.LENGTH_SHORT).show();
    }

    public void map(MenuItem item) {
        openMapActivity();
    }


    @Override
    public void onItemClick(int position) {

        Buildings selectedItem = mUploads.get(position);
        final String name = selectedItem.getName();
        final String address = selectedItem.getAddress();
        final String image_url = selectedItem.getImage_url();
        final double longt = selectedItem.getLongitude();
        final double lat = selectedItem.getLatitude();
        final String uid = selectedItem.getUserID();
        final String desc = selectedItem.getDescription();
        final String categ = selectedItem.getCategorie();

        Intent mainIntent = new Intent(MainActivity.this, ProfileActivity2.class);
        mainIntent.putExtra("b_name", name);
        mainIntent.putExtra("b_address", address);
        mainIntent.putExtra("b_image_url", image_url);
        mainIntent.putExtra("b_userId", uid);
        mainIntent.putExtra("b_desc", desc);
        mainIntent.putExtra("b_categ", categ);

        Bundle b = new Bundle();
        b.putDouble("longt", longt);
        b.putDouble("lat", lat);

        mainIntent.putExtras(b);
        startActivity(mainIntent);
        finish();
    }


    @Override
    public void onDeleteClick(int position) {
        Buildings selectedItem = mUploads.get(position);
        final String selectedKey = selectedItem.getKey();

        StorageReference imageRef = mStorage.getReferenceFromUrl(selectedItem.getImage_url());
        imageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                mDatabaseRef.child(selectedKey).removeValue();
                Toast.makeText(MainActivity.this, "Item deleted", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDatabaseRef.removeEventListener(mDBListener);
    }


    private void openMapActivity() {
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }


    private void openChoseMapActivity() {
        Intent intent = new Intent(this, ChoseonMapActivity.class);
        startActivity(intent);

    }

    private void sendToStart() {
        Intent startIntent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(startIntent);
        finish();
    }

}