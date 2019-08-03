
package com.example.alber.undesiredapplication.activities;

import android.graphics.Typeface;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.alber.undesiredapplication.R;

import org.w3c.dom.Text;


public class ProfileActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        //hide the default action bar
        getSupportActionBar().hide();
      //  getSupportActionBar().setElevation(0);

        String name  = getIntent().getExtras().getString("building_name");
        String description = getIntent().getExtras().getString("building_description");
        String categorie = getIntent().getExtras().getString("building_categorie");
        String address = getIntent().getExtras().getString("building_address") ;
        String image_url = getIntent().getExtras().getString("building_img") ;


        // ini views



        net.opacapp.multilinecollapsingtoolbar.CollapsingToolbarLayout collapsingToolbarLayout = findViewById(R.id.collapsingtoolbar_id);
        collapsingToolbarLayout.setTitleEnabled(true);

        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppBar);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppBar);

        Typeface typeface = ResourcesCompat.getFont(this, R.font.open_sans_bold);
        collapsingToolbarLayout.setExpandedTitleTypeface(typeface);

        CardView cardView = (CardView) findViewById(R.id.cardview);
        cardView.setBackgroundResource(R.drawable.card_view_bg);

        //  TextView ud_description = findViewById(R.id.aa_description);
        TextView ud_address = findViewById(R.id.aa_address);
        ImageView img = findViewById(R.id.aa_thumbnail);
        TextView ud_categorie = findViewById(R.id.aa_categorie);

        collapsingToolbarLayout.setTitle(name);

        RequestOptions requestOptions = new RequestOptions().centerCrop().placeholder(R.drawable.loading_shape).error(R.drawable.loading_shape);


        //  set image using Glide
        Glide.with(this).load(image_url).apply(requestOptions).into(img);

        //  ud_description.setText(description);
        ud_address.setText(address);
        ud_categorie.setText(categorie);
    }
}
