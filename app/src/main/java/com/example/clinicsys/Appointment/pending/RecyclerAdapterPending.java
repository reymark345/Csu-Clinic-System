package com.example.clinicsys.Appointment.pending;

import static com.example.clinicsys.MainActivity.admin;
import static com.example.clinicsys.Splash.Activity_Splash_Login.BASE_URL;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.clinicsys.MainActivity;
import com.example.clinicsys.R;
import com.example.clinicsys.SignUp;
import com.example.clinicsys.Splash.Activity_Splash_Login;
import com.google.android.material.textfield.TextInputLayout;
import com.vishnusivadas.advanced_httpurlconnection.PutData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class RecyclerAdapterPending extends RecyclerView.Adapter<RecyclerAdapterPending.MyViewHolderPending> {

    private Context mContext;
    private List<AppointmentPending> appointments = new ArrayList<>();
    private Button btnDone,btnChange, btnCancel;
    String message,type;

    public RecyclerAdapterPending(Context context, List<AppointmentPending> appointments){
        this.mContext = context;
        this.appointments = appointments;
    }

    public class MyViewHolderPending extends RecyclerView.ViewHolder {

        private TextView aptCategory, aptSubCat, aptDate;
        private LinearLayout mContainer;

        public MyViewHolderPending(View view){
            super(view);
            aptCategory = view.findViewById(R.id.appointment_title);
            aptSubCat = view.findViewById(R.id.appointment_subCat);
            aptDate = view.findViewById(R.id.appointment_date);
            mContainer = view.findViewById(R.id.appointment_container);
            btnDone = view.findViewById(R.id.btnDone);
            btnChange = view.findViewById(R.id.btnChange);
            btnCancel = view.findViewById(R.id.btnCancel);
            if (!admin==true) {
                btnDone.setVisibility(View.GONE);
            }
        }
    }
    public void test(){

    }

    @NonNull
    @Override
    public MyViewHolderPending onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.appointment_layout_pending,parent,false);
        return new MyViewHolderPending(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolderPending holder, @SuppressLint("RecyclerView") int position) {

        final AppointmentPending appointment = appointments.get(position);
        int id = 0;
        int userId =0;
        id = Integer.parseInt(appointment.getIdd());
        userId = Integer.parseInt(appointment.getUserId());

        int finalId = id;
        int finalUserId = userId;
        btnDone.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                final SweetAlertDialog pDiaglog = new SweetAlertDialog(
                        mContext, SweetAlertDialog.WARNING_TYPE);
                pDiaglog.setTitleText("Are you sure?");
                pDiaglog.setContentText("Approved appointment");
                pDiaglog.setConfirmText("Confirm");

                pDiaglog.setCancelable(true);
                pDiaglog.showCancelButton(true)

                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                Toast.makeText(mContext.getApplicationContext(), "Donee " + finalUserId,Toast.LENGTH_SHORT).show();
                                String idd = String.valueOf(finalId);
                                String userid = String.valueOf(finalUserId);
                                DoneAppointment(idd, pDiaglog, userid);
                            }
                        }).show();

            }
        });

        btnChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(mContext.getApplicationContext(), "Click button of Change " + finalId ,Toast.LENGTH_SHORT).show();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Toast.makeText(mContext.getApplicationContext(), "Click button of Change " + appointment.getIdd() ,Toast.LENGTH_SHORT).show();
                showCustomDialog(finalId);
            }
        });

        holder.aptCategory.setText(appointment.getCategory());
        holder.aptSubCat.setText(appointment.getSub_cat());
        String schedule = appointment.getSchedule();
        holder.aptDate.setText(schedule);
        }

    public void showCustomDialog(int id) {

        final Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_cancel);
//        spnAppointmentCat = dialog.findViewById(R.id.spnAppointmentCat);
//        spinnerComplaints = dialog.findViewById(R.id.spnComplaints);
        EditText noteField = dialog.findViewById(R.id.edtRemarksCancel);
        TextInputLayout tilRemarks = dialog.findViewById(R.id.til_remarks);
//        edtRemarks =  dialog.findViewById(R.id.edtRemarks);




        Button submitButtonCancel = dialog.findViewById(R.id.btn_cancel);
//        patientType(spnAppointmentCat);
//        edtSched.setFocusable(false);
//        edtSched.setClickable(true);

        submitButtonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (noteField.length()==0){
                    tilRemarks.setError("Please fill this blank");
                }
                else {

                    String remarks = noteField.getText().toString();

                    String idd = String.valueOf(id);
                    CancelAppointment(idd, dialog, remarks);


                }
            }
        });
        dialog.show();
    }

    public void CancelAppointment(String id, Dialog dialog, String remarks){
        try {
                //Start ProgressBar first (Set visibility VISIBLE)
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        String[] field = new String[2];
                        field[0] = "id";
                        field[1] = "remarks";
                        //Creating array for data
                        String[] data = new String[2];
                        data[0] = id;
                        data[1] = remarks;


                            PutData putData = new PutData(BASE_URL+"/csu_clinic_app/api/appointment/cancel", "POST", field, data);
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
                                        dialog.dismiss();
                                        final SweetAlertDialog pDiaglog = new SweetAlertDialog(
                                                mContext, SweetAlertDialog.SUCCESS_TYPE);
                                        pDiaglog.setTitleText("Successfully Save");
                                        pDiaglog.setContentText("Appointment Cancel");
                                        pDiaglog.setConfirmText("Ok");
                                        pDiaglog.setCancelable(false);
                                        pDiaglog.showCancelButton(false)
                                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                    @Override
                                                    public void onClick(SweetAlertDialog sDialog) {

                                                        Intent in = new Intent(mContext.getApplicationContext(), HomePending.class);
                                                        mContext.startActivity(in);
                                                        ((Activity) mContext).finish();
                                                        pDiaglog.dismiss();

                                                    }
                                                }).show();

                                    } else {
                                        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
                                    }
                                    //End ProgressBar (Set visibility to GONE)
                                    Log.i("PutData", result);
                                    }
                                    catch (JSONException e)
                                    {
                                        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
                                    }
                            }

                        }
                        //End Write and Read data with URL
                    }
                });
        }
        catch (Exception e){
            Toast.makeText(mContext, "Server Connection Failed", Toast.LENGTH_SHORT).show();
        }
    }

    public void DoneAppointment(String id, Dialog dialog, String userId){
        try {

            //Start ProgressBar first (Set visibility VISIBLE)
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    String[] field = new String[2];
                    field[0] = "id";
                    field[1] = "user_id";
                    //Creating array for data
                    String[] data = new String[2];
                    data[0] = id;
                    data[1] = userId;
                    PutData putData = new PutData(BASE_URL+"/csu_clinic_app/api/appointment/approve/"+id+"/"+userId, "POST", field, data);
//                        PutData putData = new PutData("http://192.168.254.109/csu_clinic/CancelApt.php", "POST", field, data);
                    if (putData.startPut()) {
                        if (putData.onComplete()) {
                            String result = putData.getResult();
                            try{
                                JSONArray jsonArray = new JSONArray(result);
                                JSONObject jsonObject1 = jsonArray.getJSONObject(0);
                                message = jsonObject1.optString("message");
                                type = jsonObject1.optString("type");
                                if (type.equals("success")) {
                                    dialog.dismiss();
                                    final SweetAlertDialog pDiaglog = new SweetAlertDialog(
                                            mContext, SweetAlertDialog.SUCCESS_TYPE);
                                    pDiaglog.setTitleText("Successfully Save");
                                    pDiaglog.setContentText("Appointment completed");
                                    pDiaglog.setConfirmText("Ok");
                                    pDiaglog.setCancelable(false);
                                    pDiaglog.showCancelButton(false)
                                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                @Override
                                                public void onClick(SweetAlertDialog sDialog) {

                                                    Intent in = new Intent(mContext.getApplicationContext(), HomePending.class);
                                                    mContext.startActivity(in);
                                                    ((Activity) mContext).finish();
                                                    pDiaglog.dismiss();

                                                }
                                            }).show();
                                } else {
                                    Toast.makeText(mContext, result, Toast.LENGTH_SHORT).show();
                                }
                                //End ProgressBar (Set visibility to GONE)
                                Log.i("PutData", result);

                            }catch (Exception e){

                            }


                        }
                    }
                    //End Write and Read data with URL
                }
            });
        }
        catch (Exception e){
            Toast.makeText(mContext, "Server Connection Failed", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public int getItemCount() {
        return appointments.size();
    }
}
