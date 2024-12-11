package vn.something.barberfinal.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import vn.something.barberfinal.R;

public class CardAdapterService extends RecyclerView.Adapter<CardAdapterService.CardViewHolder> {

    private List<String> dataList;
    OnItemClickListener onItemClickListener;

    public CardAdapterService(List<String> dataList) {
        this.dataList = dataList;

    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.service_card, parent, false);
        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        holder.cardMainText.setText(dataList.get(position));
        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(position);
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
        TextView cardMainText, durationText, priceText;
        ImageView imgcontent;

        public CardViewHolder(View itemView) {
            super(itemView);
            cardMainText = itemView.findViewById(R.id.textViewMain);
            durationText = itemView.findViewById(R.id.textViewDuration);
            priceText = itemView.findViewById(R.id.textViewPrice);

            imgcontent = itemView.findViewById(R.id.image_content);

        }
    }
}