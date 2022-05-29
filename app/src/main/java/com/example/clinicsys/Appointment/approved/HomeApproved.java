package com.example.clinicsys.Appointment.approved;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.clinicsys.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class HomeApproved extends AppCompatActivity{


    private Toolbar mToolbar;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager manager;
    private RecyclerView.Adapter mAdapter;
    private List<AppointmentApproved> appointments;
    private ProgressBar progressBar;
    public static boolean admin= false;
    RequestQueue requestQueue;

    Spinner ageEt, spinnerComplaints,appointmentCat;
    private static  final String BASE_URL = "http://192.168.254.109/android/getProducts.php";
//    private static  final  String BASE_URL = "http://192.168.254.107/android/getProducts.php";
//        private static  final  String BASE_URL = "http://172.31.250.24/android/getProducts.php";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_approved);
        requestQueue = Volley.newRequestQueue(this);
        mToolbar = findViewById(R.id.dashboard_toolbar);
        progressBar = findViewById(R.id.progressbar);
        setSupportActionBar(mToolbar);

        recyclerView = findViewById(R.id.products_recyclerView);
        Button openDialog;
        TextView infoTv;
        manager = new GridLayoutManager(HomeApproved.this, 1);
        recyclerView.setLayoutManager(manager);
        appointments = new ArrayList<>();

        ageEt = findViewById(R.id.spnAppointmentCat);

        spinnerComplaints = (Spinner)findViewById(R.id.spnComplaints);



        SharedPreferences sh = getSharedPreferences("MySharedPref", MODE_APPEND);
        String patientType = sh.getString("PatientType", "");

        Toast.makeText(getApplicationContext(), "Type is "+patientType, Toast.LENGTH_SHORT).show();

        if (patientType.matches("Admin")) {
            Toast.makeText(getApplicationContext(), "Type is admin", Toast.LENGTH_SHORT).show();
            admin = true;
        }
        getAptApproved();
    }

    private void getAptApproved (){
        progressBar.setVisibility(View.VISIBLE);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, BASE_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        progressBar.setVisibility(View.GONE);

                        try {

                            JSONArray array = new JSONArray(response);
                            for (int i = 0; i<array.length(); i++){

                                JSONObject object = array.getJSONObject(i);

                                String title = object.getString("title");
                                String price = object.getString("price");
                                double rating = object.getDouble("rating");
//                                String image = object.getString("image");

//                                String rate = String.valueOf(rating);
//                                float newRate = Float.valueOf(rate);

                                AppointmentApproved product = new AppointmentApproved(title,price);
                                appointments.add(product);
                            }

                        }catch (Exception e){

                        }

                        mAdapter = new RecyclerAdapterApproved(HomeApproved.this,appointments);
                        recyclerView.setAdapter(mAdapter);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(HomeApproved.this, "Database connection failed " + error.toString(),Toast.LENGTH_LONG).show();
            }
        });

        Volley.newRequestQueue(HomeApproved.this).add(stringRequest);

    }

}
