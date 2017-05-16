package com.thesis.velma;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.thesis.velma.helper.DBInfo;
import com.thesis.velma.helper.DataBaseHandler;
import com.thesis.velma.helper.OkHttp;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static android.content.ContentValues.TAG;

public class UpdateEventActivity extends AppCompatActivity implements View.OnClickListener{

    View rootView;
    EditText editEname, editDescription, editSdate, editEdate, editStime, editEtime, editFriends, editLoc;
    PlaceAutocompleteFragment searchLoc;
    Button buttonAddFriends;
    CheckBox allday;
    Context mcontext;
    DataBaseHandler db;
    String eventID;
    String list = "";

    String id;

    public static int sYear, sMonth, sDay, sHour, sMinute;
    public static int eYear, eMonth, eDay, eHour, eMinute;
    DatePickerDialog datePickerDialog;
    long timeInMilliseconds;
    Dialog dialog;
    String modetravel = "driving";
    Double latitude, longtiude;
    public static String geolocation;
    boolean locationIsClick = false;
    String name="", desc="", sdate="", edate="", stime="", etime="", loc="", friends="", longi="", lati="";

    int REQUEST_INVITE = 1;

    ArrayList<String> myContacts = new ArrayList<String>();
    public static ArrayList<String> invitedContacts = new ArrayList<String>();

    String user_id, allDay;
    FloatingActionButton save;

    boolean isInviteButtonClicked = false;
    String oldNew = "";
    String[] on = new String[0];
    private PendingIntent pendingIntent;
    int count;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_event);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(0);

        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        Typeface custom_font = Typeface.createFromAsset(getAssets(), "fonts/avenir-next-regular.ttf");

        mcontext = this;

        Bundle bundle = getIntent().getExtras();
        id = bundle.getString("key");

        Cursor a = LandingActivity.db.getEventDetails(id);

        while (a.moveToNext()){
            name = a.getString(a.getColumnIndex(DBInfo.DataInfo.EVENT_NAME));
            desc = a.getString(a.getColumnIndex(DBInfo.DataInfo.EVENT_DESCRIPTION));
            sdate = a.getString(a.getColumnIndex(DBInfo.DataInfo.START_DATE));
            edate = a.getString(a.getColumnIndex(DBInfo.DataInfo.END_DATE));
            stime = a.getString(a.getColumnIndex(DBInfo.DataInfo.START_TIME));
            etime = a.getString(a.getColumnIndex(DBInfo.DataInfo.END_TIME));
            loc = a.getString(a.getColumnIndex(DBInfo.DataInfo.EVENT_LOCATION));
            friends = a.getString(a.getColumnIndex(DBInfo.DataInfo.RECIPIENTS));
            longi = a.getString(a.getColumnIndex(DBInfo.DataInfo.LONGITUDE));
            lati = a.getString(a.getColumnIndex(DBInfo.DataInfo.LATITUDE));

        }

        editEname = (EditText) findViewById(R.id.eName);
        editEname.setText(name);
        editEname.setTypeface(custom_font);
        editDescription = (EditText) findViewById(R.id.desc);
        editDescription.setText(desc);
        editDescription.setTypeface(custom_font);
        editSdate = (EditText) findViewById(R.id.sDate);
        editSdate.setText(sdate);
        editSdate.setTypeface(custom_font);
        editSdate.setInputType(InputType.TYPE_NULL);
        editEdate = (EditText) findViewById(R.id.eDate);
        editEdate.setText(edate);
        editEdate.setTypeface(custom_font);
        editEdate.setInputType(InputType.TYPE_NULL);
        editStime = (EditText) findViewById(R.id.sTime);
        editStime.setText(stime);
        editStime.setTypeface(custom_font);
        editStime.setInputType(InputType.TYPE_NULL);
        editEtime = (EditText) findViewById(R.id.eTime);
        editEtime.setText(etime);
        editEtime.setTypeface(custom_font);
        editEtime.setInputType(InputType.TYPE_NULL);
        allday = (CheckBox) findViewById(R.id.checkAllDay);
        allday.setTypeface(custom_font);
        editLoc = (EditText) findViewById(R.id.loc);
        editLoc.setText(loc);
        editLoc.setTypeface(custom_font);
        editFriends = (EditText) findViewById(R.id.friends);
        editFriends.setText(friends);
        editFriends.setTypeface(custom_font);
        buttonAddFriends = (Button) findViewById(R.id.addFriends);
        buttonAddFriends.setTypeface(custom_font);
        save = (FloatingActionButton) findViewById(R.id.fabSave);
        searchLoc = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
//        searchLoc.setBoundsBias(new LatLngBounds(
//                new LatLng(14.599512, 120.984222),
//                new LatLng(14.599512, 120.984222)));
        editSdate.setOnClickListener(this);
        editEdate.setOnClickListener(this);
        editStime.setOnClickListener(this);
        editEtime.setOnClickListener(this);
        buttonAddFriends.setOnClickListener(this);
        save.setOnClickListener(this);
        final String oldList = editFriends.getText().toString();
        final String[] separateOld = oldList.split("\n");


        AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_ADDRESS)
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_GEOCODE)
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_ESTABLISHMENT)
                .build();
        searchLoc.setFilter(typeFilter);

        searchLoc.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                Log.i(TAG, "Place: " + place.getAddress());//get place details here

                locationIsClick = true;

                editLoc.setText("" + place.getName());
                geolocation = place.getAddress().toString();

                String v;


                latitude = place.getLatLng().latitude;
                longtiude = place.getLatLng().longitude;

                String coordinates = latitude + "," + longtiude;
                Log.i("latlang", "" + latitude + ":" + longtiude);
                Log.i("event geo", geolocation);
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences
                        (getApplicationContext()).edit();
                editor.putString("coordinates", coordinates);
                editor.putString("location", geolocation);

                Log.d("latlang", "" + latitude + ":" + longtiude);

                new UpdateEventActivity.getDetails().execute();

            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });





        datePickerDialog = new DatePickerDialog(UpdateEventActivity.this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {

                        String sd = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;

                        SimpleDateFormat sdf;
                        try {

                            sdf = new SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault());

                            Date mDate = sdf.parse(sd);
                            String strdate = sdf.format(mDate);
                            editSdate.setText(strdate);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                    }
                }, sYear, sMonth, sDay);
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 10000);


        AlertDialog.Builder builder = new AlertDialog.Builder(UpdateEventActivity.this)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String oldie = "";
//                        editFriends.setText(null);
                        for(int s = 0; s < separateOld.length;s++)
                        {
                            oldie = oldie + separateOld[s].replace(" (Pending)", "").trim() + "\n";
                        }

                        for (int i = 0; i < invitedContacts.size(); i++) {

                            list = list + invitedContacts.get(i) + "\n";
                        }
                        oldNew = oldie+list;
                        String[] newOld = oldNew.split("\n");
                        Set<String> oldN = new HashSet<String>(Arrays.asList(newOld));//removing duplicates
                        on = oldN.toArray(new String[0]);//all contacts with no duplicate
                        String newcontact = "";
                        for(int o=0; o < on.length; o++)
                        {
                            newcontact = newcontact + on[o] + "\n";//converting array into a string
                        }
                        editFriends.setText(newcontact);
                        Log.d("NEWCONTACTNODUP: ", oldN.toString());
                        Log.d("newcontact: ", newcontact);
                        Log.d("NEWCONTACT: ", oldNew);

                        Log.d("InvitedList:", list);

                    }
                }).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        invitedContacts.clear();
                        dialog.dismiss();
                        // Do stuff when user neglects.
                    }
                });
        builder.setTitle("Select From Contacts");

        ListView modeList = new ListView(UpdateEventActivity.this);
        //modeList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        //modeList.setItemsCanFocus(false);
        // String[] stringArray = new String[] { "Bright Mode", "Normal Mode" };


        Cursor c = LandingActivity.db.getContacts();

        while (c.moveToNext()) {
            myContacts.add(c.getString(c.getColumnIndex(DBInfo.DataInfo.CONTACT_NAME))); //this adds an element to the list.
        }

        ArrayAdapter<String> modeAdapter = new ArrayAdapter<String>(UpdateEventActivity.this, android.R.layout.simple_list_item_1, android.R.id.text1, myContacts);
        modeList.setAdapter(modeAdapter);

        builder.setView(modeList);

        dialog = builder.create();


        modeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // When clicked, show a toast with the TextView text
                //Toast.makeText(getContext(), ((TextView) view).getText(),
                //        Toast.LENGTH_SHORT).show();

                isInviteButtonClicked = true;

                int flag=0;
                for(int i=0 ; i<invitedContacts.size() ; i++){
                    if(invitedContacts.get(i).equals(((TextView) view).getText().toString()))
                        flag=1;
                }
                if(flag==0){

                    invitedContacts.add(((TextView) view).getText().toString());
                }
            }
        });


    }

    @Override
    public void onClick(View view) {


        if (view == editSdate) {
            final Calendar c = Calendar.getInstance();
            sYear = c.get(Calendar.YEAR);
            sMonth = c.get(Calendar.MONTH);
            sDay = c.get(Calendar.DAY_OF_MONTH);

            datePickerDialog.show();
        }  if (view == editEdate) {
            final Calendar c = Calendar.getInstance();
            eYear = c.get(Calendar.YEAR);
            eMonth = c.get(Calendar.MONTH);
            eDay = c.get(Calendar.DAY_OF_MONTH);
            String datetime;
            //i'll just put a static starttime kay para mmugana ang validation whether ni
            if(editSdate.getText() == null){
                editEdate.setClickable(false);
            }else {
                datetime = editSdate.getText().toString() + " 00:00";

                //    datetime = dateStart.getText().toString() + " " + timeStart.getText().toString();

                SimpleDateFormat sdf;
                try {

                    Log.i("Event dt", datetime);
                    sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault());

                    Date mDate = sdf.parse(datetime);
                    String strdate = sdf.format(mDate);
                    Log.i("Event String", strdate);
                    Log.i("Event mDate", "" + mDate);
                    timeInMilliseconds = mDate.getTime();
                    Log.i("Date in milli :: ", "" + timeInMilliseconds);
                } catch (ParseException e) {
                    e.printStackTrace();
                }


                DatePickerDialog datePickerDialog = new DatePickerDialog(UpdateEventActivity.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {

                                String ed = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;

                                SimpleDateFormat sdf;
                                try {


                                    sdf = new SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault());

                                    Date mDate = sdf.parse(ed);
                                    String nddate = sdf.format(mDate);
                                    editEdate.setText(nddate);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }

                            }
                        }, eYear, eMonth, eDay);
                datePickerDialog.getDatePicker().setMinDate(timeInMilliseconds);
                Log.i("Event datepicker", String.valueOf(System.currentTimeMillis() - 10000));
                datePickerDialog.show();
            }
        }


        if(allday.isChecked())
        {

            allDay = "All Day";

        }else{
            allDay = "Daily";
        }

        if (view == editStime) {

            final Calendar cal = Calendar.getInstance();
            sHour = cal.get(Calendar.HOUR_OF_DAY);
            sMinute = cal.get(Calendar.MINUTE);

            long start = cal.getTimeInMillis();
            Log.i("Event start", ""+start);
            if(editStime.getText()== null ){
                editEtime.setEnabled(false);
            }
            else {
                // Launch Time Picker Dialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(UpdateEventActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {



                                String st = hourOfDay + ":" + minute;

                                SimpleDateFormat sdf;
                                try {


                                    sdf = new SimpleDateFormat("HH:mm", java.util.Locale.getDefault());

                                    Date mDate = sdf.parse(st);
                                    String strtime = sdf.format(mDate);
                                    editStime.setText(strtime);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }

                            }
                        }, sHour, sMinute, false);


                timePickerDialog.show();
            }

        }  if (view == editEtime) {

            // Get Current Time

            final Calendar c = Calendar.getInstance();
            eHour = c.get(Calendar.HOUR_OF_DAY);
            eMinute = c.get(Calendar.MINUTE);


            // Launch Time Picker Dialog
            TimePickerDialog timePickerDialog = new TimePickerDialog(UpdateEventActivity.this,
                    new TimePickerDialog.OnTimeSetListener() {

                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay,
                                              int minute) {


                            String st = hourOfDay + ":" + minute;

                            SimpleDateFormat sdf;
                            try {


                                sdf = new SimpleDateFormat("HH:mm", java.util.Locale.getDefault());

                                Date mDate = sdf.parse(st);
                                String ndtime = sdf.format(mDate);
                                editEtime.setText(ndtime);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                    }, eHour, eMinute, false);


            timePickerDialog.show();
        }




        if(view == buttonAddFriends )
        {
            dialog.show();

        }

        if(view == save){
            String name = editEname.getText().toString();
            String eventDescription = editDescription.getText().toString();
            String eventLocation = editLoc.getText().toString();
            String lat = " "+latitude;
            String lng = " "+longtiude;
            String startDate = editSdate.getText().toString();
            String startTime = editStime.getText().toString();
            String endDate= editEdate.getText().toString();
            String endTime = editEtime.getText().toString();
            String eventAllDay = allDay;

            String invitedlist = editFriends.getText().toString();

            String[] separated = invitedlist.split("\n");
            Set<String> names = new HashSet<String>(Arrays.asList(separated));
            Cursor b = LandingActivity.db.getContacts();
            ArrayList<String> invitesid = new ArrayList<String>();

            String invitesemail = "";
            String[] listid = new String[separated.length];
            String listinvitesid="";
            String emailinvites = "";

            Log.d("invitedlist:", separated.toString());
            for(String na:names)
            {
                Log.d("names:", na);
            }
            while (b.moveToNext()){
                String contactsname = b.getString(b.getColumnIndex("contact_name"));
                String contactsid = b.getString(b.getColumnIndex("contact_user_id"));
                String contactsemail = b.getString(b.getColumnIndex("contact_email"));

                int i;

                for (i = 0; i < on.length; i++) {

                    if (contactsname.equals(on[i])) {
                        Log.d("Contactsname: ", contactsname);

                        listid[i] = contactsid;
                        listinvitesid = listinvitesid + contactsname + " (Pending)\n";
                        invitesid.add(contactsid);

                        invitesemail = invitesemail + contactsemail.replace("@gmail.com", "").trim() + "\n";
                    }
                }
            }

            String[] separateName = listinvitesid.split("\n");
            String[] separateEmail = invitesemail.split("\n");
            //Removing duplicate names
            Set<String> sname = new HashSet<String>(Arrays.asList(separateName));
            Set<String> semail = new HashSet<String>(Arrays.asList(separateEmail));
            String[] arrayname = sname.toArray(new String[0]);
            String[] arrayemail = semail.toArray(new String[0]);
            String finalName = "";
            String finalEmail = "";
            for(int count =0; count < arrayname.length; count++)
            {
                finalName = finalName + arrayname[count] + "\n";
                finalEmail = finalEmail + arrayemail[count] + "\n";
            }


            Log.d("listinvitesidname:", listinvitesid);
            Log.d("invitesemail:", invitesemail);
            Log.d("finalName:", finalName);
            Log.d("finalEmail:", finalEmail);
//            if (isInviteButtonClicked==true){
//                String[] eachemail = emailinvites.split("\n");
//                String countemail = String.valueOf(eachemail.length);
//                Log.d("countemail", countemail);
//
//                for (int i=0; i<eachemail.length; i++){
//                    //codes for sending notif
//                        //codes for sending notif
//                        OkHttp.getInstance(mcontext).sendNotificationUpdate("Update", id, name,
//                                eventDescription, eventLocation, startDate, startTime, endDate, endTime, eachemail[i]+ "Velma",
//                                lat, lng, LandingActivity.useremail, listinvitesid);//eachemail[0]
//
//                    Log.d("EachemailFirst", eachemail[i]);
//                }
//                Log.d("Amodia:", emailinvites);
//
//            } else {
//                String[] eachemail = invitesemail.split("\n");
//                String countemail = String.valueOf(eachemail.length);
//                Log.d("countemail", countemail);
//
//                for (int i=0; i<eachemail.length; i++){
//                    //codes for sending notif
//
//                    OkHttp.getInstance(mcontext).sendNotificationUpdate("Update", id, name,
//                            eventDescription, eventLocation, startDate, startTime, endDate, endTime, eachemail[i]+ "Velma",
//                            lat, lng, LandingActivity.useremail, listinvitesid);//eachemail[0]
//                    Log.d("EachemailSecond", eachemail[i]);
//                }
//                Log.d("Jane:", invitesemail);
//            }


            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mcontext);
            String sharedPrefUserId = sharedPreferences.getString("user_id", null);

            if (locationIsClick==true){
                OkHttp.getInstance(getBaseContext()).updateEvent(id, name, eventDescription, eventLocation, lng,  lat, startDate, startTime, endDate, endTime, eventAllDay, listid);
                LandingActivity.db.updateEvent(id, name, eventDescription, eventLocation, lng,  lat, startDate, startTime, endDate, endTime, eventAllDay, finalName);

                //Code for alarm


                DataBaseHandler db = new DataBaseHandler(this);
                Cursor c = db.getMaxId();
                c.moveToFirst();
                if (c.getCount() == 0) {
                    count = 1;
                } else {
                    count = Integer.parseInt(c.getString(0));
                    Log.d("CountNoPlus: ", "" +count);
                    count += 1;
                    Log.d("CountWithPlus: ", "" +count);
                }

                AlarmManager cancelAlarmManager = (AlarmManager) mcontext.getSystemService
                        (Context.ALARM_SERVICE);
                Intent myCancelIntent = new Intent(mcontext, AlarmReceiver.class);
//                PendingIntent morningIntent = PendingIntent.getBroadcast(MainActivity.getContext(), Alarm_id.get(positon),
//                        intentAlarm, PendingIntent.FLAG_CANCEL_CURRENT);
                pendingIntent = PendingIntent.getBroadcast(mcontext, count, myCancelIntent,
                        PendingIntent.FLAG_CANCEL_CURRENT);

                cancelAlarmManager.cancel(pendingIntent);
                pendingIntent.cancel();

                Log.d("Count", "" + count);

                AlarmManager alarmManager = (AlarmManager) mcontext.getSystemService
                        (Context.ALARM_SERVICE);
                Intent myIntent = new Intent(mcontext, AlarmReceiver.class);
                myIntent.putExtra("userID", user_id);
                myIntent.putExtra("name", name);
                myIntent.putExtra("description", eventDescription);
                myIntent.putExtra("location", eventLocation);
                myIntent.putExtra("start", startTime);
                myIntent.putExtra("end", endTime);
                myIntent.putExtra("dateS", startDate);
                myIntent.putExtra("dateE", endDate);
                myIntent.putExtra("people", listinvitesid);
                Log.d("MyData", name);
                pendingIntent = PendingIntent.getBroadcast(mcontext, count + 1, myIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);

                Calendar calNow = Calendar.getInstance();
                Calendar calSet = (Calendar) calNow.clone();
                calSet.setTimeInMillis(System.currentTimeMillis());

                String[] mydates = startDate.split("-");
                String[] mytimes = startTime.split(":");

                String date = "" + mydates[0] + "-" + mydates[1] + "-" + mydates[2];

                calSet.set(Calendar.YEAR, Integer.parseInt(mydates[0]));
                calSet.set(Calendar.MONTH, Integer.parseInt(mydates[1]) - 1);
                calSet.set(Calendar.DATE, Integer.parseInt(mydates[2]));
                calSet.set(Calendar.HOUR_OF_DAY, Integer.parseInt(mytimes[0]));
                calSet.set(Calendar.MINUTE, Integer.parseInt(mytimes[1]));
                calSet.set(Calendar.SECOND, 0);
                calSet.set(Calendar.MILLISECOND, 0);

                Log.d("AlaramJeanne", "" + calSet.getTimeInMillis());
                Log.d("AlaramJeanne1", "" + System.currentTimeMillis());

                alarmManager.setExact(AlarmManager.RTC_WAKEUP, calSet.getTimeInMillis(),
                        pendingIntent);
                //Code for the alarm ends here....

                for (int i=0; i<arrayemail.length; i++){
                    //codes for sending notif

                    OkHttp.getInstance(mcontext).sendNotification("Invitation", sharedPrefUserId, id,
                            name,
                            eventDescription, eventLocation, startDate, startTime, endDate, endTime, arrayemail[i].replace("@gmail", "")+ "Velma",
                            lat, lng, LandingActivity.useremail, finalName, eventAllDay, "Update");//eachemail[0]

                    Log.d("EachemailFirst", arrayemail[i]);
                }

//
//                String[] target = invitesemail.split("\n");
//
//                for (int i=0; i<target.length; i++){
//                    //codes for sending notif
//                    OkHttp.getInstance(mcontext).sendNotificationUpdate("Update", id, name,
//                            eventDescription, eventLocation, startDate, startTime, endDate, endTime, target[i]+ "Velma",
//                            lat, lng, LandingActivity.useremail, listid, listinvitesid);//eachemail[0]
//                    Log.d("Emails who accepted:", target[i]);
//
//                }

//                    OkHttp.getInstance(mcontext).sendNotificationUpdate("Update", id, name,
//                            eventDescription, eventLocation, startDate, startTime, endDate, endTime, target[i]+ "Velma",
//                            lat, lng, LandingActivity.useremail, listid, listinvitesid);//eachemail[0]
            } else {
                OkHttp.getInstance(getBaseContext()).updateEvent(id, name, eventDescription, eventLocation, longi,  lati, startDate, startTime, endDate, endTime, eventAllDay, listid);
                LandingActivity.db.updateEvent(id, name, eventDescription, eventLocation, longi,  lati, startDate, startTime, endDate, endTime, eventAllDay, finalName);

                //Code for alarm

                int count;
                DataBaseHandler db = new DataBaseHandler(this);
                Cursor c = db.getMaxId();
                c.moveToFirst();
                if (c.getCount() == 0) {
                    count = 1;
                } else {
                    count = Integer.parseInt(c.getString(0));
                    Log.d("CountNoPlus: ", "" +count);
                    count += 1;
                    Log.d("CountWithPlus: ", "" +count);
                }

                AlarmManager cancelAlarmManager = (AlarmManager) mcontext.getSystemService
                        (Context.ALARM_SERVICE);
                Intent myCancelIntent = new Intent(mcontext, AlarmReceiver.class);
//                PendingIntent morningIntent = PendingIntent.getBroadcast(MainActivity.getContext(), Alarm_id.get(positon),
//                        intentAlarm, PendingIntent.FLAG_CANCEL_CURRENT);
                pendingIntent = PendingIntent.getBroadcast(mcontext, count, myCancelIntent,
                        PendingIntent.FLAG_CANCEL_CURRENT);

                cancelAlarmManager.cancel(pendingIntent);
                pendingIntent.cancel();

                Log.d("Count", "" + count);

                AlarmManager alarmManager = (AlarmManager) mcontext.getSystemService
                        (Context.ALARM_SERVICE);
                Intent myIntent = new Intent(mcontext, AlarmReceiver.class);
                myIntent.putExtra("userID", user_id);
                myIntent.putExtra("name", name);
                myIntent.putExtra("description", eventDescription);
                myIntent.putExtra("location", eventLocation);
                myIntent.putExtra("start", startTime);
                myIntent.putExtra("end", endTime);
                myIntent.putExtra("dateS", startDate);
                myIntent.putExtra("dateE", endDate);
                myIntent.putExtra("people", listinvitesid);
                Log.d("MyData", name);
                pendingIntent = PendingIntent.getBroadcast(mcontext, count + 1, myIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);

                Calendar calNow = Calendar.getInstance();
                Calendar calSet = (Calendar) calNow.clone();
                calSet.setTimeInMillis(System.currentTimeMillis());


                String[] mydates = startDate.split("-");
                String[] mytimes = startTime.split(":");


                String date = "" + mydates[0] + "-" + mydates[1] + "-" + mydates[2];

                calSet.set(Calendar.YEAR, Integer.parseInt(mydates[0]));
                calSet.set(Calendar.MONTH, Integer.parseInt(mydates[1]) - 1);
                calSet.set(Calendar.DATE, Integer.parseInt(mydates[2]));
                calSet.set(Calendar.HOUR_OF_DAY, Integer.parseInt(mytimes[0]));
                calSet.set(Calendar.MINUTE, Integer.parseInt(mytimes[1]));
                calSet.set(Calendar.SECOND, 0);
                calSet.set(Calendar.MILLISECOND, 0);

                Log.d("AlaramJeanne", "" + calSet.getTimeInMillis());
                Log.d("AlaramJeanne1", "" + System.currentTimeMillis());

                alarmManager.setExact(AlarmManager.RTC_WAKEUP, calSet.getTimeInMillis(),
                        pendingIntent);
                //Code for the alarm ends here....

                for (int i=0; i<arrayemail.length; i++){
                    //codes for sending notif
                    //codes for sending notif
//                    OkHttp.getInstance(mcontext).sendNotification("Invitation", id, name,
//                            eventDescription, eventLocation, startDate, startTime, endDate, endTime, arrayemail[i].replace("@gmail", "")+ "Velma",
//                            lat, lng, LandingActivity.useremail, finalName);//eachemail[0]
                    OkHttp.getInstance(mcontext).sendNotification("Invitation", sharedPrefUserId, id,
                            name,
                            eventDescription, eventLocation, startDate, startTime, endDate, endTime, arrayemail[i].replace("@gmail", "")+ "Velma",
                            lat, lng, LandingActivity.useremail, finalName, eventAllDay, "Update");//eachemail[0]

//                    OkHttp.getInstance(mcontext).sendNotification("Invitation", sharedPrefUserId, id, name,
//                            eventDescription, eventLocation, startDate, startTime, endDate, endTime, arrayemail[i].replace("@gmail", "") + "Velma",
//                            lat, lng, LandingActivity.useremail, finalName);

                    Log.d("EachemailFirst", arrayemail[i]);
                }
            }

            Log.i("Event allDay", eventAllDay);
            Log.i("Event name", name);
            Log.i("Event SD", startDate);
            Log.i("Event coord", lat +","+lng);
            Toast.makeText(mcontext, "Event is updated successfully.", Toast.LENGTH_SHORT).show();

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mcontext);
            eventID = prefs.getString("sharedEventID", eventID);


//            GitID gitID = null;
//            String eventIDnew = gitID.getGitEventId();
//
//            Log.i("Cathlyn", eventID +" "+ sharedPrefUserId);
//
//            LandingActivity.db.saveEvent(Integer.valueOf(sharedPrefUserId), Integer.valueOf(eventID), name, eventDescription,
//                    eventLocation, lng, lat, startDate, startTime, endDate, endTime, null, "Creator", null);

            Intent i = new Intent(UpdateEventActivity.this, LandingActivity.class);
            finish();
            startActivity(i);


        }
    }

    class getDetails extends AsyncTask<Void, Void, String> {

        protected String getASCIIContentFromEntity(HttpEntity entity)
                throws IllegalStateException, IOException {
            InputStream in = entity.getContent();
            StringBuffer out = new StringBuffer();
            int n = 1;
            while (n > 0) {
                byte[] b = new byte[4096];
                n = in.read(b);
                if (n > 0)
                    out.append(new String(b, 0, n));
            }
            return out.toString();
        }

        @Override
        protected String doInBackground(Void... params) {

            String text = null;
            String coordinates = latitude + "," + longtiude;
            Log.d("Coordinates", coordinates);
            try {
                String regAPIURL = "https://maps.googleapis.com/maps/api/distancematrix/json?units=metric&origins=" + LandingActivity.origlatitude + "," + LandingActivity.origlongitude;
                regAPIURL = regAPIURL + "&destinations=" + URLEncoder.encode(coordinates);
                regAPIURL = regAPIURL + "&mode=" + URLEncoder.encode(modetravel);
                regAPIURL = regAPIURL + "&key=AIzaSyDWjoAbJf9uDrLCFAM_fCSWxP0muVEGbOA";
                Log.d("URI", regAPIURL);
                HttpGet httpGet = new HttpGet(regAPIURL);
                HttpParams httpParameters = new BasicHttpParams();
                int timeoutConnection = 60000;
                HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
                int timeoutSocket = 60000;
                HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

                DefaultHttpClient httpClient = new DefaultHttpClient(httpParameters);
                HttpResponse response = httpClient.execute(httpGet);
                HttpEntity entity = response.getEntity();
                text = getASCIIContentFromEntity(entity);

            } catch (Exception e) {
                text = null;
            }

            return text;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d("Result", s);

            if (s != null) {
                try {

                    String distance = new JSONObject(s)
                            .getJSONArray("rows")
                            .getJSONObject(0)
                            .getJSONArray("elements")
                            .getJSONObject(0)
                            .getJSONObject("distance").getString("text");

                    String duration = new JSONObject(s)
                            .getJSONArray("rows")
                            .getJSONObject(0)
                            .getJSONArray("elements")
                            .getJSONObject(0)
                            .getJSONObject("duration").getString("text");
//
//                    distanceduration.setText("Distance : " + distance + ": Duration : " + duration);
//                    distanceduration.setVisibility(View.VISIBLE);


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }


}

