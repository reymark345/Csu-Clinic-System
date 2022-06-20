package com.example.clinicsys.Appointment.pending;

import static com.example.clinicsys.MainActivity.admin;
import static com.example.clinicsys.MainActivity.BASE_URL;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
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
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class RecyclerAdapterPending extends RecyclerView.Adapter<RecyclerAdapterPending.MyViewHolderPending> implements AdapterView.OnItemSelectedListener {

    private Context mContext;
    private List<AppointmentPending> appointments = new ArrayList<>();
    private Button btnDone,btnChange, btnCancel;
    String message,type;
    ArrayList<String> complaintList = new ArrayList<>();
    ArrayList<String> patientType = new ArrayList<>();
    ArrayList<JSONObject> categories = new ArrayList<>();
    ArrayList<JSONObject> sub_categories = new ArrayList<>();
    ArrayAdapter<String> patientAdapter;
    ArrayAdapter<String> complaintAdapter;
    RequestQueue requestQueue;
    String selectedCat, selectSubCat;
    Spinner EdtSpnAppointmentCat,EditSpnComplaints;
    EditText scheduleEdit,complaints,edtMedication;
    int finalId;
    String ChangeCategoryId,ChangesubCategoryId,schedule,complaint,catGlobal, sub_catGlobal;

    TextView txt_loading,txt_loadingChange;
    public RecyclerAdapterPending(Context context, List<AppointmentPending> appointments){
        this.mContext = context;
        this.appointments = appointments;
        requestQueue = Volley.newRequestQueue(mContext);
    }

    public class MyViewHolderPending extends RecyclerView.ViewHolder {

        private Button btnDone, btnChange, btnCancel;
        private TextView aptCategory, aptSubCat, aptDate,aptName ;
        private LinearLayout mContainer;

        public MyViewHolderPending(View view){
            super(view);
            aptCategory = view.findViewById(R.id.appointment_title);
            aptSubCat = view.findViewById(R.id.appointment_subCat);
            aptName = view.findViewById(R.id.patient_name);
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

        finalId = id;
        int finalUserId = userId;

        holder.btnDone.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Toast.makeText(mContext, "Appointment IDs" +  appointment.getIdd(), Toast.LENGTH_SHORT).show();
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

                                String userid = String.valueOf(finalUserId);
                                ApprovedAppointment(appointment.getIdd(), pDiaglog, userid);
                            }
                        }).show();

            }
        });

        holder.btnChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int changeId = Integer.parseInt(appointment.getIdd());
//                Toast.makeText(mContext, "Editt " + changeId  ,Toast.LENGTH_LONG).show();
//                holder.aptSubCat.setText(appointment.getSub_cat());
                CustomDialogEdit(changeId);
            }
        });
        holder.btnCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                int changeId = Integer.parseInt(appointment.getIdd());
                CustomDialogCancel(changeId);
            }
        });

        holder.aptCategory.setText(appointment.getCategory());
        holder.aptSubCat.setText(appointment.getSub_cat());
        String schedule = appointment.getSchedule();
        holder.aptDate.setText(schedule);
        holder.aptName.setText(appointment.getPatientName());
    }


    public void CustomDialogCancel(int id) {

        final Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_cancel);
        EditText noteField = dialog.findViewById(R.id.edtRemarksCancel);
        TextInputLayout tilRemarks = dialog.findViewById(R.id.til_remarks);

        txt_loading = dialog.findViewById(R.id.txt_loading);
        txt_loading.setVisibility(View.GONE);

        Button submitButtonCancel = dialog.findViewById(R.id.btn_cancel);
        submitButtonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (noteField.length()==0){
                    tilRemarks.setError("Please fill this blank");
                }
                else {

                    txt_loading.setVisibility(View.VISIBLE);
                    submitButtonCancel.setClickable(false);
                    String remarks = noteField.getText().toString();
                    String idd = String.valueOf(id);
                    CancelAppointment(idd, dialog, remarks);
                }
            }
        });
        dialog.show();
    }

    public void CustomDialogEdit(int id) {

        final Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.edit_appointment_dialog);

        EdtSpnAppointmentCat = dialog.findViewById(R.id.EdtSpnAppointmentCat);
        EditSpnComplaints = dialog.findViewById(R.id.EditSpnComplaints);
        scheduleEdit = dialog.findViewById(R.id.scheduleEdit);
        edtMedication = dialog.findViewById(R.id.medicationEdit);
        txt_loadingChange = dialog.findViewById(R.id.txt_loadingChange);
        txt_loadingChange.setVisibility(View.GONE);
        complaints = dialog.findViewById(R.id.editComplaints);
        TextInputLayout tilComplaints = dialog.findViewById(R.id.til_complaints);
        TextInputLayout tilMedication = dialog.findViewById(R.id.til_medication);


        if (admin==true) {
            complaints.setEnabled(false);
        }
        else {
            tilMedication.setVisibility(View.GONE);
            edtMedication.setVisibility(View.GONE);

        }
        Button submitButtonChange = dialog.findViewById(R.id.EditSubmit_button);

        scheduleEdit.setFocusable(false);
        scheduleEdit.setClickable(true);

        Category(EdtSpnAppointmentCat,id);

        scheduleEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateTimeDialog(scheduleEdit);
            }
        });

        submitButtonChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    txt_loadingChange.setVisibility(View.VISIBLE);
                    submitButtonChange.setClickable(false);
                    String complaint = complaints.getText().toString();
                    String idd = String.valueOf(id);

                new android.os.Handler(Looper.getMainLooper()).postDelayed(
                        new Runnable() {
                            public void run() {
                                ChangeAppointment(idd, complaint);
                            }
                        },
                        500);
            }
        });
        dialog.show();
    }

    public void Category(Spinner aptCat, int idd){
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
                                patientAdapter = new ArrayAdapter<>(mContext,
                                        android.R.layout.simple_spinner_item, patientType);
                                patientAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                aptCat.setAdapter(patientAdapter);
                            }
                            new android.os.Handler(Looper.getMainLooper()).postDelayed(
                                    new Runnable() {
                                        public void run() {
                                            CategoryReplace(EdtSpnAppointmentCat,EditSpnComplaints, idd);
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
        aptCat.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if(adapterView.getId() == R.id.EdtSpnAppointmentCat){
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
                                    String subName = object.optString("name");

                                    sub_categories.add(object);
                                    complaintList.add(subName);
                                    complaintAdapter = new ArrayAdapter<>(mContext,
                                            android.R.layout.simple_spinner_item, complaintList);
                                    complaintAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                    EditSpnComplaints.setAdapter(complaintAdapter);
                                }
//                                SubCategoryReplace(EditSpnComplaints);
//                                Toast.makeText(mContext, "responsive" + response  ,Toast.LENGTH_LONG).show();

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
            EditSpnComplaints.setOnItemSelectedListener(this);
        }
        else if (adapterView.getId() == R.id.EditSpnComplaints){
//            Toast.makeText(mContext, "Diria oh",Toast.LENGTH_LONG).show();
            JSONObject sub_category_data = sub_categories.get(i);
            selectSubCat = sub_category_data.optString("id");
        }
        else{
            Toast.makeText(mContext, "Please contact administrator " + selectSubCat ,Toast.LENGTH_LONG).show();
        }
    }
    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public void CategoryReplace(Spinner apptCat,Spinner apptSub, int id){
        StringRequest stringRequest = new StringRequest(Request.Method.GET, BASE_URL+"/csu_clinic_app/api/appointment/get/"+id,
//        StringRequest stringRequest = new StringRequest(Request.Method.GET, BASE_URL+"/csu_clinic_app/api/category/list/2",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
//                            Toast.makeText(mContext, "ID ni  " + id ,Toast.LENGTH_LONG).show();
                            JSONObject object = new JSONObject(response);

                            ChangeCategoryId = object.getString("category_id");
                            ChangesubCategoryId = object.getString("sub_category_id");
                            catGlobal = object.getString("category");
                            sub_catGlobal = object.getString("sub_category");
                            schedule = object.getString("schedule");
                            complaint = object.getString("complaint");
                            if (!complaint.matches("null")) {
                                complaints.setText(complaint);
                            }
                            scheduleEdit.setText(schedule);
                            try {
                                patientAdapter = (ArrayAdapter) apptCat.getAdapter(); //cast to an ArrayAdapter
                                int spinnerPositionCat = patientAdapter.getPosition(catGlobal);
                                apptCat.setSelection(spinnerPositionCat);

                                new android.os.Handler(Looper.getMainLooper()).postDelayed(
                                        new Runnable() {
                                            public void run() {
                                                SubCategoryReplace(EditSpnComplaints);
                                            }
                                        },
                                        500);


                            }
                            catch (Exception e){
                                Toast.makeText(mContext, "Error  " + e ,Toast.LENGTH_LONG).show();
                            }

                        }catch (Exception e){
                            Toast.makeText(mContext, "error " + e ,Toast.LENGTH_LONG).show();

                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        requestQueue.add(stringRequest);
        apptCat.setOnItemSelectedListener(this);
        apptSub.setOnItemSelectedListener(this);
    }


    public void SubCategoryReplace(Spinner apptSub){
        try {
            new android.os.Handler(Looper.getMainLooper()).postDelayed(
                    new Runnable() {
                        public void run() {
                            complaintAdapter = (ArrayAdapter) apptSub.getAdapter(); //cast to an ArrayAdapter
                            int spinnerSubCat = complaintAdapter.getPosition(sub_catGlobal);
                            apptSub.setSelection(spinnerSubCat);
                        }
                    },
                    500);

        }
        catch (Exception e){
            Toast.makeText(mContext, "Error  " + e ,Toast.LENGTH_LONG).show();
        }
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
//                                Toast.makeText(mContext, "Sheeefd" + jsonArray, Toast.LENGTH_SHORT).show();
//                                message = jsonObject1.optString("message");
                                type = jsonObject1.optString("type");

                                if (type.equals("success")) {
//                                    btnCancel.setEnabled(true);
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
                            catch (Exception e)
                            {
                                Toast.makeText(mContext, ""+e, Toast.LENGTH_SHORT).show();
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

    public void ChangeAppointment(String id, String remarks){
        try {

            Toast.makeText(mContext, "ID Appointment " + id, Toast.LENGTH_SHORT).show();

            String sched = scheduleEdit.getText().toString();
            //Start ProgressBar first (Set visibility VISIBLE)
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    String[] field = new String[5];
                    field[0] = "id";
                    field[1] = "schedule";
                    field[2] = "category";
                    field[3] = "sub_category";
                    field[4] = "complaint";
                    //Creating array for data
                    String[] data = new String[5];
                    data[0] = id;
                    data[1] = sched;
                    data[2] = selectedCat;
                    data[3] = selectSubCat;
                    data[4] = remarks;
                    PutData putData = new PutData(BASE_URL+"/csu_clinic_app/api/appointment/pending/update", "POST", field, data);
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
                                            mContext, SweetAlertDialog.SUCCESS_TYPE);
                                    pDiaglog.setTitleText("Successfully Save");
                                    pDiaglog.setContentText("Appointment Change");
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

    public void ApprovedAppointment(String id, Dialog dialog, String userId){
        try {

            Toast.makeText(mContext, "Appointment ID" +  id, Toast.LENGTH_SHORT).show();

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

                new TimePickerDialog(mContext,timeSetListener,calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE),false).show();
            }
        };

        new DatePickerDialog(mContext,dateSetListener,calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH)).show();

    }

    @Override
    public int getItemCount() {
        return appointments.size();
    }
}
