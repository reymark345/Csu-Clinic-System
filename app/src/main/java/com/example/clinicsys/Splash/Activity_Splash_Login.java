package com.example.clinicsys.Splash;

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
import com.example.clinicsys.Appointment.pending.HomePending;
import com.example.clinicsys.MainActivity;
import com.example.clinicsys.R;
import com.example.clinicsys.SignUp;
import com.vishnusivadas.advanced_httpurlconnection.FetchData;
import com.vishnusivadas.advanced_httpurlconnection.PutData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class Activity_Splash_Login extends AppCompatActivity implements AdapterView.OnItemSelectedListener  {

    Button BtnLogin;
    EditText EdtloginIdno,EdtloginPassword;
//    public static final  String BASE_URL = "http://172.26.153.173";
    public static final  String BASE_URL = "http://192.168.1.12";
    private int prevCount = 0;
    String blankMessage = "Please fill this blank";
    private boolean isAtSpaceDelimiter(int currCount) {
        return currCount == 3;
    }

    ArrayList<String> patientType = new ArrayList<>();
    ArrayAdapter<String> patientAdapter;
    RequestQueue requestQueue;
    TextView newPatient;
    String user_id,id_number,message,type,role,fname,lname, image;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_login);
        requestQueue = Volley.newRequestQueue(this);

        EdtloginIdno = findViewById(R.id.loginIdno);
        EdtloginPassword = findViewById(R.id.loginPassword);
        BtnLogin = (Button) findViewById(R.id.btnAccess);
        newPatient = (TextView)findViewById(R.id.tv_forgotPassword);

        newPatient.setPaintFlags(newPatient.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);
        DataAuthentication();
//        fetchToken();


        BtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                String id_no, password;
                id_no = String.valueOf(EdtloginIdno.getText());
                password = String.valueOf(EdtloginPassword.getText());

                if(!id_no.equals("") && !password.equals("")){
                    //Start ProgressBar first (Set visibility VISIBLE)
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
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
                                                id_number = jsonObject2.optString("id_no");
                                                fname = jsonObject2.optString("first_name");
                                                lname = jsonObject2.optString("last_name");
                                                user_id = jsonObject2.optString("user_id");
                                                role = jsonObject2.optString("role_name");
                                                image = jsonObject2.optString("image");

                                                SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref",MODE_PRIVATE);
                                                SharedPreferences.Editor myEdit = sharedPreferences.edit();

                                                myEdit.putString("idNo", id_number);
                                                myEdit.putString("firstName", fname);
                                                myEdit.putString("lastName", lname);
                                                myEdit.putString("userId", user_id);
                                                myEdit.putString("roleName", role);
                                                myEdit.putString("imageUrl", image);
                                                myEdit.commit();

                                            if (type.matches("success")){
    //                                            Toast.makeText(getApplicationContext(), "test " + id, Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                                startActivity(intent);
                                                finish();
                                            }
                                            else{
                                                Toast.makeText(getApplicationContext(), "User/Password is wrong!", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                        catch (JSONException e)
                                        {
                                            Toast.makeText(getApplicationContext(), "ID no. is not yet approved"  , Toast.LENGTH_SHORT).show();
                                        }

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


        newPatient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SignUp.class);
                startActivity(intent);
//                finish();

            }
        });
    }

public void fetchToken(){
    FetchData fetchData = new FetchData("http://192.168.1.3/csu_clinic_app/refreshtokens");
    if (fetchData.startFetch()) {
        if (fetchData.onComplete()) {
            String fetchResult = fetchData.getResult();
            Toast.makeText(getApplicationContext(), "fetchToken " + fetchResult, Toast.LENGTH_SHORT).show();
//
            Log.i("fetchData", fetchResult);
        }}
}




    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

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
        EdtloginIdno.setText(sb.toString());
        EdtloginIdno.setSelection(sb.length());
    }
    public void DataAuthentication(){
        EdtloginIdno.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if(s.toString().length() != 9){
                    EdtloginIdno.setError("Not enough length");
                }
                else{
                    EdtloginIdno.setError(null);
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
                prevCount = EdtloginIdno.getText().toString().length();


            }
        });
    }
}