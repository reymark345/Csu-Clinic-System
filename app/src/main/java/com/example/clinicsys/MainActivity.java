package com.example.clinicsys;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.clinicsys.Appointment.approved.HomeApproved;
import com.example.clinicsys.Appointment.pending.HomePending;

import com.example.clinicsys.Splash.Activity_Splash_Login;

public class MainActivity extends AppCompatActivity {
    CardView AppointmentDashPending,AppointmentDashApproved, Logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard_main);

        AppointmentDashPending = (CardView) findViewById(R.id.AppointmentDash_Pending);
        AppointmentDashApproved = (CardView) findViewById(R.id.AppointmentDash_Approved);
        Logout = (CardView) findViewById(R.id.logout);


        AppointmentDashPending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, HomePending.class);
                startActivity(intent);
//                finish();
            }
        });

        AppointmentDashApproved.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, HomeApproved.class);
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