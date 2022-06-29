package com.example.clinicsys.Appointment.approved;

import static com.example.clinicsys.MainActivity.admin;
import static com.example.clinicsys.MainActivity.BASE_URL;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.nfc.Tag;
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
import com.example.clinicsys.R;
import com.google.android.material.textfield.TextInputLayout;
import com.vishnusivadas.advanced_httpurlconnection.PutData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import br.com.simplepass.loadingbutton.customViews.CircularProgressButton;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class RecyclerAdapterApproved extends RecyclerView.Adapter<RecyclerAdapterApproved.MyViewHolderApproved> implements AdapterView.OnItemSelectedListener {

    private static final String TAG ="" ;
    private Context mContext;
    private List<AppointmentApproved> appointments = new ArrayList<>();
    private Button btnDone,btnChange, btnCancel,submitButtonChange ;
    String message,type;
    ArrayList<String> complaintList = new ArrayList<>();
    ArrayList<String> categoryType = new ArrayList<>();
    ArrayList<JSONObject> categories = new ArrayList<>();
    ArrayList<JSONObject> sub_categories = new ArrayList<>();
    ArrayAdapter<String> categoryAdapter;
    ArrayAdapter<String> complaintAdapter;
    ArrayAdapter<String>medicineAdapter;
    RequestQueue requestQueue;
    String selectedCat, selectSubCat;
    Spinner EdtSpnAppointmentCat;
    Spinner EditSpnComplaints;
    EditText scheduleEdit,complaints, edtMedication;
    int finalId;
    JSONArray ApptMedications;
    String ChangeCategoryId,ChangesubCategoryId,schedule,complaint,catGlobal, sub_catGlobal;
    boolean[] selectedMedication;
    ArrayList<Integer> medicationList = new ArrayList<Integer>();
    ArrayList<Integer> medicationUpdate = new ArrayList<Integer>();
    ArrayList<JSONObject> medication = new ArrayList<>();
    ArrayList<String> medicationType = new ArrayList<>();
    ArrayAdapter<String> medicationAdapter;


//    String[] medicationArray =  {"Amoxicillin","Paracetamol","Sympex", "Zilgam","Ceterezine"};
    ArrayList<String> medicationArray = new ArrayList<>();
    ArrayList<JSONObject> medicationData = new ArrayList<>();
    TextView txt_loading,txt_loadingChange;




    public RecyclerAdapterApproved(Context context, List<AppointmentApproved> appointments){
        this.mContext = context;
        this.appointments = appointments;
        requestQueue = Volley.newRequestQueue(mContext);
        MedicationList();
    }

    public class MyViewHolderApproved extends RecyclerView.ViewHolder {

        private Button btnDone, btnChange, btnCancel;
        private TextView aptCategory, aptSubCat, aptDate, aptName;
        private LinearLayout mContainer;

        public MyViewHolderApproved(View view){
            super(view);
            aptCategory = view.findViewById(R.id.appointment_title);
            aptSubCat = view.findViewById(R.id.appointment_subCat);
            aptName = view.findViewById(R.id.ApprovedPatient_name);
            aptDate = view.findViewById(R.id.appointment_date);
            mContainer = view.findViewById(R.id.appointment_container);
            btnDone = view.findViewById(R.id.btnDone);
            btnChange = view.findViewById(R.id.btnChange);
            btnCancel = view.findViewById(R.id.btnCancel);



            if (!admin==true) {
                btnDone.setVisibility(View.GONE);
                btnChange.setVisibility(View.GONE);
            }
        }
    }

    @NonNull
    @Override
    public MyViewHolderApproved onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.appointment_layout_approved,parent,false);
        return new MyViewHolderApproved(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolderApproved holder, @SuppressLint("RecyclerView") int position) {

        final AppointmentApproved appointment = appointments.get(position);
        int id = 0;
        int userId =0;
        id = Integer.parseInt(appointment.getIdd());
        userId = Integer.parseInt(appointment.getUserId());

        finalId = id;
        int finalUserId = userId;

        holder.btnDone.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                final SweetAlertDialog pDiaglog = new SweetAlertDialog(
                        mContext, SweetAlertDialog.WARNING_TYPE);
                pDiaglog.setTitleText("Are you sure?");
                pDiaglog.setContentText("Complete appointment");
                pDiaglog.setConfirmText("Confirm");

                pDiaglog.setCancelable(true);
                pDiaglog.showCancelButton(true)

                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {

                                String userid = String.valueOf(finalUserId);
                                CompleteAppointment(appointment.getIdd(), pDiaglog, userid);
                            }
                        }).show();

            }
        });

        holder.btnChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int changeId = Integer.parseInt(appointment.getIdd());
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

        Button submitButtonCancel = (Button) dialog.findViewById(R.id.btn_cancel);
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
        selectedMedication = new boolean[medicationArray.size()];
        txt_loadingChange = dialog.findViewById(R.id.txt_loadingChange);
        txt_loadingChange.setVisibility(View.GONE);
        complaints = dialog.findViewById(R.id.editComplaints);
        TextInputLayout tilComplaints = dialog.findViewById(R.id.til_complaints);
        submitButtonChange = dialog.findViewById(R.id.EditSubmit_button);


        scheduleEdit.setFocusable(false);
        scheduleEdit.setClickable(true);
        edtMedication.setFocusable(false);
        edtMedication.setClickable(true);

        Category(EdtSpnAppointmentCat,id);

        scheduleEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateTimeDialog(scheduleEdit);
            }
        });
//        edtMedication.setText("");
        edtMedication.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("Select medication");
                builder.setCancelable(false);
                String[] arr = new String[medicationArray.size()];
                medicationList.clear();
                for(int i=0 ; i< medicationArray.size();i++){
                    arr[i] = medicationArray.get(i);
                    selectedMedication[i] = false;
                    //getProductName or any suitable method
                    for (int j=0 ; j<ApptMedications.length(); j++) {
                        if (ApptMedications.optString(j).equals(medicationArray.get(i))) {
                            selectedMedication[i] = true;
                            medicationList.add(i);
                        }
                    }
                }

                builder.setMultiChoiceItems(arr, selectedMedication, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i, boolean b) {
//                        for(int i=0 ; i< medicationArray.size();i++){
//                            arr[i] = medicationArray.get(i);
//                            selectedMedication[i] = false;
//                            //getProductName or any suitable method
//                            for (int j=0 ; j<ApptMedications.length(); j++) {
//                                if (ApptMedications.optString(j).equals(medicationArray.get(i))) {
//                                    selectedMedication[i] = true;
//                                    medicationList.add(i);
//                                }
//                            }
//                        }
                        if (b) {
                            medicationList.add(i);
                            ApptMedications.put(medicationArray.get(i));
                            Collections.sort(medicationList);
                        } else {
                            medicationList.remove(Integer.valueOf(i));
                            for (int x=0; x < ApptMedications.length(); x++) {
                                if (ApptMedications.optString(x).equals(medicationArray.get(i))) {
                                    ApptMedications.remove(x);
                                }
                            }
                        }
                    }
                });
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        StringBuilder stringBuilder = new StringBuilder();
                        for(int j=0; j<medicationList.size();j++){
                            stringBuilder.append(medicationArray.get(medicationList.get(j)));
                            if(j !=medicationList.size()-1){
                                stringBuilder.append(", ");
                            }
                        }
                        edtMedication.setText(stringBuilder.toString());
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                builder.setNeutralButton("Clear All", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        for(int j=0;j<selectedMedication.length;j++){
                            selectedMedication[j]=false;
                            medicationList.clear();
                            edtMedication.setText("");
                        }
                    }
                });
                builder.show();
            }
        });

        submitButtonChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txt_loadingChange.setVisibility(View.VISIBLE);
                submitButtonChange.setClickable(false);
                String complaint = complaints.getText().toString();
                String idd = String.valueOf(id);
                medicationUpdate.clear();
                for (int i=0; i<medicationList.size(); i++) {
                    for (int j=0; j<medicationData.size(); j++) {
                        if (medicationArray.get(medicationList.get(i)).equals(medicationData.get(j).optString("name"))) {
                            medicationUpdate.add(medicationData.get(j).optInt("id"));
                        }
                    }
                }

                ChangeAppointment(idd, complaint, dialog);
            }
        });
        dialog.show();
    }


    public void MedicationList(){
        StringRequest stringRequest = new StringRequest(Request.Method.GET, BASE_URL+"/csu_clinic_app/api/medicine/list/2",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray array = new JSONArray(response);
                            medicationArray.clear();
                            medicationData.clear();
                            for (int i = 0; i<array.length(); i++){
                                JSONObject object = array.getJSONObject(i);
                                String medicineId = object.optString("id");
                                String MedicineName = object.optString("name");
                                medicationArray.add(MedicineName);
                                medicationData.add(object);
                            }
                        }catch (Exception e){
                            Toast.makeText(mContext, "error medicine  " + e ,Toast.LENGTH_LONG).show();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        requestQueue.add(stringRequest);
    }

    public void Category(Spinner aptCat, int id){
        categories.clear();
        categoryType.clear();
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
                                categoryType.add(categoryName);
                                categoryAdapter = new ArrayAdapter<>(mContext,
                                        android.R.layout.simple_spinner_item, categoryType);
                                categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                aptCat.setAdapter(categoryAdapter);
                            }
                            new android.os.Handler(Looper.getMainLooper()).postDelayed(
                                    new Runnable() {
                                        public void run() {
                                            CategoryReplace(EdtSpnAppointmentCat,EditSpnComplaints, id);
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

    public void CategoryReplace(Spinner aptCat,Spinner aptComplaint, int id){
        StringRequest stringRequest = new StringRequest(Request.Method.GET, BASE_URL+"/csu_clinic_app/api/appointment/get/"+id,
//        StringRequest stringRequest = new StringRequest(Request.Method.GET, BASE_URL+"/csu_clinic_app/api/category/list/2",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {

                            JSONObject object = new JSONObject(response);
                            ChangeCategoryId = object.getString("category_id");
                            ChangesubCategoryId = object.getString("sub_category_id");
                            catGlobal= object.getString("category");
                            sub_catGlobal = object.getString("sub_category");
                            ApptMedications = object.getJSONArray("medications_name");
                            schedule = object.getString("schedule");
                            complaint = object.getString("remarks");
                            scheduleEdit.setText(schedule);
                            medicationUpdate.clear();
                            for (int j=0; j<object.getJSONArray("medications").length(); j++) {
                                    medicationUpdate.add((Integer) object.getJSONArray("medications").get(j));
                            }
                            if (!complaint.matches("null")){
                                complaints.setText(complaint);
                            }
                            try {
                                categoryAdapter = (ArrayAdapter) aptCat.getAdapter(); //cast to an ArrayAdapter
                                int spinnerPositionCat = categoryAdapter.getPosition(catGlobal);
                                aptCat.setSelection(spinnerPositionCat);
                                if (ApptMedications.length() > 0) {
                                    edtMedication.setText(ApptMedications.toString().replace("[", "").replace("]", "").replace("\"", "").replace(",", ", ").trim());
                                }

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
        aptCat.setOnItemSelectedListener(this);
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
            Toast.makeText(mContext, "Error SubCategoryReplace  " + e ,Toast.LENGTH_LONG).show();
        }
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
                                    String cityName = object.optString("name");
                                    sub_categories.add(object);
                                    complaintList.add(cityName);
                                    complaintAdapter = new ArrayAdapter<>(mContext,
                                            android.R.layout.simple_spinner_item, complaintList);
                                    complaintAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                    EditSpnComplaints.setAdapter(complaintAdapter);
                                }
                                SubCategoryReplace(EditSpnComplaints);

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

                                                    Intent in = new Intent(mContext.getApplicationContext(), HomeApproved.class);
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

    public void ChangeAppointment(String id, String remarks, Dialog dialog){
        try {

            Toast.makeText(mContext, "try "+remarks, Toast.LENGTH_SHORT).show();
            Log.i(TAG,"aPPOINTMENT TEST " + remarks);
            String sched = scheduleEdit.getText().toString();
            //Start ProgressBar first (Set visibility VISIBLE)
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    String[] field = new String[6];
                    field[0] = "id";
                    field[1] = "schedule";
                    field[2] = "category";
                    field[3] = "sub_category";
                    field[4] = "remarks";
                    field[5] = "medications";
                    //Creating array for data
                    String[] data = new String[6];
                    data[0] = id;
                    data[1] = sched;
                    data[2] = selectedCat;
                    data[3] = selectSubCat;
                    data[4] = remarks;
                    data[5] = medicationUpdate.toString();

                    PutData putData = new PutData(BASE_URL+"/csu_clinic_app/api/appointment/approved/update", "POST", field, data);
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
                                    pDiaglog.setContentText("Appointment Updated");
                                    pDiaglog.setConfirmText("Ok");
                                    pDiaglog.setCancelable(false);
                                    pDiaglog.showCancelButton(false)
                                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                @Override
                                                public void onClick(SweetAlertDialog sDialog) {
                                                    txt_loadingChange.setVisibility(View.GONE);
                                                    submitButtonChange.setClickable(true);
//                                                    Intent in = new Intent(mContext.getApplicationContext(), HomeApproved.class);
//                                                    mContext.startActivity(in);
//                                                    ((Activity) mContext).finish();
                                                    dialog.dismiss();
                                                    pDiaglog.dismiss();
                                                }
                                            }).show();
                                } else {
                                    txt_loadingChange.setVisibility(View.GONE);
                                    submitButtonChange.setClickable(true);
                                    final SweetAlertDialog pDiaglog = new SweetAlertDialog(
                                            mContext, SweetAlertDialog.ERROR_TYPE);
                                    pDiaglog.setTitleText("Error!");
                                    pDiaglog.setContentText(message);
                                    pDiaglog.setConfirmText("Ok");
                                    pDiaglog.setCancelable(false);
                                    pDiaglog.showCancelButton(false)
                                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                @Override
                                                public void onClick(SweetAlertDialog sDialog) {
                                                    pDiaglog.dismiss();
                                                }
                                            }).show();
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

    public void CompleteAppointment(String id, Dialog dialog, String userId){
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
                    PutData putData = new PutData(BASE_URL+"/csu_clinic_app/api/appointment/complete/"+id+"/"+userId, "POST", field, data);
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
                                                    Intent in = new Intent(mContext.getApplicationContext(), HomeApproved.class);
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
                                Toast.makeText(mContext, result, Toast.LENGTH_SHORT).show();
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
