package com.tlatla.extimer;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.telecom.StatusHints;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class Adapter extends RecyclerView.Adapter<Adapter.Holder> {
    private ArrayList<String> list;
    private Context context;
    private OnItemClickListener listener;

    public class Holder extends RecyclerView.ViewHolder{
        TextView textView;

        public Holder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.list_textView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   int position = getAdapterPosition();
                   if(listener != null){
                       listener.onItemClick(Holder.this, v, position);
                   }
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int position = getAdapterPosition();
                    if (listener != null) {
                        listener.onLongItemClick(Holder.this, v, position);
                    }
                    return true;
                }
            });
        }
    }

    Adapter(ArrayList<String> list, Context context){
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater =(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        holder.textView.setText(list.get(position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }
    public void onItemClick(Holder holder, View view, int position){
        if (listener != null){
            listener.onItemClick(holder,view,position);
        }
    }
    public String getItem(int position){
        return list.get(position);
    }
}
