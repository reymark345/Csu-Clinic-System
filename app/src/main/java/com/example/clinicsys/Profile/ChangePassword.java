package com.example.clinicsys.Profile;

import static com.example.clinicsys.MainActivity.BASE_URL;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.clinicsys.Appointment.approved.HomeApproved;
import com.example.clinicsys.MainActivity;
import com.example.clinicsys.R;
import com.example.clinicsys.SignUp;
import com.google.android.material.textfield.TextInputLayout;
import com.vishnusivadas.advanced_httpurlconnection.PutData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class ChangePassword extends AppCompatActivity{

    EditText edtCurrent,edtNew,edtConfirm;
    Button btnUpdatePass;

    TextInputLayout tilCurrent,tilNew,tilConfirm;
    String blankMessage = "Please don't leave a blank";
    String current,newP,confirm, type,message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_password);

        edtCurrent = findViewById(R.id.edt_currentPassword);
        edtNew = findViewById(R.id.edt_newPassword);
        edtConfirm = findViewById(R.id.edt_confirmPass);
        btnUpdatePass = findViewById(R.id.updateChangePassword);

        tilCurrent = findViewById(R.id.cc_CurrentPass);
        tilNew = findViewById(R.id.cc_newPass);
        tilConfirm = findViewById(R.id.cc_confirmPassword);



        btnUpdatePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                current = edtCurrent.getText().toString();
                newP = edtNew.getText().toString();
                confirm = edtConfirm.getText().toString();
                if (!current.matches("") && !newP.matches("") && !confirm.matches("")){

                    String md5Current = getMdHash(current);
                    String md5new = getMdHash(newP);
                    SharedPreferences sh = getSharedPreferences("MySharedPref", MODE_APPEND);
                    String token = sh.getString("token", "");
                    String userId = sh.getString("userId", "");

                    if (md5Current.matches(token)){

                        if (newP.matches(confirm)){
                            if(!current.matches(newP)){
                                //id and password md5
                                ///users/update/change_pass
                                updatePassword(userId,md5new);

                            }
                            else{
                                new SweetAlertDialog(ChangePassword.this, SweetAlertDialog.ERROR_TYPE)
                                        .setTitleText("You entered an Old Password")
                                        .setContentText("Invalid")
                                        .showCancelButton(true)
                                        .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                            @Override
                                            public void onClick(SweetAlertDialog sDialog) {
                                            }
                                        }).show();
                            }

                        }
                        else{
                            new SweetAlertDialog(ChangePassword.this, SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("Mismatched Password!")
                                    .setContentText("Password not match")
                                    .showCancelButton(true)
                                    .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sDialog) {
                                        }
                                    }).show();
                        }
                    }
                    else {
                        new SweetAlertDialog(ChangePassword.this, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Wrong Password!")
                                .setContentText("Incorrect Old Password")
                                .showCancelButton(true)
                                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog) {
                                    }
                                }).show();

                    }
                }
                else {
                    new SweetAlertDialog(ChangePassword.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Invalid")
                            .setContentText("Please complete the details")
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



    public static String getMdHash(String s) {
        final String MD5 = "MD5";
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest
                    .getInstance(MD5);
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    public void updatePassword(String id, String token){
//        Toast.makeText(ChangePassword.this,"base url  " + BASE_URL, Toast.LENGTH_SHORT).show();
//        Toast.makeText(ChangePassword.this,"test " + id +" "+ token, Toast.LENGTH_SHORT).show();
        try {
            //Start ProgressBar first (Set visibility VISIBLE)
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    String[] field = new String[2];
                    field[0] = "id";
                    field[1] = "password";
                    //Creating array for data
                    String[] data = new String[2];
                    data[0] = id;
                    data[1] = token;

                    //id and password md5
                    ///users/update/change_pass

                    PutData putData = new PutData(BASE_URL+"/csu_clinic_app/api/users/update/change_pass", "POST", field, data);
//                        PutData putData = new PutData("http://192.168.254.109/csu_clinic/CancelApt.php", "POST", field, data);
                    if (putData.startPut()) {
                        if (putData.onComplete()) {
                            String result = putData.getResult();

                            try {
                                JSONArray jsonArray = new JSONArray(result);
                                JSONObject jsonObject1 = jsonArray.getJSONObject(0);
                                message = jsonObject1.optString("message");
                                type = jsonObject1.optString("type");
                                if (type.equals("success")) {
                                    final SweetAlertDialog pDiaglog = new SweetAlertDialog(
                                            ChangePassword.this, SweetAlertDialog.SUCCESS_TYPE);
                                    pDiaglog.setTitleText("Successfully Save");
                                    pDiaglog.setContentText("Password has changed");
                                    pDiaglog.setConfirmText("Ok");
                                    pDiaglog.setCancelable(false);
                                    pDiaglog.showCancelButton(false)
                                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                @Override
                                                public void onClick(SweetAlertDialog sDialog) {
                                                    pDiaglog.dismiss();
                                                    edtCurrent.setText("");
                                                    edtNew.setText("");
                                                    edtConfirm.setText("");
                                                }
                                            }).show();

                                } else {
                                    Toast.makeText(ChangePassword.this, message, Toast.LENGTH_SHORT).show();
                                }
                                //End ProgressBar (Set visibility to GONE)
                                Log.i("PutData", result);
                            }
                            catch (JSONException e)
                            {
                                Toast.makeText(ChangePassword.this, "error ni "+e, Toast.LENGTH_SHORT).show();
                            }
                        }

                    }
                    //End Write and Read data with URL
                }
            });
        }
        catch (Exception e){
            Toast.makeText(ChangePassword.this, "Server Connection Failed", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onBackPressed() {

        Intent intent = new Intent(getApplicationContext(), EditProfile.class);
        startActivity(intent);
        finish();
    }
}