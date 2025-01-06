package com.example.fridge;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class SuggestionNamesAdapter extends RecyclerView.Adapter<SuggestionNamesAdapter.ViewHolder> {
    private ArrayList<String> suggestionNames;
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(String fullName);
    }

    public SuggestionNamesAdapter(ArrayList<String> suggestionNames, OnItemClickListener listener) {
        this.suggestionNames = suggestionNames;
        this.onItemClickListener = listener;
    }

    public void updateData(ArrayList<String> newSuggestionNames) {
        suggestionNames.clear();
        suggestionNames.addAll(newSuggestionNames);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String fullName = suggestionNames.get(position);
        holder.textView.setText(fullName);

        holder.itemView.setOnClickListener(v -> onItemClickListener.onItemClick(fullName));
    }

    @Override
    public int getItemCount() {
        return suggestionNames.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(android.R.id.text1);
        }
    }

}
