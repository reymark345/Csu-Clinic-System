package com.example.clinicsys.Appointment.pending;

import static com.example.clinicsys.Appointment.pending.HomeActivity.admin;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.clinicsys.R;

import java.util.ArrayList;
import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyViewHolder> {

    private Context mContext;
    private List<Appointment> appointments = new ArrayList<>();
    private Button btnDone,btnChange, btnCancel;

    public RecyclerAdapter(Context context, List<Appointment> appointments){
        this.mContext = context;
        this.appointments = appointments;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView aptTitle, aptTime;
        private LinearLayout mContainer;

        public MyViewHolder (View view){
            super(view);
            aptTitle = view.findViewById(R.id.appointment_title);
            aptTime = view.findViewById(R.id.appointment_time);
            mContainer = view.findViewById(R.id.appointment_container);
            btnDone = view.findViewById(R.id.btnDone);
            btnChange = view.findViewById(R.id.btnChange);
            btnCancel = view.findViewById(R.id.btnCancel);
            if (admin==true) {
                btnDone.setVisibility(View.GONE);
            }
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.products_list_item_layout,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {

        final Appointment appointment = appointments.get(position);
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
                Toast.makeText(mContext.getApplicationContext(), "Click button of Cancel " + id ,Toast.LENGTH_SHORT).show();
            }
        });

        holder.aptTime.setText("Ksh "+appointment.getPrice());
        holder.aptTitle.setText(appointment.getTitle());
        holder.mContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext.getApplicationContext(), "The ID is " + id ,Toast.LENGTH_SHORT).show();
            }
        });
    }

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

    @Override
    public int getItemCount() {
        return appointments.size();
    }
}
