package com.example.clinicsys;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.clinicsys.Appointment.HomeActivity;
import com.example.clinicsys.Splash.Activity_Splash_Login;

public class MainActivity extends AppCompatActivity {
    CardView AppointmentDash, Logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AppointmentDash = (CardView) findViewById(R.id.AppointmentDash);

        Logout = (CardView) findViewById(R.id.logout);


        AppointmentDash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                startActivity(intent);
//                finish();
            }
        });

        Logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), Activity_Splash_Login.class);
                startActivity(intent);
                finish();

            }
        });
    }

}