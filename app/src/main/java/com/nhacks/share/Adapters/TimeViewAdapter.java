package com.nhacks.share.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nhacks.share.R;

import org.json.JSONArray;

/**
 * Created by Sagar on 3/12/2016.
 */
public class TimeViewAdapter extends RecyclerView.Adapter<TimeViewAdapter.MyViewHolder> {
    public int[] times;
    private LayoutInflater inflater;
    Context context;
    int[] availableHours;

    public TimeViewAdapter(FragmentActivity activity, int[] times, int[] availableTimes) {
        super();
        this.times = times;
        this.context = activity;
        inflater = LayoutInflater.from(context);
        this.availableHours = availableTimes;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.l_time_row, parent, false);

        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        String t = "";
        if (position == 0) {
            t = "12:00 AM";
        } else if (position < 12) {
            t = t + Integer.toString(position) + ":00 AM";
        } else if (position == 12) {
            t = "12:00 PM";
        } else {
            t = t + Integer.toString(position - 12) + ":00 PM";
        }

        holder.tv.setText(t);

        if (times[position] == 1) {
            holder.bar.setVisibility(View.VISIBLE);
        } else {
            holder.bar.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return 24;
    }

    public void updateData(int[] availableHours) {
        for (int i = 0; i < 24; i++) {
            if (contains(availableHours, i)) {
                times[i] = 1;
            } else {
                times[i] = 0;
            }
        }
        notifyDataSetChanged();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv;
        View bar;

        public MyViewHolder(View itemView) {
            super(itemView);
            bar = (View) itemView.findViewById(R.id.colored_bar);
            tv = (TextView) itemView.findViewById(R.id.time1am);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (bar.getVisibility() == View.GONE) {
                        bar.setVisibility(View.VISIBLE);
                        times[getPosition()] = 1;
                    } else {
                        bar.setVisibility(View.GONE);
                        times[getPosition()] = 0;
                    }
                }

            });
        }

    }

    public Boolean contains(int[] availableHours, int num) {
        for (int i = 0; i < availableHours.length; i++) {
            if (availableHours[i] == num) {
                return true;
            }
        }
        return false;
    }


}