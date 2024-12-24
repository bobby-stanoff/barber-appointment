package vn.something.barberfinal.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import vn.something.barberfinal.DataModel.Appointment;
import vn.something.barberfinal.R;

public class CardAdapterBooking extends RecyclerView.Adapter<CardAdapterBooking.CardViewHolder> {

    private List<Appointment> dataList;
    private List<Appointment> rawDataList;
    OnItemClickListener onItemClickListener;

    public CardAdapterBooking(List<Appointment> rawdataList, OnItemClickListener clickedListener) {
        this.rawDataList = rawdataList;
        this.dataList = new ArrayList<>();
        this.dataList.addAll(rawdataList);
        this.onItemClickListener = clickedListener;
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_appointment, parent, false);
        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        Appointment apdata = dataList.get(position);
        holder.cardTextView.setText(apdata.getCustomerName());
        holder.appointment_time.setText(apdata.getDate()+" - "+apdata.getTime());
        holder.appointment_status.setText(apdata.getStatus());
        holder.appointment_id.setText("# "+apdata.getShortId());
        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(position);
            }
        });
        holder.accept_button.setOnClickListener(event -> {
            onItemClickListener.onAcceptClick(position);
        });

        holder.decline_button.setOnClickListener(event -> {
            onItemClickListener.onDeclineClick(position);
        });
        switch (apdata.getStatus()) {
            case "PENDING":
                holder.accept_button.setText("Chấp nhận");
                holder.decline_button.setText("Xóa");
                break;
            case "UPCOMING":
                holder.accept_button.setText("Hoàn thành");
                holder.decline_button.setText("Hủy");
                break;
            case "FINISHED":
                holder.accept_button.setText("Đã hoàn thành");
                holder.decline_button.setText("Xóa");
                holder.accept_button.setEnabled(false);
                holder.accept_button.setAlpha(0.5f);
                break;
            case "CANCELLED":
                holder.accept_button.setText("Đã hủy lịch");
                holder.decline_button.setText("Xóa");
                holder.accept_button.setEnabled(false);
                holder.accept_button.setAlpha(0.5f);
                break;
            default:
                // Handle default case
                break;
        }
    }
    public void filter(String query) {
        dataList.clear();

        if (query.isEmpty()) {
            dataList.addAll(rawDataList);
        } else {
            for (Appointment booking : rawDataList) {
                Log.d("TAG", "filter: "+booking.getCustomerName());
                if (booking.getCustomerName().toLowerCase().contains(query.toLowerCase()) || booking.getStatus().toLowerCase().contains(query.toLowerCase())) {
                    dataList.add(booking);
                }
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
        void onDeclineClick(int position);
        void onAcceptClick(int position);
    }

    public static class CardViewHolder extends RecyclerView.ViewHolder {
        TextView cardTextView;
        TextView appointment_time,service_type,appointment_status, appointment_id;
        Button decline_button, accept_button;

        public CardViewHolder(View itemView) {
            super(itemView);
            cardTextView = itemView.findViewById(R.id.client_name);
            appointment_status = itemView.findViewById(R.id.appointment_status);
            service_type = itemView.findViewById(R.id.service_type);
            appointment_time = itemView.findViewById(R.id.appointment_time);
            decline_button = itemView.findViewById(R.id.decline_button);
            accept_button = itemView.findViewById(R.id.accept_button);
            appointment_id = itemView.findViewById(R.id.appointment_id);

        }
    }
}