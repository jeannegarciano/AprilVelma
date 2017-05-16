package com.thesis.velma;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;

import com.thesis.velma.Adapter.NotificationAdapter;
import com.thesis.velma.Model.NotificationModel;

import java.util.ArrayList;

public class NotificationList extends AppCompatActivity {


    private RecyclerView mRecyclerView;
    private NotificationAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    public static ArrayList<NotificationModel> List;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_list);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(0);

        List = new ArrayList<>();

        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        Cursor cursor = LandingActivity.db.getPendingEvents(LandingActivity.db);


        if (cursor.getCount() > 0) {

            while (cursor.moveToNext()) {
                NotificationModel notificationModel = new NotificationModel(cursor.getString(cursor.getColumnIndex(("event_name"))),
                        cursor.getString(cursor.getColumnIndex("event_location")),
                        cursor.getString(cursor.getColumnIndex("start_time")),
                        cursor.getString(cursor.getColumnIndex("event_id")),
                        cursor.getString(cursor.getColumnIndex("EventStatus")));
                List.add(notificationModel);
            }
        }


        Log.d("Notification Count", "" + cursor.getCount());


        mAdapter = new NotificationAdapter(List);

        mRecyclerView.setAdapter(mAdapter);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) // Press Back Icon
        {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        this.finish();
    }
}
