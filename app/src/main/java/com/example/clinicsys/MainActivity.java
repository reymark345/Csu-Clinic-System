package com.example.clinicsys;

import static com.example.clinicsys.Splash.Activity_Splash_Login.BASE_URL;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.clinicsys.Appointment.approved.HomeApproved;
import com.example.clinicsys.Appointment.pending.HomePending;
import com.example.clinicsys.Records.temporary;

import com.example.clinicsys.Splash.Activity_Splash_Login;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    CardView AppointmentDashPending,AppointmentDashApproved,AppointmentDashRecords, Logout;
    TextView txtPending;

    ArrayList<String> patientType = new ArrayList<>();
    ArrayAdapter<String> patientAdapter;
    ArrayAdapter<String> complaintAdapter;
    RequestQueue requestQueue;
    DrawerLayout drawerLayout;

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
        Logout = (CardView) findViewById(R.id.logout);
        drawerLayout = findViewById(R.id.drawerlayout);
        countAppointment(txtPending);
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigationview);
        navigationView.bringToFront();
        View headerView = navigationView.getHeaderView(0);
        TextView navfullName = (TextView) headerView.findViewById(R.id.fullName);
        TextView navUsername = (TextView) headerView.findViewById(R.id.idNumber);
        SharedPreferences sh = getSharedPreferences("MySharedPref", MODE_APPEND);
        String fName = sh.getString("firstName", "");
        String lName = sh.getString("lastName", "");
        String idNo = sh.getString("idNo", "");
        String roleName = sh.getString("roleName", "");
        String fullName = fName + " "+ lName;
        navfullName.setText(fullName);
        navUsername.setText(idNo);
        if (roleName.matches("staff") || roleName.matches("admin") ) {
            admin = true;
        }
        else {
            admin = false;
        }

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {

                if (item.getItemId() == R.id.editProfile)
                {
                    Toast.makeText(getApplicationContext(),"profile",Toast.LENGTH_SHORT).show();
                }
                else if (item.getItemId() == R.id.logout_menu)
                {
                    Toast.makeText(getApplicationContext(),"logout",Toast.LENGTH_SHORT).show();
                }

                return false;
            }
        });

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
        AppointmentDashRecords.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,temporary.class);
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
    public void countAppointment(TextView aptCat){
        patientType.clear();
//        String url =
        String url = BASE_URL+"/csu_clinic/populate_country.php";
//        String url = "http://192.168.254.109/csu_clinic/populate_country.php";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray jsonArray = response.getJSONArray("countries");
                    for(int i=0; i<jsonArray.length();i++){
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String countryName = jsonObject.optString("country_name");
                        patientType.add(countryName);
                        patientAdapter = new ArrayAdapter<>(MainActivity.this,
                                android.R.layout.simple_spinner_item, patientType);
                        patientAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//
                    }
                    aptCat.setText(""+patientType.size());
                    Toast.makeText(getApplicationContext(), "Pilaa? "+patientType.size(), Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        requestQueue.add(jsonObjectRequest);
//        aptCat.setOnItemSelectedListener(this);
    }

}