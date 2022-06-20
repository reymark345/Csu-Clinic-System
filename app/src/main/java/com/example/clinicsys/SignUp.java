package com.example.clinicsys;


import static com.example.clinicsys.Splash.Activity_Splash_Login.BASE_URL;

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
import android.widget.ProgressBar;
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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.clinicsys.Appointment.pending.HomePending;
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

    EditText edtIDno,edtFirstname,edtMiddleName,edtLastname,edtBirthdate,edtAddress,edtCpno,edtEmail,edtSchedule, edtComplaint;
    Button buttonSignup;
    private int prevCount = 0;
    String selectedCat, selectSubCat, selectedPatient, type;

    Spinner spinnerPatientType,spinnerAppointment,spinnerSubCat, spinnerSex;

    ArrayList<String> patientList = new ArrayList<>();
    ArrayList<String> complaintList = new ArrayList<>();
    ArrayList<String> aptCategory = new ArrayList<>();

    ArrayList<JSONObject> patientType = new ArrayList<>();
    ArrayList<JSONObject> categories = new ArrayList<>();
    ArrayList<JSONObject> sub_categories = new ArrayList<>();

    ArrayAdapter<String> patientAdapter;
    ArrayAdapter<String> categoriesAdapter;
    ArrayAdapter<String> complaintAdapter;
    RequestQueue requestQueue;

    TextInputLayout tilIdno, tilFname, tilMiddle,tilLastname, tilSex, tilAddress, tilBdate, til_SubCat, tilCpno, tilEmail;
    String blankMessage = "Please fill this blank";
    private ProgressBar progressBar;
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
        spinnerSubCat = (Spinner)findViewById(R.id.spinnerSubCat);
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
        edtComplaint = findViewById(R.id.edtComplaint);
        progressBar = findViewById(R.id.progressbarSignup);


        buttonSignup = findViewById(R.id.confirmRequestBtn);

        //layoutMaterial
        tilIdno = findViewById(R.id.til_Idno);
        tilFname = findViewById(R.id.til_firstname);
        tilMiddle = findViewById(R.id.til_middlename);
        tilLastname = findViewById(R.id.til_lastname);
        tilSex = findViewById(R.id.til_sex);
        tilAddress = findViewById(R.id.til_address);
        tilBdate = findViewById(R.id.til_birthdate);
        til_SubCat = findViewById(R.id.til_subCat);
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
//        patientType(spinnerPatientType);
        Category(spinnerAppointment);

        PatientType(spinnerPatientType);
        DataAuthentication();

        buttonSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    String id_no, patientType, fname, middle, lname, category, sub_category, sex, birthdate,schedule, email, contact_no, address, complaint;
                    id_no = String.valueOf(edtIDno.getText());
                    fname = String.valueOf(edtFirstname.getText());
                    middle = String.valueOf(edtMiddleName.getText());
                    lname = String.valueOf(edtLastname.getText());
                    birthdate = String.valueOf(edtBirthdate.getText());
                    schedule = String.valueOf(edtSchedule.getText());
                    sex = spinnerSex.getSelectedItem().toString();
                    contact_no = String.valueOf(edtCpno.getText());
//                    email = String.valueOf(edtEmail.getText());
                    address = String.valueOf(edtAddress.getText());
                    complaint = String.valueOf(edtComplaint.getText());
//                    patientType = spinnerPatientType.getSelectedItem().toString();
//                    category = spinnerAppointment.getSelectedItem().toString();
//                    sub_category = spinnerSubCat.getSelectedItem().toString();

                    email = edtEmail.getText().toString().trim();
                    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

                    if (!email.matches(emailPattern)){
                        tilEmail.setError("Invalid Email e.g kevin@gmail.com");
                    }
                    else if (!id_no.equals("") && !fname.equals("") && !lname.equals("")&& !birthdate.equals("")&& !schedule.equals("")&& !contact_no.equals("") && !email.equals("") && !address.equals("")) {
                        disabledAction();
                        progressBar.setVisibility(View.VISIBLE);
                        //Start ProgressBar first (Set visibility VISIBLE)
                        Handler handler = new Handler(Looper.getMainLooper());
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                String[] field = new String[14];
                                EditText edtIDno, edtPatient, edtFirstname, edtMiddleName, edtLastname, edtBirthdate, edtAge, edtCpno, edtEmail;

                                field[0] = "id_no";
                                field[1] = "first_name";
                                field[2] = "middle_name";
                                field[3] = "last_name";
                                field[4] = "birthday";
                                field[5] = "schedule";
                                field[6] = "sex";
                                field[7] = "cellphone_no";
                                field[8] = "email";
                                field[9] = "address";
                                field[10] = "patient_type";
                                field[11] = "category";
                                field[12] = "sub_category";
                                field[13] = "complaint";
                                //Creating array for data
                                String[] data = new String[14];
                                data[0] = id_no;
                                data[1] = fname;
                                data[2] = middle;
                                data[3] = lname;
                                data[4] = birthdate;
                                data[5] = schedule;
                                data[6] = sex;
                                data[7] = contact_no;
                                data[8] = email;
                                data[9] = address;
                                data[10] = selectedPatient;
                                data[11] = selectedCat;
                                data[12] = selectSubCat;
                                data[13] = complaint;
//
                                PutData putData = new PutData(BASE_URL + "/csu_clinic_app/api/appointment/create/new", "POST", field, data);

                                if (putData.startPut()) {
                                    if (putData.onComplete()) {
                                        String result = putData.getResult();

                                        try{
                                            JSONArray jsonArray = new JSONArray(result);
                                            JSONObject jsonObject1 = jsonArray.getJSONObject(0);
                                            type = jsonObject1.optString("type");
                                            if (type.equals("success")) {
                                                enabledAction();
                                                progressBar.setVisibility(View.GONE);
                                                final SweetAlertDialog pDiaglog = new SweetAlertDialog(
                                                        SignUp.this, SweetAlertDialog.SUCCESS_TYPE);
                                                pDiaglog.setTitleText("Booked appointment successfully ");
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
                                                //End ProgressBar (Set visibility to GONE)
                                                Log.i("PutData", result);

                                            }
                                            else {
                                                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
                                            }


                                        }
                                        catch (Exception e){
                                            enabledAction();
                                            progressBar.setVisibility(View.GONE);
//                                            new SweetAlertDialog(SignUp.this, SweetAlertDialog.ERROR_TYPE)
//                                                    .setTitleText("Error!")
//                                                    .setContentText("ID number already exists")
//                                                    .showCancelButton(true)
//                                                    .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
//                                                        @Override
//                                                        public void onClick(SweetAlertDialog sDialog) {
//                                                        }
//                                                    }).show();
                                        }



                                    }
                                    //End Write and Read data with URL
                                }
                            }
                        });
                    } else {
                        new SweetAlertDialog(SignUp.this, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Error!")
                                .setContentText("Fields are required")
                                .showCancelButton(true)
                                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog) {
                                    }
                                }).show();
                    }
                }
                 catch (Exception e){
                    Toast.makeText(getApplicationContext(), "Server Connection Failed", Toast.LENGTH_SHORT).show();
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
            sub_categories.clear();
            JSONObject category_data = categories.get(i);
            selectedCat = category_data.optString("id");
            StringRequest stringRequest = new StringRequest(Request.Method.GET, BASE_URL+"/csu_clinic_app/api/sub_category/list/2/"+selectedCat,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONArray array = new JSONArray(response);
                                for (int i = 0; i<array.length(); i++){
                                    JSONObject object = array.getJSONObject(i);
                                    String appointName = object.optString("name");
                                    sub_categories.add(object);
                                    complaintList.add(appointName);
                                    complaintAdapter = new ArrayAdapter<>(SignUp.this,
                                            android.R.layout.simple_spinner_item, complaintList);
                                    complaintAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                    spinnerSubCat.setAdapter(complaintAdapter);
                                }

                            }catch (Exception e){
                                e.printStackTrace();
                            }

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });
            requestQueue.add(stringRequest);
            spinnerSubCat.setOnItemSelectedListener(this);
        }
        else if (adapterView.getId() == R.id.spinnerSubCat){
            JSONObject sub_category_data = sub_categories.get(i);
            selectSubCat = sub_category_data.optString("id");
        }
        else if (adapterView.getId() == R.id.spinnerPatientType){
            JSONObject patient = patientType.get(i);
            selectedPatient = patient.optString("id");
        }
        else{
            Toast.makeText(SignUp.this, "Please contact administrator " + selectSubCat ,Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
    public void patientTypeaa(Spinner aptType){
//        categories.clear();
        aptCategory.clear();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, BASE_URL+"/csu_clinic_app/api/patient_types/list/2",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {

                            JSONArray array = new JSONArray(response);
                            for (int i = 0; i<array.length(); i++){
                                JSONObject object = array.getJSONObject(i);
                                String categoryID = object.optString("id");
                                String categoryName = object.optString("name");
                                categories.add(object);
                                aptCategory.add(categoryName);
                                categoriesAdapter = new ArrayAdapter<>(SignUp.this,
                                        android.R.layout.simple_spinner_item, aptCategory);
                                categoriesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                aptType.setAdapter(categoriesAdapter);

                            }

                        }catch (Exception e){
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        requestQueue.add(stringRequest);
        aptType.setOnItemSelectedListener(this);
    }

    public void PatientType(Spinner aptPatientType){
        patientList.clear();
//        categories.clear();
//        aptCategory.clear();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, BASE_URL+"/csu_clinic_app/api/patient_types/list/2",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {

                            JSONArray array = new JSONArray(response);
                            for (int i = 0; i<array.length(); i++){
                                JSONObject object = array.getJSONObject(i);
                                String patientName = object.optString("type");
                                patientType.add(object);
                                patientList.add(patientName);
                                patientAdapter = new ArrayAdapter<>(SignUp.this,
                                        android.R.layout.simple_spinner_item, patientList);
                                patientAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                aptPatientType.setAdapter(patientAdapter);

                            }

                        }catch (Exception e){
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        requestQueue.add(stringRequest);
        aptPatientType.setOnItemSelectedListener(this);
    }

    public void disabledAction(){
        edtIDno.setEnabled(false);
        edtFirstname.setEnabled(false);
        edtMiddleName.setEnabled(false);
        edtLastname.setEnabled(false);
        edtBirthdate.setEnabled(false);
        edtAddress.setEnabled(false);
        edtCpno.setEnabled(false);
        edtEmail.setEnabled(false);
        edtSchedule.setEnabled(false);
        edtComplaint.setEnabled(false);
        edtIDno.setEnabled(false);
//        buttonSignup.setEnabled(false);
        spinnerPatientType.setEnabled(false);
        spinnerAppointment.setEnabled(false);
        spinnerSubCat.setEnabled(false);
        spinnerSex.setEnabled(false);
    }

    public void enabledAction(){
        edtIDno.setEnabled(true);
        edtFirstname.setEnabled(true);
        edtMiddleName.setEnabled(true);
        edtLastname.setEnabled(true);
        edtBirthdate.setEnabled(true);
        edtAddress.setEnabled(true);
        edtCpno.setEnabled(true);
        edtEmail.setEnabled(true);
        edtSchedule.setEnabled(true);
        edtComplaint.setEnabled(true);
        edtIDno.setEnabled(true);
//        buttonSignup.setEnabled(true);
        spinnerPatientType.setEnabled(true);
        spinnerAppointment.setEnabled(true);
        spinnerSubCat.setEnabled(true);
        spinnerSex.setEnabled(true);
    }

    public void Category(Spinner aptCat){
        categories.clear();
        aptCategory.clear();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, BASE_URL+"/csu_clinic_app/api/category/list/2",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {

                            JSONArray array = new JSONArray(response);
                            for (int i = 0; i<array.length(); i++){
                                JSONObject object = array.getJSONObject(i);
                                String categoryID = object.optString("id");
                                String categoryName = object.optString("name");
                                categories.add(object);
                                aptCategory.add(categoryName);
                                categoriesAdapter = new ArrayAdapter<>(SignUp.this,
                                        android.R.layout.simple_spinner_item, aptCategory);
                                categoriesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                aptCat.setAdapter(categoriesAdapter);

                            }

                        }catch (Exception e){
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        requestQueue.add(stringRequest);
        aptCat.setOnItemSelectedListener(this);
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
    }
}