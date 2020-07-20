package com.example.tovisit_ram_c0779370_android;

import android.content.Context;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class LocationAdapter extends ArrayAdapter {

    Context mContext;
    private List<FavModel> locations;
    private final int layoutresources;

    public LocationAdapter(@NonNull Context context, int resource, List<FavModel> locations) {
        super(context, resource, locations);
        this.mContext = context;
        this.locations = locations;
        this.layoutresources = resource;
    }

    @Override
    public int getCount() {
        return locations.size();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View v = layoutInflater.inflate(layoutresources,null);
        TextView addrs = v.findViewById(R.id.address);
        final FavModel favModel = locations.get(position);
        System.out.println("adaptor view"+ favModel.getAddress());
        return  v;


    }
}