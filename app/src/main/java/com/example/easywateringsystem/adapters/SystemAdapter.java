package com.example.easywateringsystem.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.easywateringsystem.R;
import com.example.easywateringsystem.models.Systems;


import java.util.ArrayList;

public class SystemAdapter extends ArrayAdapter<Systems> {

    public SystemAdapter(Activity context, ArrayList<Systems> systems) {

        super(context, 0, systems);

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }

        Systems currentSystem = getItem(position);

        TextView tvName = convertView.findViewById(R.id.list_text_2);
        tvName.setText(currentSystem.getAddress()  );

        return convertView;
    }
}
