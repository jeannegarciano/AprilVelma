package com.thesis.velma;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.thesis.velma.helper.DataBaseHandler;
import com.thesis.velma.helper.OkHttp;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.StringTokenizer;
import java.util.concurrent.TimeUnit;

public class acceptEvent extends AppCompatActivity {

    Context mcontext;
    public static DataBaseHandler db;
    int i;
    TextView title, ename, n, edescription, description, sdText, sd, edText, ed, stText, st, et, etText, fText, f, lText, l;
    TextView userT, userId, eventT, eventId;
    Button accept;
    String en, des, sDate, endDate, sTime, eTime, iFriends, locat, idUser, lat, lng, eventID, creator;
    Long idEvent;
    String modetravel = "driving";
    Bundle b;
    private PendingIntent pendingIntent;


    String locNameA = "", locLatA = "", locLngA = "", locLocationA = "", locSDA = "", locEDA = "", locSTA  = "", locETA = "";
    String locNameB= "", locLatB= "", locLngB= "", locLocationB= "", locSDB= "", locEDB= "", locSTB= "", locETB= "";
    long diffInMinutesA, diffInMinutesB;
    String diffA, diffB;
    double latA, lngA, latB, lngB;
    int eventid;

    ArrayList<String> myConflictEvents = new ArrayList<String>();
    ArrayList<String> myCurrentEvent = new ArrayList<>();

    String userEmail;


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

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mcontext);
        //then you use
        userEmail = prefs.getString("Email", null);

        Bundle bundle = getIntent().getExtras();
        eventid = bundle.getInt("eventid");
        Log.d("ID CANCELED", String.valueOf(eventid));


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

//        title = (TextView) findViewById(R.id.textView);
//        ename = (TextView) findViewById(R.id.eventName);
//        n = (TextView) findViewById(R.id.name);
//        edescription = (TextView) findViewById(R.id.eventDescription);
//        description = (TextView) findViewById(R.id.eventDescription1);
//        sdText = (TextView) findViewById(R.id.sdate);
//        sd = (TextView) findViewById(R.id.sdate1);
//        edText = (TextView) findViewById(R.id.edate);
//        ed = (TextView) findViewById(R.id.edate1);
//        stText = (TextView) findViewById(R.id.stime);
//        st = (TextView) findViewById(R.id.stime1);
//        etText = (TextView) findViewById(R.id.etime);
//        et = (TextView) findViewById(R.id.etime1);
//        fText = (TextView) findViewById(R.id.friends);
//        f = (TextView) findViewById(R.id.friends1);
//        accept = (Button) findViewById(R.id.acceptEvent);
//        accept.setOnClickListener(this);
//        lText = (TextView) findViewById(R.id.location);
//        l = (TextView) findViewById(R.id.location1);
//        userT = (TextView) findViewById(R.id.userIdText);
//        userId = (TextView) findViewById(R.id.userIdText1);
//        eventT = (TextView) findViewById(R.id.eventId);
//        eventId = (TextView) findViewById(R.id.eventId1);


        b = this.getIntent().getExtras();
//        i = b.getInt("ID");



        //Okay button from alarm notif

//        en = b.getString("name");
//        des = b.getString("description");
//        sDate =  b.getString("dateS");
//        endDate = b.getString("dateE");
//        sTime = b.getString("start");
//        eTime = b.getString("end");
//        iFriends = b.getString("people");
//        locat = b.getString("location");

//        n.setText(en);
//        description.setText(des);
//        sd.setText(sDate);
//        ed.setText(endDate);
//        st.setText(sTime);
//        et.setText(eTime);
//        f.setText(iFriends);
//        l.setText(locat);


        if (b!= null) {// to avoid the NullPointerException

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
        }

        Log.i("Event Accept", lat +","+ lng);
        collapsingToolbarLayout.setTitle(en);
        mdesc.setText(des);
        msdate.setText(sDate);
        medate.setText(endDate);
        mstime.setText(sTime);
        metime.setText(eTime);
        mfriends.setText(iFriends);
        mlocation.setText(locat);
//        userId.setText(idUser);
//        eventId.setText(String.valueOf(idEvent));


//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.accept);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                String target[] = creator.split("@");
//
//                OkHttp.getInstance(mcontext).sendNotificationReply("confirmEvent", en, des, target[0] + "Velma");
//
//                Toast.makeText(getApplicationContext(), "Accept event invitation", Toast.LENGTH_SHORT).show();
//
//                Intent i = new Intent(acceptEvent.this, LandingActivity.class);
//                finish();
//                startActivity(i);
//
//          //          saveEventFunction(idEvent,  en,  des, locat,sDate, sTime,  endDate, eTime, b.getString("notify"), iFriends,  lat,lng);
//                 }
//    });

        Button acceptInvite = (Button)findViewById(R.id.accept);
        acceptInvite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String target[] = creator.split("@");

                OkHttp.getInstance(mcontext).sendNotificationReply("confirmEvent", en, des, target[0] + "Velma");
                OkHttp.getInstance(mcontext).updateStatus(idUser,idEvent);
                Toast.makeText(getApplicationContext(), "Accept event invitation", Toast.LENGTH_SHORT).show();



                Intent i = new Intent(acceptEvent.this, LandingActivity.class);
                finish();
                startActivity(i);

                //          saveEventFunction(idEvent,  en,  des, locat,sDate, sTime,  endDate, eTime, b.getString("notify"), iFriends,  lat,lng);
            }
        });

        Button declineInvite = (Button)findViewById(R.id.decline);
        declineInvite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String target[] = creator.split("@");

                OkHttp.getInstance(mcontext).sendNotificationReply("declineEvent", en, des, target[0] + "Velma");

                Toast.makeText(getApplicationContext(), "Declined event invitation", Toast.LENGTH_SHORT).show();

                Intent i = new Intent(acceptEvent.this, LandingActivity.class);
                finish();
                startActivity(i);

                //          saveEventFunction(idEvent,  en,  des, locat,sDate, sTime,  endDate, eTime, b.getString("notify"), iFriends,  lat,lng);
            }
        });
}

//    public void saveEventFunction(final long idEvent, final String en, final String des, final String locat, final String sDate, final String sTime, final String endDate, final String eTime, final String notify, final String iFriends, final String lat, final String lng){
//        String[] mydates = sDate.split("-");
//        String[] mytimes = sTime.split(":");
//
//        int count;
//        Cursor c = db.getMaxId();
//        c.moveToFirst();
//        if(c.getCount() == 0)
//        {
//            count = 1;
//        }
//        else {
//            count = Integer.parseInt(c.getString(0));
//            count +=1;
//        }
//
//        Log.d("Count", "" + count);
//
//        AlarmManager alarmManager = (AlarmManager) mcontext.getSystemService(Context.ALARM_SERVICE);
//        Intent myIntent = new Intent(mcontext, AlarmReceiver.class);
//        myIntent.putExtra("unix",  idEvent);
//        myIntent.putExtra("name", en);
//        myIntent.putExtra("description", des);
//        myIntent.putExtra("location", locat);
//        myIntent.putExtra("start", sTime);
//        myIntent.putExtra("end", eTime);
//        myIntent.putExtra("dateS", sDate);
//        myIntent.putExtra("dateE", endDate);
//        myIntent.putExtra("people", iFriends);
//        Log.d("Event name", en);
//        pendingIntent = PendingIntent.getBroadcast(mcontext, count+1, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//        Calendar calNow = Calendar.getInstance();
//        Calendar calSet = (Calendar) calNow.clone();
//        calSet.setTimeInMillis(System.currentTimeMillis());
//        calSet.set(Calendar.YEAR, Integer.parseInt(mydates[0]));
//        calSet.set(Calendar.MONTH, Integer.parseInt(mydates[1])-1);
//        calSet.set(Calendar.DATE, Integer.parseInt(mydates[2]));
//        calSet.set(Calendar.HOUR_OF_DAY, Integer.parseInt(mytimes[0]));
//        calSet.set(Calendar.MINUTE, Integer.parseInt(mytimes[1]));
//        calSet.set(Calendar.SECOND, 0);
//        calSet.set(Calendar.MILLISECOND, 0);
//        //Logs for checking values received.
//        Log.d("AcceptActivityAlarm1", "" + calSet.getTimeInMillis());
//        Log.d("AcceptActivityAlarm2", "" + System.currentTimeMillis());
//
//        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calSet.getTimeInMillis(), pendingIntent);
//
//        //Saving to local database
////        LandingActivity.db.saveEvent(LandingActivity.imei, idEvent, en, des, locat, sDate, sTime,
////                endDate, eTime, b.getString("notify"), iFriends, userEmail, lat, lng);
////
////        //Saving to mysql database
////        OkHttp.getInstance(getBaseContext()).saveEvent(idEvent, en, des, locat, sDate, sTime, endDate,
////                eTime, b.getString("notify"), iFriends, userEmail, lat, lng);
//
//
//        Log.d("AcceptDetail", "" + LandingActivity.imei + " Id Event: " + idEvent + "" +
//                en + "" + des + "" + locat + "" +
//                sDate + "" + sTime + "" + endDate + "" +
//                eTime + "" + b.getString("notify") + "" + iFriends);
//
//        Intent i = new Intent(acceptEvent.this, LandingActivity.class);
//        startActivity(i);
//        finish();
//
//        Log.d("Data1", "" + LandingActivity.imei + " Id Event: " + idEvent + "" +
//                en + "" + des + "" + locat + "" +
//                sDate + "" + sTime + "" + endDate + "" +
//                eTime + "" + b.getString("notify") + "" + iFriends+ "" +lat+ ""+lng);
//        Log.d("Data2", "" + LandingActivity.imei + "" + b.getLong("eventid") + "" +
//                b.getString("eventname") + "" + b.getString("eventDescription") + "" + b.getString("eventLocation") + "" +
//                b.getString("eventStartDate") + "" + b.getString("eventStartTime") + "" + b.getString("eventEndDate") + "" +
//                b.getString("eventEndTime") + "" + b.getString("notify") + "" + b.getString("invitedfriends"));
//
//        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//        notificationManager.cancel(eventid);
//    }

}

