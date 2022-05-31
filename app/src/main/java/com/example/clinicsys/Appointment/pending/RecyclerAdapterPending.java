package com.example.clinicsys.Appointment.pending;

import static com.example.clinicsys.Appointment.pending.HomePending.admin;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
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

import com.example.clinicsys.R;
import com.example.clinicsys.Splash.Activity_Splash_Login;
import com.google.android.material.textfield.TextInputLayout;
import com.vishnusivadas.advanced_httpurlconnection.PutData;

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
            if (admin==true) {
//                btnDone.setVisibility(View.GONE);
                btnChange.setVisibility(View.GONE);
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
        int id = position+1;

        btnDone.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Toast.makeText(mContext.getApplicationContext(), "Click button of Done " + id ,Toast.LENGTH_SHORT).show();
            }
        });

        btnChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(mContext.getApplicationContext(), "Click button of Change " + id ,Toast.LENGTH_SHORT).show();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                showCustomDialog(id);
            }
        });


        holder.aptCategory.setText(appointment.getCategory());
        holder.aptSubCat.setText(appointment.getSub_cat());



        String schedule = appointment.getSchedule();

        String outputPattern = "MMM dd, yyyy h:mm a";

        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime dateTime = LocalDateTime.parse(schedule, inputFormatter);
        String output = DateTimeFormatter.ofPattern(outputPattern).format(dateTime);

        holder.aptDate.setText(output);




//        StringTokenizer tk = new StringTokenizer(schedule);
//
//        String date = tk.nextToken();
//        String tme = tk.nextToken();







//        holder.mContainer.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(mContext.getApplicationContext(), "The ID is " + id ,Toast.LENGTH_SHORT).show();
//            }
//        });


    //        holder.mRate.setRating(product.getRating());
    //        Glide.with(mContext).load(product.getImage()).into(holder.mImageView);

    //                Intent intent = new Intent(mContext,DetailedProductsActivity.class);
//
//                intent.putExtra("title",product.getTitle());
////                intent.putExtra("image",product.getImage());
////                intent.putExtra("rate",product.getRating());
//                intent.putExtra("price",product.getPrice());
//
//                mContext.startActivity(intent);

        }

    public void showCustomDialog(int id) {

        final Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_cancel);
//        spnAppointmentCat = dialog.findViewById(R.id.spnAppointmentCat);
//        spinnerComplaints = dialog.findViewById(R.id.spnComplaints);
        EditText remarks = dialog.findViewById(R.id.edtRemarksCancel);
        TextInputLayout tilRemarks = dialog.findViewById(R.id.til_remarks);
//        edtRemarks =  dialog.findViewById(R.id.edtRemarks);


        Button submitButtonCancel = dialog.findViewById(R.id.btn_cancel);
//        patientType(spnAppointmentCat);
//        edtSched.setFocusable(false);
//        edtSched.setClickable(true);

        submitButtonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (remarks.length()==0){
                    tilRemarks.setError("Please fill this blank");
                }
                else {

                    String idd = String.valueOf(id);
                    CancelAppointment(idd, dialog);


                }
            }
        });
        dialog.show();
    }
    public void CancelAppointment(String id, Dialog dialog){
        try {
                //Start ProgressBar first (Set visibility VISIBLE)
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        String[] field = new String[1];
                        field[0] = "id";
                        //Creating array for data
                        String[] data = new String[1];
                        data[0] = id;

                            PutData putData = new PutData("http://172.31.250.43/csu_clinic/CancelApt.php", "POST", field, data);
//                        PutData putData = new PutData("http://192.168.254.109/csu_clinic/CancelApt.php", "POST", field, data);
                        if (putData.startPut()) {
                            if (putData.onComplete()) {
                                String result = putData.getResult();
                                if (result.equals("Created success")) {
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
//                                            .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
//                                                @Override
//                                                public void onClick(SweetAlertDialog sDialog) {
//                                                    Intent in = new Intent(getApplicationContext(), HomePending.class);
//                                                    startActivity(in);
//                                                    finish();
//                                                }
//                                            }).show();

//
                                } else {
                                    Toast.makeText(mContext, result, Toast.LENGTH_SHORT).show();
                                }
                                //End ProgressBar (Set visibility to GONE)
                                Log.i("PutData", result);
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
