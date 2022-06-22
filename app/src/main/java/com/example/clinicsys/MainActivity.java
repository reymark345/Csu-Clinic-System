package com.example.clinicsys;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.clinicsys.Appointment.approved.HomeApproved;
import com.example.clinicsys.Appointment.pending.HomePending;

import com.example.clinicsys.Appointment.records.HomeRecords;
import com.example.clinicsys.Profile.EditProfile;
import com.example.clinicsys.Splash.Activity_Splash_Login;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONObject;


public class MainActivity extends AppCompatActivity {
    CardView AppointmentDashPending,AppointmentDashApproved,AppointmentDashRecords, Logout;
    TextView txtPending, txtApproved, txtRecords, txtCancelled, txtCompleted;
    RequestQueue requestQueue;
    DrawerLayout drawerLayout;
    String Urltype,useridd;
    ImageView profileImg;
    public static String BASE_URL = "";

    public static boolean admin= false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard_main);
        requestQueue = Volley.newRequestQueue(this);
        AppointmentDashPending = (CardView) findViewById(R.id.AppointmentDash_Pending);
        AppointmentDashApproved = (CardView) findViewById(R.id.AppointmentDash_Approved);
        AppointmentDashRecords = (CardView) findViewById(R.id.AppointmentDashRecords);
        txtPending = (TextView) findViewById(R.id.txtPending);
        txtApproved = (TextView) findViewById(R.id.txtApproved);
        txtCancelled = (TextView) findViewById(R.id.txtCancelled);
        txtRecords = (TextView) findViewById(R.id.txtRecords);
        txtCompleted = (TextView) findViewById(R.id.txtCompleted);
        Logout = (CardView) findViewById(R.id.logout);
        drawerLayout = findViewById(R.id.drawerlayout);
        profileImg = findViewById(R.id.ImgProfileDashboard);

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigationview);
        navigationView.bringToFront();
        View headerView = navigationView.getHeaderView(0);
        TextView navfullName = (TextView) headerView.findViewById(R.id.fullName);
        TextView navUsername = (TextView) headerView.findViewById(R.id.idNumber);
        ImageView profileImage = (ImageView) headerView.findViewById(R.id.profile_images);
        SharedPreferences sh = getSharedPreferences("MySharedPref", MODE_APPEND);
        String fName = sh.getString("firstName", "");
        String lName = sh.getString("lastName", "");
        String idNo = sh.getString("idNo", "");
        String roleName = sh.getString("roleName", "");
        String imageUrl = sh.getString("imageUrl", "");
        String token = sh.getString("token", "");
        BASE_URL = sh.getString("urlBased", "");

        String fullName = fName + " "+ lName;
        navfullName.setText(fullName);
        navUsername.setText(idNo);

        countAppointment(txtPending,txtApproved,txtCancelled,txtRecords);

        if (roleName.matches("staff") || roleName.matches("admin") ) {
            admin = true;
        }
        else {
            admin = false;
        }

        if (!imageUrl.matches("null")){
            Glide.with(this)
                    .load(BASE_URL+"/csu_clinic_app/storage/profile_img/"+imageUrl)
                    .into(profileImage);
            Glide.with(this)
                    .load(BASE_URL+"/csu_clinic_app/storage/profile_img/"+imageUrl)
                    .into(profileImg);
        }

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                if (item.getItemId() == R.id.editProfile)
                {
                    Intent intent = new Intent(getApplicationContext(), EditProfile.class);
                    startActivity(intent);
                    finish();
                }
                else if (item.getItemId() == R.id.logout_menu)
                {
                    Intent intent = new Intent(getApplicationContext(), Activity_Splash_Login.class);
                    startActivity(intent);
                    finish();
                }

                return false;
            }
        });

        AppointmentDashPending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, HomePending.class);
                startActivity(intent);
                finish();
            }
        });
        AppointmentDashApproved.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, HomeApproved.class);
                startActivity(intent);
                finish();
            }
        });
        AppointmentDashRecords.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, HomeRecords.class);
                startActivity(intent);
                finish();
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

    public void countAppointment (TextView txtPending, TextView txtApproved, TextView txtCancelled,TextView txtRecords){
        SharedPreferences sh = getSharedPreferences("MySharedPref", MODE_APPEND);
        useridd = sh.getString("userId", "");
        String roleName = sh.getString("roleName", "");
        if (roleName.matches("end-user")){Urltype = "/csu_clinic_app/api/dashboard/status/"+useridd;}
        else {Urltype = "/csu_clinic_app/api/dashboard/status/";}

        StringRequest stringRequest = new StringRequest(Request.Method.GET, BASE_URL+Urltype,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            JSONObject jsonObject1 = jsonArray.getJSONObject(0);
                            String pending = jsonObject1.optString("pending");
                            String approved = jsonObject1.optString("approved");
                            String cancelled = jsonObject1.optString("cancelled");
                            String records = jsonObject1.optString("records");
                            String completed = jsonObject1.optString("completed");

                            if (!pending.matches("null") ){
                                txtPending.setText(pending);
                                txtApproved.setText(approved);
                                txtCancelled.setText(cancelled);
                                txtCompleted.setText(completed);
                                txtRecords.setText(records);
                            }
                        }
                        catch (Exception e){
                            Toast.makeText(getApplicationContext(), "catch error " + e, Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Database connection failed " + error.toString(),Toast.LENGTH_LONG).show();
            }
        });
        Volley.newRequestQueue(MainActivity.this).add(stringRequest);
    }
}