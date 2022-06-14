package com.example.clinicsys;


import static com.example.clinicsys.Splash.Activity_Splash_Login.BASE_URL;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.clinicsys.Appointment.records.AppointmentRecords;
import com.example.clinicsys.Appointment.records.HomeRecords;
import com.example.clinicsys.Appointment.records.RecyclerAdapterRecords;

import org.json.JSONArray;
import org.json.JSONObject;

public class EditProfile extends AppCompatActivity{

    EditText idNo,edtFirstname,edtMiddle,edtLast_name,edtBdate,edtContact,edtEmail, edtAddress;
    Button btnEdit, btnSave;
    String userType;
    Spinner spnSex,spnPatient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_profile);

        idNo = findViewById(R.id.edt_idno);

        btnEdit = findViewById(R.id.editProfile);

        edtFirstname = findViewById(R.id.edt_Fname);
        edtMiddle = findViewById(R.id.edt_middleName);
        edtLast_name = findViewById(R.id.edt_lastName);
        edtBdate = findViewById(R.id.edt_Birthdate);
        spnSex = findViewById(R.id.spinnerSex);
        edtContact = findViewById(R.id.edt_Contact);
        edtEmail = findViewById(R.id.edt_Email);
        spnPatient = findViewById(R.id.spinnerPatient);
        btnEdit = findViewById(R.id.editProfile);
        edtAddress = findViewById(R.id.edt_address);





        getAppointment();
        disabled();


        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enabled();
            }
        });



    }
    public void getAppointment (){
        SharedPreferences sh = getSharedPreferences("MySharedPref", MODE_APPEND);
        userType = sh.getString("userId", "");
        String linkUrl = "/csu_clinic_app/api/users/show/"+userType;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, BASE_URL+linkUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {

                            JSONObject object = new JSONObject(response);
                            String id_No = object.optString("id_no");


                            JSONObject det = (JSONObject)object.get("details");
                            String fName = det.optString("first_name");
                            String mName = det.optString("middle_name");
                            String lName = det.optString("last_name");
                            String bDate = det.optString("birthdate");
                            String cpNo = det.optString("contact_no");
                            String email = det.optString("email");
                            String address = det.optString("address");

                            idNo.setText(id_No);
                            edtFirstname.setText(fName);
                            edtMiddle.setText(mName);
                            edtLast_name.setText(lName);
                            edtBdate.setText(bDate);
                            edtContact.setText(cpNo);
                            edtEmail.setText(email);
                            edtAddress.setText(address);



//                            for (int i = 0; i<array.length(); i++){
////                                JSONObject object = array.getJSONObject(i);
////                                String categoryID = object.optString("id");
//
//
//                            }

                        }catch (Exception e){
                            Toast.makeText(getApplicationContext(), "catch error " + e, Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(EditProfile.this, "Database connection failed " + error.toString(),Toast.LENGTH_LONG).show();
            }
        });

        Volley.newRequestQueue(EditProfile.this).add(stringRequest);

    }

    public void disabled(){
        idNo.setEnabled(false);
        edtFirstname.setEnabled(false);
    }

    public void enabled(){
        idNo.setEnabled(true);
        edtFirstname.setEnabled(true);
    }

    @Override
    public void onBackPressed() {

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }
}