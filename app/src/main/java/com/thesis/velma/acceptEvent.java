package com.thesis.velma;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.thesis.velma.helper.DataBaseHandler;
import com.thesis.velma.helper.OkHttp;

import java.util.ArrayList;

public class acceptEvent extends AppCompatActivity {

    Context mcontext;
    public static DataBaseHandler db;
    int i;
    TextView title, ename, n, edescription, description, sdText, sd, edText, ed, stText, st, et, etText, fText, f, lText, l;
    //    TextView userT, userId, eventT, eventId;
    Button accept;
    String en, des, sDate, endDate, sTime, eTime, iFriends, locat, idUser, lat, lng, eventID, creator;
    Long idEvent;
    String modetravel = "driving";
    Bundle b;
    private PendingIntent pendingIntent;


    //    String locNameA = "", locLatA = "", locLngA = "", locLocationA = "", locSDA = "", locEDA = "", locSTA = "", locETA = "";
//    String locNameB = "", locLatB = "", locLngB = "", locLocationB = "", locSDB = "", locEDB = "", locSTB = "", locETB = "";
//    long diffInMinutesA, diffInMinutesB;
//    String diffA, diffB;
//    double latA, lngA, latB, lngB;
    String eventid;

    ArrayList<String> myConflictEvents = new ArrayList<String>();
    ArrayList<String> myCurrentEvent = new ArrayList<>();

    String userEmail;
    String eventAllDay = "allDay";
    String friends;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accept_event_new);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mcontext = this;

        db = new DataBaseHandler(mcontext);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mcontext);
        final String sharedPrefUserId = sharedPreferences.getString("user_id", null);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mcontext);
        //then you use
        userEmail = prefs.getString("Email", null);

        Bundle bundle = getIntent().getExtras();
        idEvent = bundle.getLong("eventid");
        Log.d("ID CANCELED", String.valueOf(eventid));


        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        Typeface custom_font = Typeface.createFromAsset(getAssets(), "fonts/avenir-next-regular.ttf");
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


        b = this.getIntent().getExtras();
//        i = b.getInt("ID");


        if (b != null) {// to avoid the NullPointerException

            en = b.getString("eventname");
            des = b.getString("eventDescription");
            sDate = b.getString("eventStartDate");
            endDate = b.getString("eventEndDate");
            sTime = b.getString("eventStartTime");
            eTime = b.getString("eventEndTime");
            iFriends = b.getString("invitedfirends");
            locat = b.getString("eventLocation");
            idUser = b.getString("userid");
            idEvent = b.getLong("eventid");
            lat = b.getString("lat");
            lng = b.getString("lng");
            creator = b.getString("creatorEmail");
            friends = b.getString("listinvitesid");
        }

        Log.i("Event Accept", lat + "," + lng);
        collapsingToolbarLayout.setTitle(en);
        mdesc.setText(des);
        msdate.setText(sDate);
        medate.setText(endDate);
        mstime.setText(sTime);
        metime.setText(eTime);
        mfriends.setText(iFriends);
        mlocation.setText(locat);


        Cursor c = db.getUserId();
        c.moveToFirst();
        final String userI = c.getString(0);

        Button acceptInvite = (Button) findViewById(R.id.accept);
        acceptInvite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String target[] = creator.split("@");
                OkHttp.getInstance(mcontext).sendNotificationReply("confirmEvent", en, des, target[0] + "Velma", idEvent);
                OkHttp.getInstance(mcontext).updateStatus(idUser, String.valueOf(idEvent), "Accepted");
                Toast.makeText(getApplicationContext(), "Accept event invitation", Toast.LENGTH_SHORT).show();

                LandingActivity.db.updateEventStatus(String.valueOf(idEvent), "ACCEPTED");


                Log.d("MyData1UserId: ", String.valueOf(eventid));
                Log.d("MyData2EvntId: ", "" + idEvent);
                Log.d("MyData3EventName: ", en);
                Log.d("MyData4EventDes: ", des);
                Log.d("MyData5Friends: ", "" + friends);

//                LandingActivity.db.saveEvent(Integer.parseInt(userI), String.valueOf(idEvent), en, des,
//                        locat, lng, lat, sDate, sTime, endDate, eTime, eventAllDay, "Participant", friends);


                //Intent i = new Intent(acceptEvent.this, LandingActivity.class);


                Intent returnIntent = new Intent();
                setResult(0, returnIntent);
                finish();

                //startActivity(i);

            }
        });

        Button declineInvite = (Button) findViewById(R.id.decline);
        declineInvite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String target[] = creator.split("@");

                LandingActivity.db.updateEventStatus(String.valueOf(idEvent), "Declined");

                OkHttp.getInstance(mcontext).sendNotificationReply("declineEvent", en, des, target[0] + "Velma", idEvent);
                OkHttp.getInstance(mcontext).updateStatus(idUser, String.valueOf(idEvent), "Declined");
                Toast.makeText(getApplicationContext(), "Declined event invitation", Toast.LENGTH_SHORT).show();

                Intent returnIntent = new Intent();
                setResult(0, returnIntent);
                finish();
            }
        });
    }


}

