package vn.something.barberfinal.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import vn.something.barberfinal.R;

public class CardAdapterBooking extends RecyclerView.Adapter<CardAdapterBooking.CardViewHolder> {

    private List<String> dataList;
    OnItemClickListener onItemClickListener;

    public CardAdapterBooking(List<String> dataList, OnItemClickListener clickedListener) {
        this.dataList = dataList;
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
        holder.cardTextView.setText(dataList.get(position));
        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(position);  // Pass position to listener
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public static class CardViewHolder extends RecyclerView.ViewHolder {
        TextView cardTextView;

        public CardViewHolder(View itemView) {
            super(itemView);
            cardTextView = itemView.findViewById(R.id.client_name); // TextView inside CardView
        }
    }
}