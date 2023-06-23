package com.example.tutron;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ComplaintAdapter extends RecyclerView.Adapter<ComplaintAdapter.ComplaintViewHolder> {
    private ArrayList<Complaint> complaintList;

    public static class ComplaintViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewTutorName;
        public TextView textViewDescription;

        public ComplaintViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTutorName = itemView.findViewById(R.id.textViewTutorName);
            textViewDescription = itemView.findViewById(R.id.textViewComplaintDescription);
        }
    }

    public ComplaintAdapter(ArrayList<Complaint> complaintList) {
        this.complaintList = complaintList;
    }

    @NonNull
    @Override
    public ComplaintViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.complaint_item, parent, false);
        return new ComplaintViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ComplaintViewHolder holder, int position) {
        Complaint currentComplaint = complaintList.get(position);
        holder.textViewTutorName.setText(currentComplaint.getTutorName());
        holder.textViewDescription.setText(currentComplaint.getDescription());
    }

    @Override
    public int getItemCount() {
        return complaintList.size();
    }
}
