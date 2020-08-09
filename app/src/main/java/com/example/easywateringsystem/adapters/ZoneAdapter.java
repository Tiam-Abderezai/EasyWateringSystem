package com.example.easywateringsystem.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.easywateringsystem.R;
import com.example.easywateringsystem.models.Zone;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ZoneAdapter extends ArrayAdapter<Zone> {

    public ZoneAdapter(Activity context, ArrayList<Zone> zones) {

        super(context, 0, zones);

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }

        Zone currentZone = getItem(position);

        TextView tvNumber = convertView.findViewById(R.id.list_text_1);
        TextView tvName = convertView.findViewById(R.id.list_text_2);
        ImageView ivImage = convertView.findViewById(R.id.list_image_1);


        tvNumber.setText(currentZone.getNumber());
        tvName.setText(currentZone.getName());

        if (!currentZone.getImage().equals("")) {
            Picasso.with(getContext())
                    .load(currentZone.getImage())
                    .fit()
                    .centerCrop()
                    .into(ivImage);
        } else {
            Picasso.with(getContext())
                    .load(R.drawable.splash_image)
                    .fit()
                    .centerCrop()
                    .into(ivImage);

        }



        return convertView;
    }



}
