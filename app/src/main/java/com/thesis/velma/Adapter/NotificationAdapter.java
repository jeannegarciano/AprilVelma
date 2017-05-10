package com.thesis.velma.Adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.thesis.velma.LandingActivity;
import com.thesis.velma.Model.NotificationModel;
import com.thesis.velma.R;

import java.util.ArrayList;

/**
 * Created by andrewlaurienrsocia on 03/05/2017.
 */

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {


    ArrayList<NotificationModel> mDataset;

    public NotificationAdapter(ArrayList<NotificationModel> mDataset) {
        this.mDataset = mDataset;
        Log.d("Count", "" + mDataset.size());

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {


        public TextView mEventName;
        public TextView mLocation;
        public TextView mTime;
        public TextView mEventID;
        public TextView mStatus;

        public ViewHolder(View itemView) {
            super(itemView);

            mEventName = (TextView) itemView.findViewById(R.id.txtEventName);
            mLocation = (TextView) itemView.findViewById(R.id.txtLocation);
            mTime = (TextView) itemView.findViewById(R.id.txtTime);
            mEventID = (TextView) itemView.findViewById(R.id.txtEventID);
            mStatus = (TextView) itemView.findViewById(R.id.txtStatus);

        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_layout_request, parent, false);
        ViewHolder vh = new ViewHolder(view);

        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {


        holder.mEventName.setText(mDataset.get(position).getEventName());
        holder.mLocation.setText(mDataset.get(position).getEventLocation());
        holder.mStatus.setText(mDataset.get(position).getStatus());
        holder.mTime.setText(mDataset.get(position).getEventTime());
        holder.mEventID.setText(mDataset.get(position).getEventId());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Load the details

                LandingActivity.getLandingActivity().displayDetails(mDataset.get(position).getEventId());
            }
        });

    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
