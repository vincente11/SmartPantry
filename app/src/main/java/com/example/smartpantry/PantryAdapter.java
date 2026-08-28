package com.example.smartpantry;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PantryAdapter extends RecyclerView.Adapter<PantryAdapter.PantryViewHolder> {

    private Context context;
    private List<PantryItem> itemList;
    private DatabaseHelper dbHelper;

    public PantryAdapter(Context context, List<PantryItem> itemList, DatabaseHelper dbHelper) {
        this.context = context;
        this.itemList = itemList;
        this.dbHelper = dbHelper;
    }

    @NonNull
    @Override
    public PantryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_2, parent, false);
        return new PantryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PantryViewHolder holder, int position) {
        PantryItem item = itemList.get(position);
        holder.tvName.setText(item.getName());
        holder.tvDetails.setText("Qty: " + item.getQuantity() + " " + item.getUnit());
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    static class PantryViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvDetails;

        public PantryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(android.R.id.text1);
            tvDetails = itemView.findViewById(android.R.id.text2);
        }
    }
}