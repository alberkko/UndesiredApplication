package com.example.alber.undesiredapplication.activities;

import android.content.Intent;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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

public class UserProfileActivity extends AppCompatActivity implements RecyclerViewAdapter.OnItemClickListener {

    private RecyclerView mRecyclerView;
    private RecyclerViewAdapter mAdapter;
    private DatabaseReference mDatabaseRef;
    private List<Buildings> mUploads;
    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
    private FirebaseStorage mStorage;
    private ValueEventListener mDBListener;
    private DatabaseReference mDatabaseCurrentUser;
    private Query mQueryCurrentUser;
    private TextView mUsername;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        getSupportActionBar().hide();

        mRecyclerView = findViewById(R.id.recycler_view_profile);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mUploads = new ArrayList<>();
        mStorage = FirebaseStorage.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Buildings");
        mAdapter = new RecyclerViewAdapter(UserProfileActivity.this, mUploads);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(UserProfileActivity.this);
        mUsername = findViewById(R.id.username_aa);

        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();


        String CurrentUserId = mAuth.getCurrentUser().getUid();
        mDatabaseCurrentUser = FirebaseDatabase.getInstance().getReference().child("Buildings");

        mQueryCurrentUser = mDatabaseCurrentUser.orderByChild("userID").equalTo(CurrentUserId);

        mDBListener = mQueryCurrentUser.addValueEventListener(new ValueEventListener() {
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
                Toast.makeText(UserProfileActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });


        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        DatabaseReference name = ref.child("Users").child(mCurrentUser.getUid()).child("name");
        name.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String username = dataSnapshot.getValue(String.class);
                mUsername.setText(username);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




        FloatingActionButton fab = findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                startActivity(new Intent(getApplicationContext(), ChoseonMapActivity.class));
            }
        });




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


        Intent mainIntent = new Intent(UserProfileActivity.this, ProfileActivity2.class);
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
                Toast.makeText(UserProfileActivity.this, "Item deleted", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDatabaseRef.removeEventListener(mDBListener);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent startIntent = new Intent(UserProfileActivity.this, MainActivity.class);
        startActivity(startIntent);
        finish();
    }

    private void openChoseMapActivity() {
        Intent intent = new Intent(this, ChoseonMapActivity.class);
        startActivity(intent);

    }

}
