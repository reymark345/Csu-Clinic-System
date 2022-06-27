package com.example.clinicsys.Splash;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.clinicsys.MainActivity;
import com.example.clinicsys.R;
import com.example.clinicsys.SignUp;
import com.google.android.material.textfield.TextInputLayout;
import com.vishnusivadas.advanced_httpurlconnection.FetchData;
import com.vishnusivadas.advanced_httpurlconnection.PutData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;


public class Activity_Splash_Login extends AppCompatActivity implements AdapterView.OnItemSelectedListener  {

    Button BtnLogin;
    EditText EdtloginIdno,EdtloginPassword;
//    public static final  String BASE_URL = "http://172.31.243.153";
    public static String BASE_URL = "";
    private int prevCount = 0;
    String blankMessage = "Please fill this blank";
    private boolean isAtSpaceDelimiter(int currCount) {
        return currCount == 3;
    }

    ArrayList<String> patientType = new ArrayList<>();
    ArrayAdapter<String> patientAdapter;
    RequestQueue requestQueue;
    TextView newPatient,tv_BaseUrl;
    String user_id,id_number,message,type,role,fname,lname, image, role_id, token;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_login);
        requestQueue = Volley.newRequestQueue(this);
        EdtloginIdno = findViewById(R.id.loginIdno);
        EdtloginPassword = findViewById(R.id.loginPassword);
        BtnLogin = (Button) findViewById(R.id.btnAccess);
        newPatient = (TextView)findViewById(R.id.tv_forgotPassword);
        tv_BaseUrl = (TextView)findViewById(R.id.tv_register);


        newPatient.setPaintFlags(newPatient.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);
//        DataAuthentication();

        SharedPreferences sh = getSharedPreferences("MySharedPref", MODE_APPEND);
        BASE_URL  = sh.getString("urlBased", "");



//        fetchToken();


        BtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SharedPreferences sh = getSharedPreferences("MySharedPref", MODE_APPEND);
                BASE_URL  = sh.getString("urlBased", "");

                String id_no, password;
                id_no = String.valueOf(EdtloginIdno.getText());
                password = String.valueOf(EdtloginPassword.getText());
                String result ="";

                if(!id_no.equals("") && !password.equals("")){
                    //Start ProgressBar first (Set visibility VISIBLE)
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            try{

                                String[] field = new String[2];
                                field[0] = "id_no";
                                field[1] = "password";

                                //Creating array for data
                                String[] data = new String[2];
                                data[0] = id_no;
                                data[1] = password;
                                PutData putData = new PutData(BASE_URL+"/csu_clinic_app/api/auth", "POST", field, data);
                                if (putData.startPut()) {
                                    if (putData.onComplete()) {
                                        String result = putData.getResult();

                                        try
                                        {
                                            JSONArray jsonArray = new JSONArray(result);
                                            JSONObject jsonObject1 = jsonArray.getJSONObject(0);
                                            JSONArray ar = (JSONArray) jsonObject1.get("result");
                                            JSONObject jsonObject2 = ar.getJSONObject(0);

                                            message = jsonObject1.optString("message");
                                            type = jsonObject1.optString("type");
                                            token = jsonObject1.optString("token");
                                            id_number = jsonObject2.optString("id_no");
                                            fname = jsonObject2.optString("first_name");
                                            lname = jsonObject2.optString("last_name");
                                            user_id = jsonObject2.optString("user_id");
                                            role = jsonObject2.optString("role_name");
                                            image = jsonObject2.optString("image");
                                            role_id = jsonObject2.optString("user_role");

                                            SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref",MODE_PRIVATE);
                                            SharedPreferences.Editor myEdit = sharedPreferences.edit();

                                            myEdit.putString("idNo", id_number);
                                            myEdit.putString("firstName", fname);
                                            myEdit.putString("lastName", lname);
                                            myEdit.putString("userId", user_id);
                                            myEdit.putString("roleName", role);
                                            myEdit.putString("imageUrl", image);
                                            myEdit.putString("roleId", role_id);
                                            myEdit.putString("token", token);
                                            myEdit.commit();


                                            if (type.matches("success")){
                                                //                                            Toast.makeText(getApplicationContext(), "test " + id, Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                                startActivity(intent);
                                                finish();
                                            }
                                            else{
                                                Toasty.error(Activity_Splash_Login.this, message, Toast.LENGTH_SHORT, true).show();
                                            }

                                        }
                                        catch (JSONException e)
                                        {
                                            Toasty.error(Activity_Splash_Login.this, "ID no./Password is wrong", Toast.LENGTH_SHORT, true).show();
                                        }

                                    }
                                }

                            }

                            catch (Exception e){
                                Toast.makeText(getApplicationContext(), "Errs " + e, Toast.LENGTH_SHORT).show();
                            }


                            //End Write and Read data with URL
                           }
                    });

                }
                else {
                    Toasty.error(Activity_Splash_Login.this, "All fields required ", Toast.LENGTH_SHORT, true).show();
                }
            }
        });


        newPatient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SignUp.class);
                startActivity(intent);
//                finish();

            }
        });

        tv_BaseUrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomDialogCancel();

            }
        });
    }

    public void CustomDialogCancel() {

        final Dialog dialog = new Dialog(Activity_Splash_Login.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.base_url);
        EditText noteField = dialog.findViewById(R.id.edtBaseUrl);
        TextInputLayout tilError = dialog.findViewById(R.id.til_baseUrl);
        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref",MODE_PRIVATE);
        SharedPreferences sh = getSharedPreferences("MySharedPref", MODE_APPEND);
        String url = sh.getString("urlBased", "");
        noteField.setText(url);

        Button submitButton = dialog.findViewById(R.id.btn_baseUrl);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (noteField.length()==0){
                    tilError.setError("Please fill this blank");
                }
                else {
                    String remarks = noteField.getText().toString();
                    SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref",MODE_PRIVATE);
                    String urls = remarks;
                    SharedPreferences.Editor myEdit = sharedPreferences.edit();
                    myEdit.putString("urlBased", urls);
                    myEdit.commit();
                    dialog.dismiss();
                    Toasty.success(Activity_Splash_Login.this, "BASE URL Successfully Save "+urls, Toast.LENGTH_SHORT, true).show();
                }
            }
        });
        dialog.show();
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

//    private boolean shouldIncrementOrDecrement(int currCount, boolean shouldIncrement) {
//        if (shouldIncrement) {
//            return prevCount <= currCount && isAtSpaceDelimiter(currCount);
//        } else {
//            return prevCount > currCount && isAtSpaceDelimiter(currCount);
//        }
//    }
//    private void appendOrStrip(String field, boolean shouldAppend) {
//        StringBuilder sb = new StringBuilder(field);
//        if (shouldAppend) {
//            sb.append("-");
//        } else {
//            sb.setLength(sb.length() - 1);
//        }
//        EdtloginIdno.setText(sb.toString());
//        EdtloginIdno.setSelection(sb.length());
//    }
//    public void DataAuthentication(){
//        EdtloginIdno.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//            @Override
//            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
//                if(s.toString().length() != 9){
//                    EdtloginIdno.setError("Not enough length");
//                }
//                else{
//                    EdtloginIdno.setError(null);
//                }
//            }
//            @Override
//            public void afterTextChanged(Editable s) {
//                String field = s.toString();
//                int currCount = field.length();
//
//                if (shouldIncrementOrDecrement(currCount, true)){
//                    appendOrStrip(field, true);
//                } else if (shouldIncrementOrDecrement(currCount, false)) {
//                    appendOrStrip(field, false);
//                }
//                prevCount = EdtloginIdno.getText().toString().length();
//
//
//            }
//        });
//    }
}