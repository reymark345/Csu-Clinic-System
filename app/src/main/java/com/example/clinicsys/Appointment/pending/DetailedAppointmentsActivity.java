package com.example.clinicsys.Appointment.pending;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.clinicsys.R;

public class DetailedAppointmentsActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private ActionBar mActionBar;
//    private ImageView mImage;
    private TextView mDate, mRating, mTime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_appointment);


// Retrieving the value using its keys the file name
// must be same in both saving and retrieving the data


// The value will be default as empty string because for
// the very first time when the app is opened, there is nothing to show






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
        String date = intent.getStringExtra("title");
        String time = intent.getStringExtra("price");
        String image = intent.getStringExtra("image");

        if (intent !=null){

//            mActionBar.setTitle(title);
            mDate.setText(date);
//            mRating.setText("Rating :"+rate+" /2");
            mTime.setText(time);
//            Glide.with(DetailedProductsActivity.this).load(image).into(mImage);
        }

    }
}