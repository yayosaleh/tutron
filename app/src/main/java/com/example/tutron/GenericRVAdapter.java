package com.example.tutron;

import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class GenericRVAdapter<T> extends RecyclerView.Adapter<GenericRVAdapter.GenericViewHolder> {
    private ArrayList<T> items;
    private OnItemClickListener mListener;
    private Binder<T> binder;
    private int layoutId;

    // Define OnItemClickListener interface
    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    // Define Binder interface
    public interface Binder<T> {
        void bindData(T item, GenericViewHolder holder);
    }

    // Initialize on item click listener
    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public static class GenericViewHolder extends RecyclerView.ViewHolder {
        private SparseArray<View> views;

        public GenericViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);
            views = new SparseArray<>();
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

        public View getViewById(int id) {
            View view = views.get(id);
            if (view == null) {
                view = itemView.findViewById(id);
                views.put(id, view);
            }
            return view;
        }
    }

    public GenericRVAdapter(ArrayList<T> items, int layoutId, Binder<T> binder) {
        this.items = items;
        this.layoutId = layoutId;
        this.binder = binder;
    }

    @NonNull
    @Override
    public GenericViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
        return new GenericViewHolder(v, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull GenericViewHolder holder, int position) {
        T currentItem = items.get(position);
        binder.bindData(currentItem, holder);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}