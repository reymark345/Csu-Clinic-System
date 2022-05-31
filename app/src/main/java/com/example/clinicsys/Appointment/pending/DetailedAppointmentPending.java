package com.example.clinicsys.Appointment.pending;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.clinicsys.R;

public class DetailedAppointmentPending extends AppCompatActivity {

    private Toolbar mToolbar;
    private ActionBar mActionBar;
//    private ImageView mImage;
    private TextView mDate, mRating, mTime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.appointment_activity);


//        mToolbar = findViewById(R.id.toolbar);
//        mImage = findViewById(R.id.image_view);
        mDate = findViewById(R.id.name);
        mTime = findViewById(R.id.price);
//        mRating = findViewById(R.id.rating);


        // Setting up action bar
//        setSupportActionBar(mToolbar);
//        mActionBar = getSupportActionBar();
//        mActionBar.setDisplayHomeAsUpEnabled(true);
//        mActionBar.setHomeAsUpIndicator(getResources().getDrawable(R.drawable.ic_chevron_left_black_24dp));

        // Catching incoming intent
        Intent intent = getIntent();
//        double price = intent.getDoubleExtra("price",0);

//        float rate = intent.getFloatExtra("rate",0);
        int id = intent.getIntExtra("id",0);
        String categoryName = intent.getStringExtra("title");
        String SubCategory = intent.getStringExtra("price");
        String image = intent.getStringExtra("image");

        if (intent !=null){

//            mActionBar.setTitle(title);
            mDate.setText(categoryName);
//            mRating.setText("Rating :"+rate+" /2");
            mTime.setText(SubCategory);
//            Glide.with(DetailedProductsActivity.this).load(image).into(mImage);
        }

    }
}
