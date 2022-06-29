package com.example.clinicsys.Profile;


import static com.example.clinicsys.Splash.Activity_Splash_Login.BASE_URL;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.clinicsys.MainActivity;
import com.example.clinicsys.R;
import com.vishnusivadas.advanced_httpurlconnection.PutData;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;
import es.dmoral.toasty.Toasty;

public class EditProfile extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    EditText idNo,edtFirstname,edtMiddle,edtLast_name,edtBdate,edtContact,edtEmail, edtAddress;
    Button btnEdit, btnChangePass;
    String userId, selectedPatient, roleId;
    Spinner spnSex,spnPatient;
    boolean editable = false;
    Button btnUpdate;

    ArrayList<String> patientList = new ArrayList<>();
    ArrayList<JSONObject> patientType = new ArrayList<>();
    ArrayAdapter<String> patientAdapter;
    ArrayAdapter<String> adapterSex;
    RequestQueue requestQueue;
    ImageView profileImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_profile);
        requestQueue = Volley.newRequestQueue(this);


        idNo = findViewById(R.id.edt_idno);
        btnEdit = findViewById(R.id.editProfile);
        btnChangePass = findViewById(R.id.editPassword);
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
        btnUpdate = findViewById(R.id.updateProfileBtn);
        btnUpdate.setVisibility(View.GONE);

        profileImage = (ImageView) findViewById(R.id.imageview_account_profile);

        edtFirstname.setFilters(new InputFilter[] {new InputFilter.AllCaps()});
        edtMiddle.setFilters(new InputFilter[] {new InputFilter.AllCaps()});
        edtLast_name.setFilters(new InputFilter[] {new InputFilter.AllCaps()});
        edtAddress.setFilters(new InputFilter[] {new InputFilter.AllCaps()});

        getAppointment();
        disabled();
        PatientType(spnPatient);

        String[] Gender = new String[]{"male", "female"};

        adapterSex = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, Gender);
        spnSex.setAdapter(adapterSex);

        SharedPreferences sh = getSharedPreferences("MySharedPref", MODE_APPEND);
        String imageUrl = sh.getString("imageUrl", "");

        if (!imageUrl.matches("null")){
            Glide.with(this)
                    .load(BASE_URL+"/csu_clinic_app/storage/profile_img/"+imageUrl)
                    .into(profileImage);
        }


        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editable==false){
                    enabled();
                    Toasty.success(EditProfile.this, "Edit profile", Toast.LENGTH_SHORT, true).show();
                    btnEdit.setBackgroundColor(Color.parseColor("#fcba03"));
                    btnUpdate.setVisibility(View.VISIBLE);
                    editable = true;
                    btnEdit.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_cancel, 0, 0, 0);
                }
                else{
                    disabled();
                    btnEdit.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_edit, 0, 0, 0);
                    btnEdit.setBackgroundColor(Color.parseColor("#667AFF"));
                    btnUpdate.setVisibility(View.GONE);
                    editable = false;
                }
            }
        });
        btnChangePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ChangePassword.class);
                startActivity(intent);
                finish();
            }
        });
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfile();
            }
        });
    }
    public void updateProfile(){
        try {


//            ToasterBuilderKtx.prepareToast(this) {
//                message = "File uploaded successfully"
//                leftDrawableRes = R.drawable.ic_baseline_cloud_done_24
//                leftDrawableTint = R.color.blue
//                stripTint = R.color.blue
//                duration = Toaster.LENGTH_SHORT
//            }.show()

            String id = idNo.getText().toString();
            String fname = edtFirstname.getText().toString();
            String middle = edtMiddle.getText().toString();
            String lname = edtLast_name.getText().toString();
            String bday = edtBdate.getText().toString();
            String sex = spnSex.getSelectedItem().toString();
            String contact = edtContact.getText().toString();
            String email = edtEmail.getText().toString();
            String patient = spnPatient.getSelectedItem().toString();
            String address = edtAddress.getText().toString();

            //Start ProgressBar first (Set visibility VISIBLE)
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    String[] field = new String[12];
                    field[0] = "id_no";
                    field[1] = "first_name";
                    field[2] = "middle_name";
                    field[3] = "last_name";
                    field[4] = "birthdate";
                    field[5] = "contact_no";
                    field[6] = "email";
                    field[7] = "address";
                    field[8] = "id";
                    field[9] = "sex";
                    field[10] = "patient_id";
                    field[11] = "role_id";

                    //Creating array for data
                    String[] data = new String[12];
                    data[0] = id;
                    data[1] = fname;
                    data[2] = middle;
                    data[3] = lname;
                    data[4] = bday;
                    data[5] = contact;
                    data[6] = email;
                    data[7] = address;
                    data[8] = userId;
                    data[9] = sex;
                    data[10] = selectedPatient;
                    data[11] = roleId;


                    PutData putData = new PutData(BASE_URL+"/csu_clinic_app/api/users/update", "POST", field, data);
//                        PutData putData = new PutData("http://192.168.254.109/csu_clinic/CancelApt.php", "POST", field, data);
                    if (putData.startPut()) {
                        if (putData.onComplete()) {
                            String result = putData.getResult();
                            try {
//                                Toast.makeText(getApplicationContext(), "Mo error doctype" + result, Toast.LENGTH_SHORT).show();
                                JSONArray jsonArray = new JSONArray(result);
                                JSONObject jsonObject1 = jsonArray.getJSONObject(0);

                                String message = jsonObject1.optString("message");
                                String type = jsonObject1.optString("type");
//                                String type = "successaa";

                                if (type.equals("success")) {
                                    final SweetAlertDialog pDiaglog = new SweetAlertDialog(
                                            EditProfile.this, SweetAlertDialog.SUCCESS_TYPE);
                                    pDiaglog.setTitleText("Successfully Save");
                                    pDiaglog.setContentText("Profile save!");
                                    pDiaglog.setConfirmText("Ok");
                                    pDiaglog.setCancelable(false);
                                    pDiaglog.showCancelButton(false)
                                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                @Override
                                                public void onClick(SweetAlertDialog sDialog) {

//                                                    Intent in = new Intent(EditProfile.this.getApplicationContext(), EditProfile.class);
//                                                    EditProfile.this.startActivity(in);
//                                                    ((Activity) EditProfile.this).finish();
                                                    pDiaglog.dismiss();

                                                }
                                            }).show();

                                } else {
                                    Toast.makeText(EditProfile.this, "fasf", Toast.LENGTH_SHORT).show();
                                }
                                //End ProgressBar (Set visibility to GONE)
                                Log.i("PutData", result);
                            }
                            catch (Exception e)
                            {
                                Toasty.error(EditProfile.this, "Id number already exists", Toast.LENGTH_SHORT, true).show();
                            }
                        }

                    }
                    //End Write and Read data with URL
                }
            });
        }
        catch (Exception e){
            Toast.makeText(EditProfile.this, "Server Connection Failed", Toast.LENGTH_SHORT).show();
        }
    }
    public void getAppointment (){
        SharedPreferences sh = getSharedPreferences("MySharedPref", MODE_APPEND);
        userId = sh.getString("userId", "");
        roleId = sh.getString("roleId", "");

        String linkUrl = "/csu_clinic_app/api/users/show/"+userId;

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
    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

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
                                patientAdapter = new ArrayAdapter<>(EditProfile.this,
                                        android.R.layout.simple_spinner_item, patientList);
                                patientAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                aptPatientType.setAdapter(patientAdapter);

                            }


                            new android.os.Handler(Looper.getMainLooper()).postDelayed(
                                    new Runnable() {
                                        public void run() {
                                            PatientReplace();
                                        }
                                    },
                                    500);

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


    public void PatientReplace(){
        SharedPreferences sh = getSharedPreferences("MySharedPref", MODE_APPEND);
        String useridd = sh.getString("userId", "");
        StringRequest stringRequest = new StringRequest(Request.Method.GET, BASE_URL+"/csu_clinic_app/api/users/show/"+useridd,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject object = new JSONObject(response);
                            String sex = object.getString("details");
                            String patient = object.getString("patient");
                            JSONObject object2 = new JSONObject(sex);
                            String gender = object2.getString("sex");
                            JSONObject object3 = new JSONObject(patient);
                            String typeName = object3.getString("type");

                            new android.os.Handler(Looper.getMainLooper()).postDelayed(
                                    new Runnable() {
                                        public void run() {
                                            patientAdapter = (ArrayAdapter) spnPatient.getAdapter(); //cast to an ArrayAdapter
                                            int spnAdapter = patientAdapter.getPosition(typeName);
                                            spnPatient.setSelection(spnAdapter);
                                            adapterSex = (ArrayAdapter) spnSex.getAdapter();
                                            int spinnerSex = adapterSex.getPosition(gender);
                                            spnSex.setSelection(spinnerSex);
                                        }
                                    },
                                    500);
                        }catch (Exception e){
                            Toast.makeText(EditProfile.this, "error" + e ,Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        requestQueue.add(stringRequest);
    }
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        JSONObject category_data = patientType.get(i);
        selectedPatient = category_data.optString("id");
    }
    public void disabled(){

        idNo.setInputType(InputType.TYPE_NULL);
        edtFirstname.setInputType(InputType.TYPE_NULL);
        edtMiddle.setInputType(InputType.TYPE_NULL);
        edtLast_name.setInputType(InputType.TYPE_NULL);
        edtBdate.setInputType(InputType.TYPE_NULL);
        spnSex.setEnabled(false);
        spnPatient.setEnabled(false);
        edtContact.setInputType(InputType.TYPE_NULL);
        edtEmail.setInputType(InputType.TYPE_NULL);
        edtAddress.setInputType(InputType.TYPE_NULL);
    }

    public void enabled(){
        idNo.setInputType(InputType.TYPE_CLASS_TEXT);
        edtFirstname.setInputType(InputType.TYPE_CLASS_TEXT);
        edtFirstname.setInputType(InputType.TYPE_CLASS_TEXT);
        edtMiddle.setInputType(InputType.TYPE_CLASS_TEXT);
        edtLast_name.setInputType(InputType.TYPE_CLASS_TEXT);
        edtBdate.setInputType(InputType.TYPE_CLASS_TEXT);
        spnSex.setEnabled(true);
        spnPatient.setEnabled(true);
        edtContact.setInputType(InputType.TYPE_CLASS_TEXT);
        edtEmail.setInputType(InputType.TYPE_CLASS_TEXT);
        edtAddress.setInputType(InputType.TYPE_CLASS_TEXT);
    }

    @Override
    public void onBackPressed() {

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }
}