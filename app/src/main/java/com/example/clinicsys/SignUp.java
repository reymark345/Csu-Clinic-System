package com.example.clinicsys;


import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.vishnusivadas.advanced_httpurlconnection.PutData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SignUp extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    EditText edtIDno,edtPatient,edtFirstname,edtMiddleName,edtLastname,edtSex,edtBirthdate,edtAddress,edtCpno,edtEmail,edtUsername,edtPassword;
    Button buttonSignup;
    TextView txtViewLogin;

    Spinner spinnerAppointment,spinnerComplaints;
    ArrayList<String> appointmentList = new ArrayList<>();
    ArrayList<String> complaintList = new ArrayList<>();
    ArrayAdapter<String> countryAdapter;
    ArrayAdapter<String> complaintAdapter;
    RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        requestQueue = Volley.newRequestQueue(this);

        spinnerAppointment = (Spinner)findViewById(R.id.spinnerAppointmentCat);
        spinnerComplaints = (Spinner)findViewById(R.id.spinnerComplaints);
        edtIDno = findViewById(R.id.edtIdno);
//        edtPatient = findViewById(R.id.edtPatientType);
        edtFirstname = findViewById(R.id.edtFirstname);
        edtMiddleName = findViewById(R.id.edtMiddleName);
        edtLastname = findViewById(R.id.edtLastname);
        edtSex = findViewById(R.id.edtSex);
        edtBirthdate = findViewById(R.id.edtBirthdate);
        edtCpno = findViewById(R.id.edtContact);
        edtEmail = findViewById(R.id.edtEmail);
        edtAddress = findViewById(R.id.edtAddress);

        edtUsername = findViewById(R.id.edtFirstname);
        edtPassword = findViewById(R.id.edtLastname);
        buttonSignup = findViewById(R.id.confirmRequestBtn);
        txtViewLogin = findViewById(R.id.loginText);

        patientDropdown();

        buttonSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String id_no, patientType, fname,middle, lname, user_role, username, password, sex, email, contact_no, address;
                id_no = String.valueOf(edtIDno.getText());
                middle = String.valueOf(edtMiddleName.getText());
                fname = String.valueOf(edtFirstname.getText());
                lname = String.valueOf(edtLastname.getText());

                user_role = String.valueOf(edtIDno.getText());
                username = String.valueOf(edtUsername.getText());
                password = String.valueOf(edtPassword.getText());

                sex = String.valueOf(edtSex.getText());
                email = String.valueOf(edtEmail.getText());
                contact_no = String.valueOf(edtCpno.getText());
                address = String.valueOf(edtAddress.getText());

                String appointment = spinnerAppointment.getSelectedItem().toString();
                String complain = spinnerComplaints.getSelectedItem().toString();


//                if(!id_no.equals("") && !patientType.equals("") && !fname.equals("") && !lname.equals("")){
                if(!user_role.equals("") && !username.equals("") && !password.equals("")){
                    //Start ProgressBar first (Set visibility VISIBLE)
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            //Starting Write and Read data with URL
                            //Creating array for parameters
                            String[] field = new String[8];
                            EditText edtIDno,edtPatient,edtFirstname,edtMiddleName,edtLastname,edtSex,edtBirthdate,edtAge,edtCpno,edtEmail,edtUsername,edtPassword;

                            field[0] = "first_name";
                            field[1] = "middle_name";
                            field[2] = "last_name";
                            field[3] = "sex";
                            field[4] = "id_no";
                            field[5] = "email";
                            field[6] = "contact_no";
                            field[7] = "address";


                            //Creating array for data
                            String[] data = new String[8];

                            data[0] = fname;
                            data[1] = middle;
                            data[2] = lname;
                            data[3] = sex;
                            data[4] = id_no;
                            data[5] = email;
                            data[6] = contact_no;
                            data[7] = address;

//                            PutData putData = new PutData("http://172.31.250.45/clinic_system/signup.php", "POST", field, data);
                            PutData putData = new PutData("http://192.168.254.103/csu_clinic/signup.php", "POST", field, data);
                            if (putData.startPut()) {
                                if (putData.onComplete()) {
                                    String result = putData.getResult();
                                    if(result.equals("Sign Up Success")){
                                        Toast.makeText(getApplicationContext(), "Successfully save", Toast.LENGTH_SHORT).show();
//                                        Intent intent = new Intent(getApplicationContext(),login.class);
//                                        startActivity(intent);
//                                        finish();
                                    }
                                    else {
                                        Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
                                    }
                                    //End ProgressBar (Set visibility to GONE)
                                    Log.i("PutData", result);
                                }
                            }
                            //End Write and Read data with URL
                        }
                    });
                }
                else {
                    Toast.makeText(getApplicationContext(), "All fields required ", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if(adapterView.getId() == R.id.spinnerAppointmentCat){
            complaintList.clear();
            String selectedCountry = adapterView.getSelectedItem().toString();
            String url = "http://10.0.2.2/android/populate_city.php?country_name="+selectedCountry;
            requestQueue = Volley.newRequestQueue(this);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                    url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        JSONArray jsonArray = response.getJSONArray("cities");
                        for(int i=0; i<jsonArray.length();i++){
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            String cityName = jsonObject.optString("city_name");
                            complaintList.add(cityName);
                            complaintAdapter = new ArrayAdapter<>(SignUp.this,
                                    android.R.layout.simple_spinner_item, complaintList);
                            complaintAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinnerComplaints.setAdapter(complaintAdapter);
                        }
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
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
    public void patientDropdown(){
        appointmentList.clear();
        String url = "http://192.168.254.103/csu_clinic/populate_country.php";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray jsonArray = response.getJSONArray("countries");
                    for(int i=0; i<jsonArray.length();i++){
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String countryName = jsonObject.optString("country_name");
                        appointmentList.add(countryName);
                        countryAdapter = new ArrayAdapter<>(SignUp.this,
                                android.R.layout.simple_spinner_item, appointmentList);
                        countryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerAppointment.setAdapter(countryAdapter);


                    }
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
        spinnerAppointment.setOnItemSelectedListener(this);
    }
}