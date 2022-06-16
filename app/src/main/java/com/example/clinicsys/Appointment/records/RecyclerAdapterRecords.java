package com.example.clinicsys.Appointment.records;

import static com.example.clinicsys.MainActivity.admin;
import static com.example.clinicsys.Splash.Activity_Splash_Login.BASE_URL;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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

import java.text.BreakIterator;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class RecyclerAdapterRecords extends RecyclerView.Adapter<RecyclerAdapterRecords.MyViewHolderPending> implements AdapterView.OnItemSelectedListener {

    private Context mContext;
    private List<AppointmentRecords> appointments = new ArrayList<>();
    private Button btnStatusRecords;
    String message,type;
    ArrayList<String> complaintList = new ArrayList<>();
    ArrayList<String> patientType = new ArrayList<>();
    ArrayList<JSONObject> categories = new ArrayList<>();
    ArrayList<JSONObject> sub_categories = new ArrayList<>();
    ArrayAdapter<String> patientAdapter;
    ArrayAdapter<String> complaintAdapter;
    RequestQueue requestQueue;
    String selectedCat, selectSubCat;
    Spinner EdtSpnAppointmentCat;
    Spinner EditSpnComplaints;
    EditText scheduleEdit,complaints;
    int finalId;
    String ChangeCategoryId,ChangesubCategoryId,schedule,complaint;

    public RecyclerAdapterRecords(Context context, List<AppointmentRecords> appointments){
        this.mContext = context;
        this.appointments = appointments;
        requestQueue = Volley.newRequestQueue(mContext);
    }

    public class MyViewHolderPending extends RecyclerView.ViewHolder {

        private Button btnStatusRecords;
        private TextView aptCategory, aptSubCat, aptDate,aptName ;
        private LinearLayout mContainer;

        public MyViewHolderPending(View view){
            super(view);
            aptCategory = view.findViewById(R.id.appointment_title);
            aptSubCat = view.findViewById(R.id.appointment_subCat);
            aptName = view.findViewById(R.id.patient_name);
            aptDate = view.findViewById(R.id.appointment_date);
            mContainer = view.findViewById(R.id.appointment_container);
            btnStatusRecords = view.findViewById(R.id.btnStatusRecords);

        }
    }

    @NonNull
    @Override
    public MyViewHolderPending onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.appointment_layout_records,parent,false);
        return new MyViewHolderPending(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolderPending holder, @SuppressLint("RecyclerView") int position) {

        final AppointmentRecords appointment = appointments.get(position);
        int id = 0;
        int userId =0;
        id = Integer.parseInt(appointment.getIdd());
        userId = Integer.parseInt(appointment.getUserId());

        finalId = id;
        int finalUserId = userId;

        holder.aptCategory.setText(appointment.getCategory());
        holder.aptSubCat.setText(appointment.getSub_cat());
        String schedule = appointment.getSchedule();
        holder.aptDate.setText(schedule);
        holder.aptName.setText(appointment.getPatientName());


        if (appointment.getStatus().matches("2")){
            holder.btnStatusRecords.setText("Completed");
            holder.btnStatusRecords.setBackgroundColor(Color.parseColor("#32993d"));
        }
        else if (appointment.getStatus().matches("3")){
            holder.btnStatusRecords.setText("Cancelled");
            holder.btnStatusRecords.setBackgroundColor(Color.RED);
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

                                                    Intent in = new Intent(mContext.getApplicationContext(), HomeRecords.class);
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
