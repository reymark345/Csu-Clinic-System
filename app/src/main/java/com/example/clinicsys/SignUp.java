package com.example.clinicsys;


import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.clinicsys.Splash.Activity_Splash_Login;
import com.google.android.material.textfield.TextInputLayout;
import com.vishnusivadas.advanced_httpurlconnection.PutData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import cn.pedant.SweetAlert.SweetAlertDialog;
import es.dmoral.toasty.Toasty;

public class SignUp extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    EditText edtIDno,edtFirstname,edtMiddleName,edtLastname,edtBirthdate,edtAddress,edtCpno,edtEmail,edtSchedule;
    Button buttonSignup;
    private int prevCount = 0;
    String DialogStatus;

    Spinner spinnerPatientType,spinnerAppointment,spinnerComplaints, spinnerSex;
    ArrayList<String> appointmentList = new ArrayList<>();
    ArrayList<String> complaintList = new ArrayList<>();
    ArrayList<String> patientType = new ArrayList<>();
    ArrayAdapter<String> countryAdapter;
    ArrayAdapter<String> complaintAdapter;
    ArrayAdapter<String> patientAdapter;

    RequestQueue requestQueue;
    TextInputLayout tilIdno, tilFname, tilMiddle,tilLastname, tilSex, tilAddress, tilBdate, tilComplants, tilCpno, tilEmail, tilPatientType;
    String blankMessage = "Please fill this blank";
    private boolean isAtSpaceDelimiter(int currCount) {
        return currCount == 3;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        requestQueue = Volley.newRequestQueue(this);
        spinnerPatientType = (Spinner)findViewById(R.id.spinnerPatientType);
        spinnerAppointment = (Spinner)findViewById(R.id.spinnerAppointmentCat);
        spinnerComplaints = (Spinner)findViewById(R.id.spinnerComplaints);
        spinnerSex = (Spinner)findViewById(R.id.spinnerSex);
        edtIDno = findViewById(R.id.edtIdno);
//        edtPatient = findViewById(R.id.edtPatientType);
        edtFirstname = findViewById(R.id.edtFirstname);
        edtMiddleName = findViewById(R.id.edtMiddleName);
        edtLastname = findViewById(R.id.edtLastname);

        edtBirthdate = findViewById(R.id.edtBirthdate);
        edtSchedule = findViewById(R.id.edtSchedule);

        edtCpno = findViewById(R.id.edtContact);
        edtEmail = findViewById(R.id.edtEmail);
        edtAddress = findViewById(R.id.edtAddress);
        buttonSignup = findViewById(R.id.confirmRequestBtn);

        //layoutMaterial
        tilIdno = findViewById(R.id.til_Idno);
        tilFname = findViewById(R.id.til_firstname);
        tilMiddle = findViewById(R.id.til_middlename);
        tilLastname = findViewById(R.id.til_lastname);
        tilSex = findViewById(R.id.til_sex);
        tilAddress = findViewById(R.id.til_address);
        tilBdate = findViewById(R.id.til_birthdate);
        tilComplants = findViewById(R.id.til_complaints);
        tilCpno = findViewById(R.id.til_cpno);
        tilEmail = findViewById(R.id.til_email);
//        tilPatientType = findViewById(R.id.til_patientType);

        String[] Gender = new String[]{"Male", "Female"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, Gender);
        spinnerSex.setAdapter(adapter);
//        ArrayAdapter<String> adapt = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, patientType);

//        spinnerPatientType.setAdapter(adapt);

        edtSchedule.setFocusable(false);
        edtSchedule.setClickable(true);
        edtBirthdate.setFocusable(false);
        edtBirthdate.setClickable(true);


        edtSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateTimeDialog(edtSchedule);
            }
        });

        edtBirthdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateDialog(edtBirthdate);
            }
        });
        patientType();
        patientDropdown();
        DataAuthentication();

        buttonSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                String id_no, patientType, fname,middle, lname, appointment, complain, sex, birthdate, email, contact_no, address;
                id_no = String.valueOf(edtIDno.getText());
                middle = String.valueOf(edtMiddleName.getText());
                fname = String.valueOf(edtFirstname.getText());
                lname = String.valueOf(edtLastname.getText());
                sex = spinnerSex.getSelectedItem().toString();

                email = String.valueOf(edtEmail.getText());
                contact_no = String.valueOf(edtCpno.getText());
                address = String.valueOf(edtAddress.getText());
                birthdate = String.valueOf(edtBirthdate.getText());

                patientType = spinnerPatientType.getSelectedItem().toString();
                appointment = spinnerAppointment.getSelectedItem().toString();
                complain = spinnerComplaints.getSelectedItem().toString();

                if(!id_no.equals("") && !fname.equals("") && !lname.equals("") && !email.equals("") ){
                    //Start ProgressBar first (Set visibility VISIBLE)
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            //Starting Write and Read data with URL
                            //Creating array for parameters
                            String[] field = new String[9];
                            EditText edtIDno,edtPatient,edtFirstname,edtMiddleName,edtLastname,edtBirthdate,edtAge,edtCpno,edtEmail;

                            field[0] = "first_name";
                            field[1] = "middle_name";
                            field[2] = "last_name";
                            field[3] = "sex";
                            field[4] = "id_no";
                            field[5] = "birthdate";
                            field[6] = "email";
                            field[7] = "contact_no";
                            field[8] = "address";



                            //Creating array for data
                            String[] data = new String[9];

                            data[0] = fname;
                            data[1] = middle;
                            data[2] = lname;
                            data[3] = sex;
                            data[4] = id_no;
                            data[5] = birthdate;
                            data[6] = email;
                            data[7] = contact_no;
                            data[8] = address;


//                            PutData putData = new PutData("http://172.31.250.143/csu_clinic/signup.php", "POST", field, data);
                            PutData putData = new PutData("http://192.168.254.107/csu_clinic/signup.php", "POST", field, data);
                            if (putData.startPut()) {
                                if (putData.onComplete()) {
                                    String result = putData.getResult();
                                    if(result.equals("Created success")){
                                        Toast.makeText(getApplicationContext(), "Successfully save", Toast.LENGTH_SHORT).show();

                                      final SweetAlertDialog pDiaglog =  new SweetAlertDialog(
                                              SignUp.this, SweetAlertDialog.SUCCESS_TYPE);
                                        pDiaglog.setTitleText("Successfully Save");
                                        pDiaglog.setContentText("Please choose corresponding action");
                                        pDiaglog.setConfirmText("Sign Up");
                                        pDiaglog.setCancelText("Add");
                                        pDiaglog.setCancelable(false);
                                        pDiaglog.showCancelButton(true)
                                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                    @Override
                                                    public void onClick(SweetAlertDialog sDialog) {
                                                        Intent in = new Intent(getApplicationContext(), Activity_Splash_Login.class);
                                                        startActivity(in);
                                                        finish();
                                                    }
                                                })
                                                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                    @Override
                                                    public void onClick(SweetAlertDialog sDialog) {
                                                        Intent in = new Intent(getApplicationContext(), SignUp.class);
                                                        startActivity(in);
                                                        finish();
                                                    }
                                                }).show();

//
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
                    new SweetAlertDialog(SignUp.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Error!")
                            .setContentText("All Fields required")
                            .showCancelButton(true)
                            .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                }
                            }).show();
                }
            }
        });

    }
//    ------------- start outside

    private void showDateTimeDialog(final EditText date_time_in) {
        final Calendar calendar=Calendar.getInstance();
        DatePickerDialog.OnDateSetListener dateSetListener=new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR,year);
                calendar.set(Calendar.MONTH,month);
                calendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);

                TimePickerDialog.OnTimeSetListener timeSetListener=new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        calendar.set(Calendar.HOUR_OF_DAY,hourOfDay);
                        calendar.set(Calendar.MINUTE,minute);
                        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm");
                        date_time_in.setText(simpleDateFormat.format(calendar.getTime()));
                    }
                };

                new TimePickerDialog(SignUp.this,timeSetListener,calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE),false).show();
            }
        };

        new DatePickerDialog(SignUp.this,dateSetListener,calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH)).show();

    }

    private void showDateDialog(final EditText date_in) {
        final Calendar calendar=Calendar.getInstance();
        DatePickerDialog.OnDateSetListener dateSetListener=new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR,year);
                calendar.set(Calendar.MONTH,month);
                calendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);
                SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd");
                date_in.setText(simpleDateFormat.format(calendar.getTime()));

            }
        };

        new DatePickerDialog(SignUp.this,dateSetListener,calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if(adapterView.getId() == R.id.spinnerAppointmentCat){
            complaintList.clear();
            String selectedCountry = adapterView.getSelectedItem().toString();
//            String url = "http://10.0.2.2/csu_clinic/populate_city.php?country_name="+selectedCountry;
//            String url = "http://172.31.250.143/csu_clinic/populate_city.php?country_name="+selectedCountry;
            String url = "http://192.168.1.10/csu_clinic/populate_city.php?country_name="+selectedCountry;
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

    public void patientType(){
        patientType.clear();
//        String url = "http://172.31.250.143/csu_clinic/populate_country.php";
//        String url = "http://172.31.250.143/csu_clinic/populate_country.php";
        String url = "http://192.168.1.10/csu_clinic/populatePatientType.php";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray jsonArray = response.getJSONArray("patient_type");
                    for(int i=0; i<jsonArray.length();i++){
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String countryName = jsonObject.optString("type");
                        patientType.add(countryName);
                        patientAdapter = new ArrayAdapter<>(SignUp.this,
                                android.R.layout.simple_spinner_item, patientType);
                        patientAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerPatientType.setAdapter(patientAdapter);
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
        spinnerPatientType.setOnItemSelectedListener(this);
    }

    public void patientDropdown(){
        appointmentList.clear();
//        String url = "http://172.31.250.143/csu_clinic/populate_country.php";
//        String url = "http://172.31.250.143/csu_clinic/populate_country.php";
        String url = "http://192.168.1.10/csu_clinic/populate_country.php";
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



    private boolean shouldIncrementOrDecrement(int currCount, boolean shouldIncrement) {
        if (shouldIncrement) {
            return prevCount <= currCount && isAtSpaceDelimiter(currCount);
        } else {
            return prevCount > currCount && isAtSpaceDelimiter(currCount);
        }
    }
    private void appendOrStrip(String field, boolean shouldAppend) {
        StringBuilder sb = new StringBuilder(field);
        if (shouldAppend) {
            sb.append("-");
        } else {
            sb.setLength(sb.length() - 1);
        }
        edtIDno.setText(sb.toString());
        edtIDno.setSelection(sb.length());
    }
    public void DataAuthentication(){
        edtIDno.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if(s.toString().length() != 9){
                    tilIdno.setError("Not enough length");
                }
                else{
                    tilIdno.setError(null);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
                String field = s.toString();
                int currCount = field.length();

                if (shouldIncrementOrDecrement(currCount, true)){
                    appendOrStrip(field, true);
                } else if (shouldIncrementOrDecrement(currCount, false)) {
                    appendOrStrip(field, false);
                }
                prevCount = edtIDno.getText().toString().length();


            }
        });
        edtFirstname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if(s.toString().length() == 0){
                    tilFname.setError(blankMessage);
                }
                else{
                    tilFname.setError(null);
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        edtLastname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if(s.toString().length() == 0){
                    tilLastname.setError(blankMessage);
                }
                else{
                    tilLastname.setError(null);
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        edtEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if(s.toString().length() == 0){
                    tilEmail.setError(blankMessage);
                }
                else{
                    tilEmail.setError(null);
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


//        });
//        edtBirthdate.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//            @Override
//            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
//                if(s.toString().length() == 0){
//                    tilBdate.setError(blankMessage);
//                }
//                else{
//                    tilBdate.setError(null);
//                }
//            }
//            @Override
//            public void afterTextChanged(Editable editable) {
//
//            }
//        });

    }
}