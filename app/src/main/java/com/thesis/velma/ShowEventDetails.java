package com.thesis.velma;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.thesis.velma.helper.CheckInternet;
import com.thesis.velma.helper.DBInfo;
import com.thesis.velma.helper.DataBaseHandler;
import com.thesis.velma.helper.NetworkUtil;
import com.thesis.velma.helper.OkHttp;

import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class ShowEventDetails extends AppCompatActivity {

    LandingActivity landingActivity = new LandingActivity();
    Context mcontext;
    static String id, eventID;
    public static DataBaseHandler db;
    String[] invitedFriends = null;
    String eventName, eventDesc, sdate, edate, stime, etime, location, friends, lng, lat, role;
    Long unixtime = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mcontext=this;
        db = new DataBaseHandler(mcontext);

        setContentView(R.layout.activity_show_event_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(0);

        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        Typeface custom_font = Typeface.createFromAsset(getAssets(),  "fonts/avenir-next-regular.ttf");
        TextView mdesc = (TextView) findViewById(R.id.desc);
        mdesc.setTypeface(custom_font);
        TextView msdate = (TextView) findViewById(R.id.sDate);
        msdate.setTypeface(custom_font);
        TextView medate = (TextView) findViewById(R.id.eDate);
        medate.setTypeface(custom_font);
        TextView mstime = (TextView) findViewById(R.id.sTime);
        mstime.setTypeface(custom_font);
        TextView metime = (TextView) findViewById(R.id.eTime);
        metime.setTypeface(custom_font);
        TextView mlocation = (TextView) findViewById(R.id.loc);
        mlocation.setTypeface(custom_font);
        TextView mfriends = (TextView) findViewById(R.id.friends);
        mfriends.setTypeface(custom_font);

        Bundle bundle = getIntent().getExtras();
        id = bundle.getString("key");
        Log.d("cathlyn: ", id);

        Cursor c = db.getEventDetails(id);

        while (c.moveToNext()){
            eventName = c.getString(c.getColumnIndex(DBInfo.DataInfo.EVENT_NAME));
            eventDesc = c.getString(c.getColumnIndex(DBInfo.DataInfo.EVENT_DESCRIPTION));
            sdate = c.getString(c.getColumnIndex(DBInfo.DataInfo.START_DATE));
            edate = c.getString(c.getColumnIndex(DBInfo.DataInfo.END_DATE));
            stime = c.getString(c.getColumnIndex(DBInfo.DataInfo.START_TIME));
            etime = c.getString(c.getColumnIndex(DBInfo.DataInfo.END_TIME));
            location = c.getString(c.getColumnIndex(DBInfo.DataInfo.EVENT_LOCATION));
            friends = c.getString(c.getColumnIndex(DBInfo.DataInfo.RECIPIENTS));
            role = c.getString(c.getColumnIndex(DBInfo.DataInfo.ROLE));
        }

        Log.d("StartTimeeeee: ", stime);

        collapsingToolbarLayout.setTitle(eventName);
        mdesc.setText(eventDesc);

        SimpleDateFormat oldSdateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date oldSdate = null;
        String newSdate = null;
        try {
            oldSdate = oldSdateFormat.parse(sdate);
            SimpleDateFormat newSdateFormat = new SimpleDateFormat("EEEE, MMMM dd, yyyy");
            newSdate = newSdateFormat.format(oldSdate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        msdate.setText(newSdate);

        SimpleDateFormat oldEdateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date oldEdate = null;
        String newEdate = null;
        try {
            oldEdate = oldEdateFormat.parse(edate);
            SimpleDateFormat newEdateFormat = new SimpleDateFormat("EEEE, MMMM dd, yyyy");
            newEdate = newEdateFormat.format(oldEdate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        medate.setText(newEdate);

        DateFormat formatstart = new SimpleDateFormat("HH:mm");
        Date formatStime = null;
        String newStime=null;
        try {
            formatStime = formatstart.parse(stime);
            DateFormat newformatstart = new SimpleDateFormat("h:mma");
            newStime = newformatstart.format(formatStime);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        mstime.setText(newStime);

        DateFormat formatend = new SimpleDateFormat("HH:mm");
        Date formatEtime = null;
        String newEtime = null;
        try {
            formatEtime = formatend.parse(etime);
            DateFormat newformatend = new SimpleDateFormat("h:mma");
            newEtime = newformatend.format(formatEtime);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        metime.setText(newEtime);
        mlocation.setText(location);

//        String[] separated = friends.split(",");
//        for (int i=0; i<separated.length; i++){
//        }
        mfriends.setText(friends);

        Log.d("HFRIENDS: ", friends);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (role.equals("Creator")){

                    int status = NetworkUtil.getConnectivityStatusString(mcontext);

                    if (status == 0) {
                        CheckInternet.showConnectionDialog(mcontext);
                    }else {

                        Intent intent = new Intent(getBaseContext(), UpdateEventActivity.class);
                        intent.putExtra("key", id);
                        Log.d("id: ", id);
                        startActivity(intent);
                        setResult(RESULT_OK, intent);
                        finish();
                    }

                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_show_event_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){

            case R.id.action_delete:

                if (role.equals("Creator")){

                    int status = NetworkUtil.getConnectivityStatusString(mcontext);

                    if (status == 0) {
                        CheckInternet.showConnectionDialog(mcontext);
                    }else {

                        new AlertDialog.Builder(this)
                                .setMessage("Are you sure you want to delete event?")
                                .setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {



                                        String[] separated = friends.split("\n");
                                      //Removing names duplicates for people invited
                                        Set<String> names = new HashSet<String>(Arrays.asList(separated));
                                        Set<String> namess = new HashSet<String>(Arrays.asList(separated));

                                        for (String namee : namess) {
                                            Log.d("Hashset1: ", namee.replace("(Pending)", ""));
                                        }
                                        Log.d("Hashset2: ", namess.toString());
                                        //Ends here


//                                        Cursor b = db.getContacts();
//                                        b.moveToFirst();
//                                        for(String name : names)
//                                            {
//                                                    String email;
////                                                    Cursor e = db.getEmail(name.replace(" (Pending)","").replace(" (Accepted)","").replace(" (Declined)","").trim());
////                                                    e.moveToFirst();
////                                                    email = e.getString(0);
//                                                    Log.d("Email: ", email);
//
//                                            }


//                                        while (b.moveToNext()) {
//                                            String contactsname = b.getString(b.getColumnIndex("contact_name"));
//                                            String contactsemail = b.getString(b.getColumnIndex("contact_email"));
//                                            Log.d("Email: ", "whileeeeee");
//                                            for(String name : names)
//                                            {
//                                                if(contactsname.equals(name.matches(".*\b(Pending)\b.*")))
//                                                {
//                                                    String email;
//                                                    Cursor e = db.getEmail(name.replace(" (Pending)","").replace(" (Accepted)","").replace(" (Declined)","").trim());
//                                                    e.moveToFirst();
//                                                    email = e.getString(0);
//                                                    Log.d("Email: ", email);
//                                                }
//                                            }
//
//                                            Log.d("Contactname: ", contactsname);
//                                            Log.d("Contactemail: ", contactsemail);
//
//                                        }


                                        OkHttp.getInstance(mcontext).deleteEvent(id);
                                        db.deleteEvent(id);


                                        Intent i = new Intent(ShowEventDetails.this, LandingActivity.class);
                                        startActivity(i);
                                        setResult(RESULT_OK, i);
                                        finish();
                                    }
                                })
                                .setPositiveButton("No", null)
                                .show();
                    }

                }

                return true;


            default:
                return super.onOptionsItemSelected(item);

        }
    }
}