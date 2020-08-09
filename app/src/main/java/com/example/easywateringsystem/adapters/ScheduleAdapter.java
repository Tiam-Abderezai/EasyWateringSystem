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
import com.example.easywateringsystem.models.Schedule;

import java.util.ArrayList;

public class ScheduleAdapter extends ArrayAdapter<Schedule> {

    public ScheduleAdapter(Activity context, ArrayList<Schedule> schedules) {
        super(context, 0, schedules);

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }

        Schedule currentSchedule = getItem(position);



        TextView startTV = convertView.findViewById(R.id.list_text_1);
        TextView finishTV = convertView.findViewById(R.id.list_text_2);

        startTV.setText(currentSchedule.getStart() + "\n" + "\n");
        finishTV.setText(currentSchedule.getFinish());

        return convertView;
    }
}
