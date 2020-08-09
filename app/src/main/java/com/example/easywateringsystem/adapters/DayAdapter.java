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
import com.example.easywateringsystem.models.Day;

import java.util.ArrayList;

;

public class DayAdapter extends ArrayAdapter<Day> {

    public DayAdapter(Activity context, ArrayList<Day> days) {

        super(context, 0, days);

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }

        Day currentDay = getItem(position);

        TextView dayTextView = convertView.findViewById(R.id.list_text_1);
        dayTextView.setText(currentDay.getDay());
        return convertView;
    }
}