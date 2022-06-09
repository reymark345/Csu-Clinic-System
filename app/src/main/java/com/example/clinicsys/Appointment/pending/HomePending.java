package com.example.clinicsys.Appointment.pending;

import static com.example.clinicsys.Splash.Activity_Splash_Login.BASE_URL;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
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
import com.example.clinicsys.SignUp;
import com.example.clinicsys.Splash.Activity_Splash_Login;
import com.vishnusivadas.advanced_httpurlconnection.PutData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class HomePending extends AppCompatActivity implements AdapterView.OnItemSelectedListener  {

    // Variable declarations
    private Toolbar mToolbar;
    private ActionBar mActionBar;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager manager;
    private RecyclerView.Adapter mAdapter;
    private List<AppointmentPending> appointments;
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
    EditText edtSched, edtRemarks;
    String Urltype,categoryID,selectedCat, selectSubCat;

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.action_add){
            showCustomDialog();
        }

        return true;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.dashboard_menu,menu);

        return true;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_pending);
        requestQueue = Volley.newRequestQueue(this);
        mToolbar = findViewById(R.id.dashboard_toolbar);
        progressBar = findViewById(R.id.progressbar);
        setSupportActionBar(mToolbar);
        mActionBar = getSupportActionBar();
        recyclerView = findViewById(R.id.products_recyclerView);
        manager = new GridLayoutManager(HomePending.this, 1);
        recyclerView.setLayoutManager(manager);
        appointments = new ArrayList<>();
        spinnerComplaints = (Spinner)findViewById(R.id.spnComplaints);
        getAppointment();

    }

   public void showCustomDialog() {

        final Dialog dialog = new Dialog(HomePending.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.custom_dialog);
        spnAppointmentCat = dialog.findViewById(R.id.spnAppointmentCat);
        spinnerComplaints = dialog.findViewById(R.id.spnComplaints);
        edtSched = dialog.findViewById(R.id.edtSchedule);
        edtRemarks =  dialog.findViewById(R.id.edtRemarks);


        Button submitButton = dialog.findViewById(R.id.submit_button);
        Category(spnAppointmentCat);
        edtSched.setFocusable(false);
        edtSched.setClickable(true);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String CatType = spnAppointmentCat.getSelectedItem().toString();
                String complaints = spinnerComplaints.getSelectedItem().toString();
                String schedule = edtSched.getText().toString();
                String remarks = edtRemarks.getText().toString();
                addAppointment(CatType,complaints,schedule,remarks);
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
                                patientAdapter = new ArrayAdapter<>(HomePending.this,
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
            Toast.makeText(HomePending.this, "Selected1 " + selectedCat ,Toast.LENGTH_LONG).show();
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
                                    complaintAdapter = new ArrayAdapter<>(HomePending.this,
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
            Toast.makeText(HomePending.this, "Selected2 " + selectSubCat ,Toast.LENGTH_LONG).show();

//            StringRequest stringRequest = new StringRequest(Request.Method.GET, BASE_URL+"/csu_clinic_app/api/sub_category/list/2/"+selectedCat,
//                    new Response.Listener<String>() {
//                        @Override
//                        public void onResponse(String response) {
//                            try {
//                                JSONArray array = new JSONArray(response);
//                                for (int i = 0; i<array.length(); i++){
//                                    JSONObject object = array.getJSONObject(i);
//                                    String cityName = object.optString("name");
//                                    sub_categories.add(object);
//                                    complaintList.add(cityName);
//                                    complaintAdapter = new ArrayAdapter<>(HomePending.this,
//                                            android.R.layout.simple_spinner_item, complaintList);
//                                    complaintAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                                    spinnerComplaints.setAdapter(complaintAdapter);
//                                }
//
//                            }catch (Exception e){
//                                e.printStackTrace();
//                            }
//
//                        }
//                    }, new Response.ErrorListener() {
//                @Override
//                public void onErrorResponse(VolleyError error) {
//
//                }
//            });
//            requestQueue.add(stringRequest);

        }
//        else{
//            Toast.makeText(HomePending.this, "sa error " ,Toast.LENGTH_LONG).show();
//        }
    }





//    public void patientTypeOLD(Spinner aptCat){
//        patientType.clear();
////        String url = "http://172.31.250.174/csu_clinic/populate_country.php";
//
//
//        String url = "http://192.168.1.3/csu_clinic_app/api/category/list/2";
////        String url = "http://192.168.254.109/csu_clinic/populate_country.php";
//        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
//                url, null, new Response.Listener<JSONObject>() {
//            @Override
//            public void onResponse(JSONObject response) {
//                try {
//                    Toast.makeText(HomePending.this, "sa error " ,Toast.LENGTH_LONG).show();
//                    JSONArray jsonArray = response.getJSONArray("lib_categories");
//                    for(int i=0; i<jsonArray.length();i++){
//                        JSONObject jsonObject = jsonArray.getJSONObject(i);
//                        String countryName = jsonObject.optString("name");
//                        patientType.add(countryName);
//                        patientAdapter = new ArrayAdapter<>(HomePending.this,
//                                android.R.layout.simple_spinner_item, patientType);
//                        patientAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                        aptCat.setAdapter(patientAdapter);
//                    }
//                    Toast.makeText(getApplicationContext(), "Pila? "+patientType.size(), Toast.LENGTH_SHORT).show();
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//
//            }
//        });
//        requestQueue.add(jsonObjectRequest);
//        aptCat.setOnItemSelectedListener(this);
//    }


//    @Override
//    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//        if(adapterView.getId() == R.id.spnAppointmentCat){
//            Toast.makeText(HomePending.this, "Tyyyyy" ,Toast.LENGTH_LONG).show();
//            complaintList.clear();
//            String selectedCountry = adapterView.getSelectedItem().toString();
//            String url = BASE_URL+"/csu_clinic_app/api/sub_category/list/2/"+selectedCountry;
//            requestQueue = Volley.newRequestQueue(this);
//            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
//                    url, null, new Response.Listener<JSONObject>() {
//                @Override
//                public void onResponse(JSONObject response) {
////                    try {
////                        JSONArray jsonArray = response.getJSONArray("sub_cat");
////                        for(int i=0; i<jsonArray.length();i++){
////                            JSONObject jsonObject = jsonArray.getJSONObject(i);
////                            String cityName = jsonObject.optString("name");
////
////                            complaintList.add(cityName);
////                            complaintAdapter = new ArrayAdapter<>(HomePending.this,
////                                    android.R.layout.simple_spinner_item, complaintList);
////                            complaintAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
////                            spinnerComplaints.setAdapter(complaintAdapter);
////                        }
////                    } catch (JSONException e) {
////                        e.printStackTrace();
////                    }
//                }
//            }, new Response.ErrorListener() {
//                @Override
//                public void onErrorResponse(VolleyError error) {
//
//                }
//            });
//            requestQueue.add(jsonObjectRequest);
////            ageEt.setOnItemSelectedListener(this);
//        }
//        else{
//            Toast.makeText(HomePending.this, "sa error " ,Toast.LENGTH_LONG).show();
//        }
//    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public void addAppointment(String CatType,String complaints,String schedule,String remarks){
        try {
            if (!CatType.equals("") && !complaints.equals("") && !schedule.equals("")) {
                //Start ProgressBar first (Set visibility VISIBLE)
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        String[] field = new String[4];
                        field[0] = "category_name";
                        field[1] = "sub_category";
                        field[2] = "schedule";
                        field[3] = "remarks";
                        //Creating array for data
                        String[] data = new String[4];

                        data[0] = CatType;
                        data[1] = complaints;
                        data[2] = schedule;
                        data[3] = remarks;
                        PutData putData = new PutData(BASE_URL+"/csu_clinic/AddNewApt.php", "POST", field, data);
                        if (putData.startPut()) {
                            if (putData.onComplete()) {
                                String result = putData.getResult();
                                if (result.equals("Created success")) {
                                    final SweetAlertDialog pDiaglog = new SweetAlertDialog(
                                            HomePending.this, SweetAlertDialog.SUCCESS_TYPE);
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
                                //End ProgressBar (Set visibility to GONE)
                                Log.i("PutData", result);
                            }
                        }
                        //End Write and Read data with URL
                    }
                });
            } else {
                new SweetAlertDialog(HomePending.this, SweetAlertDialog.ERROR_TYPE)
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

                new TimePickerDialog(HomePending.this,timeSetListener,calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE),false).show();
            }
        };

        new DatePickerDialog(HomePending.this,dateSetListener,calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH)).show();

    }

    public void getAppointment (){
        progressBar.setVisibility(View.VISIBLE);

        SharedPreferences sh = getSharedPreferences("MySharedPref", MODE_APPEND);
        String useridd = sh.getString("userId", "");
        String roleName = sh.getString("roleName", "");

        Toast.makeText(HomePending.this, "type s" + roleName,Toast.LENGTH_LONG).show();


        if (roleName.matches("end-user")){
            Urltype = "/csu_clinic_app/api/appointment/list/user/0/"+useridd;
        }
        else {
            Urltype = "/csu_clinic_app/api/appointment/list/0/"+useridd;
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

                                JSONObject object = array.getJSONObject(i);

//                                Toast.makeText(HomePending.this, "Testing list" + useridd,Toast.LENGTH_LONG).show();

                                String idd = object.getString("id");
                                String categoryName = object.getString("category");
                                String subCat = object.getString("subCategory");
                                String schedule = object.getString("schedule");
                                String userId = useridd;
//                                String image = object.getString("image");

//                                String rate = String.valueOf(rating);
//                                float newRate = Float.valueOf(rate);

                                AppointmentPending appointment = new AppointmentPending(idd,userId,categoryName,subCat,schedule);
                                appointments.add(appointment);
                            }

                        }catch (Exception e){

                        }

                        mAdapter = new RecyclerAdapterPending(HomePending.this,appointments);
                        recyclerView.setAdapter(mAdapter);


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(HomePending.this, "Database connection failed " + error.toString(),Toast.LENGTH_LONG).show();
            }
        });

        Volley.newRequestQueue(HomePending.this).add(stringRequest);

    }

}
