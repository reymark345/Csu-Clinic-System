package com.example.clinicsys.Appointment.records;

import static com.example.clinicsys.Splash.Activity_Splash_Login.BASE_URL;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.clinicsys.MainActivity;
import com.example.clinicsys.R;
import com.vishnusivadas.advanced_httpurlconnection.PutData;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class HomeRecords extends AppCompatActivity implements AdapterView.OnItemSelectedListener  {

    // Variable declarations
    private Toolbar mToolbar;
    private ActionBar mActionBar;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager manager;
    private RecyclerView.Adapter mAdapter;
    private List<AppointmentRecords> appointments;
    private ProgressBar progressBar;
    public static boolean admin= false;
    ArrayList<String> complaintList = new ArrayList<>();
    ArrayList<String> patientType = new ArrayList<>();
    ArrayList<JSONObject> categories = new ArrayList<>();
    ArrayList<JSONObject> sub_categories = new ArrayList<>();
    ArrayAdapter<String> patientAdapter;
    ArrayAdapter<String> complaintAdapter;
    RequestQueue requestQueue;

    Spinner spnAppointmentCat, spinnerComplaints;
    EditText edtSched, edtComplaints;
    String Urltype,selectedCat, selectSubCat, useridd, type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_records);
        requestQueue = Volley.newRequestQueue(this);
        mToolbar = findViewById(R.id.dashboard_toolbar);
        progressBar = findViewById(R.id.progressbar);
        setSupportActionBar(mToolbar);
        mActionBar = getSupportActionBar();
        recyclerView = findViewById(R.id.products_recyclerView);
        manager = new GridLayoutManager(HomeRecords.this, 1);
        recyclerView.setLayoutManager(manager);
        appointments = new ArrayList<>();
        spinnerComplaints = (Spinner)findViewById(R.id.spnComplaints);
        getAppointment();

    }

    public void showCustomDialog() {

        final Dialog dialog = new Dialog(HomeRecords.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.custom_dialog);
        spnAppointmentCat = dialog.findViewById(R.id.spnAppointmentCat);
        spinnerComplaints = dialog.findViewById(R.id.spnComplaints);
        edtSched = dialog.findViewById(R.id.edtSchedule);
        edtComplaints =  dialog.findViewById(R.id.edtComplaints);


        Button submitButton = dialog.findViewById(R.id.submit_button);
        Category(spnAppointmentCat);
        edtSched.setFocusable(false);
        edtSched.setClickable(true);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                String CatType = spnAppointmentCat.getSelectedItem().toString();
//                String subCategory = spinnerComplaints.getSelectedItem().toString();
                String schedule = edtSched.getText().toString();
                String complaint = edtComplaints.getText().toString();
//                addAppointment(CatType,complaints,schedule,remarks);
                addAppointment(selectedCat,selectSubCat,schedule,complaint);
                dialog.dismiss();
            }
        });
        dialog.show();
        edtSched.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateTimeDialog(edtSched);
            }
        });
    }

    public void Category(Spinner aptCat){
        categories.clear();
        patientType.clear();
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
                                patientType.add(categoryName);
                                patientAdapter = new ArrayAdapter<>(HomeRecords.this,
                                        android.R.layout.simple_spinner_item, patientType);
                                patientAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                aptCat.setAdapter(patientAdapter);

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


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

        if(adapterView.getId() == R.id.spnAppointmentCat){
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
                                    String cityName = object.optString("name");
                                    sub_categories.add(object);
                                    complaintList.add(cityName);
                                    complaintAdapter = new ArrayAdapter<>(HomeRecords.this,
                                            android.R.layout.simple_spinner_item, complaintList);
                                    complaintAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                    spinnerComplaints.setAdapter(complaintAdapter);
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
            spinnerComplaints.setOnItemSelectedListener(this);
        }
        else if (adapterView.getId() == R.id.spnComplaints){

            JSONObject sub_category_data = sub_categories.get(i);
            selectSubCat = sub_category_data.optString("id");
        }
        else{
            Toast.makeText(HomeRecords.this, "Please contact administrator " + selectSubCat ,Toast.LENGTH_LONG).show();
        }
    }
    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public void addAppointment(String CatType,String subCategory,String schedule,String complaint){
        try {

            if (!CatType.equals("") && !subCategory.equals("") && !schedule.equals("")) {
                //Start ProgressBar first (Set visibility VISIBLE)
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        String[] field = new String[4];
                        field[0] = "schedule";
                        field[1] = "category";
                        field[2] = "sub_category";
                        field[3] = "complaint";
                        //Creating array for data
                        String[] data = new String[4];

                        data[0] = schedule;
                        data[1] = CatType;
                        data[2] = subCategory;
                        data[3] = complaint;

                        PutData putData = new PutData(BASE_URL+"/csu_clinic_app/api/appointment/create/"+useridd, "POST", field, data);
                        if (putData.startPut()) {
                            if (putData.onComplete()) {
                                String result = putData.getResult();

                                try{
                                    JSONArray jsonArray = new JSONArray(result);
                                    JSONObject jsonObject1 = jsonArray.getJSONObject(0);
                                    type = jsonObject1.optString("type");
                                    if (type.equals("success")) {
                                        final SweetAlertDialog pDiaglog = new SweetAlertDialog(
                                                HomeRecords.this, SweetAlertDialog.SUCCESS_TYPE);
                                        pDiaglog.setTitleText("Successfully Save");
                                        pDiaglog.setContentText("Done!");
                                        pDiaglog.setConfirmText("Ok");
                                        pDiaglog.setCancelable(false);
                                        pDiaglog.showCancelButton(false)
                                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                    @Override
                                                    public void onClick(SweetAlertDialog sDialog) {
                                                        appointments.clear();
                                                        getAppointment();
                                                        pDiaglog.dismiss();
                                                    }
                                                }).show();

                                    } else {
                                        Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
                                    }
                                }
                                catch (Exception e){
                                    Toast.makeText(getApplicationContext(), "catch error " + e, Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                        //End Write and Read data with URL
                    }
                });
            } else {
                new SweetAlertDialog(HomeRecords.this, SweetAlertDialog.ERROR_TYPE)
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
        catch (Exception e){
            Toast.makeText(getApplicationContext(), "Server Connection Failed", Toast.LENGTH_SHORT).show();
        }
    }
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

                        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yy-MM-dd HH:mm");

                        date_time_in.setText(simpleDateFormat.format(calendar.getTime()));
                    }
                };

                new TimePickerDialog(HomeRecords.this,timeSetListener,calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE),false).show();
            }
        };

        new DatePickerDialog(HomeRecords.this,dateSetListener,calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH)).show();

    }

    public void getAppointment (){
        progressBar.setVisibility(View.VISIBLE);

        SharedPreferences sh = getSharedPreferences("MySharedPref", MODE_APPEND);
        useridd = sh.getString("userId", "");
        String roleName = sh.getString("roleName", "");

        if (roleName.matches("end-user")){
//            Urltype = "/csu_clinic_app/api/appointment/list/user/5/"+useridd;
            Urltype = "/csu_clinic_app/appointment/list/user/5/"+useridd;
        }
        else {
            Urltype = "/csu_clinic_app/appointment/list/5/"+useridd;
        }

//        StringRequest stringRequest = new StringRequest(Request.Method.GET, BASE_URL+"/android/getPendingApt/getAppointment",
        StringRequest stringRequest = new StringRequest(Request.Method.GET, BASE_URL+Urltype,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        progressBar.setVisibility(View.GONE);

                        try {

                            JSONArray array = new JSONArray(response);
                            for (int i = 0; i<array.length(); i++){

//                                Toast.makeText(getApplicationContext(), "Records " + response, Toast.LENGTH_SHORT).show();

                                JSONObject object = array.getJSONObject(i);
                                String idd = object.getString("id");
                                String categoryName = object.getString("category");
                                String subCat = object.getString("subCategory");
                                String schedule = object.getString("schedule");
                                String patientName = object.getString("name");
                                String status = object.getString("status");
                                String userId = useridd;

                                AppointmentRecords appointment = new AppointmentRecords(idd,userId,categoryName,subCat,schedule,patientName,status);
                                appointments.add(appointment);
                                mAdapter = new RecyclerAdapterRecords(HomeRecords.this,appointments);
                                recyclerView.setAdapter(mAdapter);
                            }

                        }catch (Exception e){
                            Toast.makeText(getApplicationContext(), "catch error " + e, Toast.LENGTH_SHORT).show();
                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(HomeRecords.this, "Database connection failed " + error.toString(),Toast.LENGTH_LONG).show();
            }
        });

        Volley.newRequestQueue(HomeRecords.this).add(stringRequest);

    }
    @Override
    public void onBackPressed() {

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }

}
