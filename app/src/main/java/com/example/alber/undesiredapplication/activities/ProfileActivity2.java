package com.example.alber.undesiredapplication.activities;

import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.alber.undesiredapplication.R;
import com.squareup.picasso.Picasso;

public class ProfileActivity2 extends AppCompatActivity {


    private String b_name;
    private String b_address;
    private String b_image;

    private TextView Building_name;
    private TextView Building_address;
    private ImageView thumbnail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().hide(); //<< this

        setContentView(R.layout.activity_profile2);


        Building_address = findViewById(R.id.aa_address);
        Building_name = findViewById(R.id.aa_building_name);
        thumbnail = findViewById(R.id.aa_thumbnail);


        b_name = getIntent().getExtras().get("b_name").toString();
        b_address = getIntent().getExtras().get("b_address").toString();

        Building_name.setText(b_name);
        Building_address.setText(b_address);


       Intent intent = getIntent();
        b_image= String.valueOf(intent.getStringExtra("b_image_url"));


        Picasso.get()
                .load(b_image)
                .into(thumbnail);
    }
}
