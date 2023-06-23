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
    private OnItemClickListener mListener;

    // Define OnItemClickListener interface
    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    // Initialize on item click listener
    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public static class ComplaintViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewTutorName;
        public TextView textViewDescription;

        public ComplaintViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);
            textViewTutorName = itemView.findViewById(R.id.textViewTutorName);
            textViewDescription = itemView.findViewById(R.id.textViewComplaintDescription);

            // Set on click listener on itemView (i.e., individual RV element)
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        // Ensure position is valid
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }

    public ComplaintAdapter(ArrayList<Complaint> complaintList) {
        this.complaintList = complaintList;
    }

    @NonNull
    @Override
    public ComplaintViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.complaint_item, parent, false);
        return new ComplaintViewHolder(v, mListener);
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
