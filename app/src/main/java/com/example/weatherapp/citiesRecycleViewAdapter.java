package com.example.weatherapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class citiesRecycleViewAdapter extends RecyclerView.Adapter<citiesRecycleViewAdapter.MyViewHolder> {

    Context context;
    ArrayList<cityModel> models;

    public citiesRecycleViewAdapter(Context context, ArrayList<cityModel> models) {
        this.context = context;
        this.models = models;
    }

    @NonNull
    @Override
    public citiesRecycleViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.city_layout, parent, false);
        return new citiesRecycleViewAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull citiesRecycleViewAdapter.MyViewHolder holder, int position) {
        holder.cityView.setText(models.get(position).getCity());
        holder.degreesView.setText(models.get(position).getWeather());
        holder.weatherView.setImageResource(models.get(position).getImage());
    }

    @Override
    public int getItemCount() {
        return models.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView cityView, degreesView;
        ImageView weatherView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            cityView = itemView.findViewById(R.id.place);
            degreesView = itemView.findViewById(R.id.degrees);
            weatherView = itemView.findViewById(R.id.weather);
        }
    }
}
