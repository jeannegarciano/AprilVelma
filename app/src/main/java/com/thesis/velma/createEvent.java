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
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.concurrent.TimeUnit;

import static android.content.ContentValues.TAG;
import static com.thesis.velma.LandingActivity.useremail;

/**
 * Created by admin on 3/24/2017.
 */

public class createEvent extends AppCompatActivity implements View.OnClickListener {

    View rootView;
    EditText editEname, editDescription, editSdate, editEdate, editStime, editEtime, editFriends, editLoc;
    PlaceAutocompleteFragment searchLoc;
    Button buttonAddFriends;
    CheckBox allday;
    Context mcontext;
    DataBaseHandler db;
    String list = "";

    public static int sYear, sMonth, sDay, sHour, sMinute;
    public static int eYear, eMonth, eDay, eHour, eMinute;
    DatePickerDialog datePickerDialog;
    long timeInMilliseconds;
    Dialog dialog;
    String modetravel = "driving";
    Double latitude, longtiude;
    public static String geolocation;
    int REQUEST_INVITE = 1;

    ArrayList<String> myContacts = new ArrayList<String>();
    public static ArrayList<String> invitedContacts = new ArrayList<String>();
    ArrayList<String> recipients = new ArrayList<>();

    String user_id, allDay;
    FloatingActionButton save;
    ArrayList<String> myConflictEvents = new ArrayList<String>();
    ArrayList<String> myCurrentEvent = new ArrayList<>();
    private CoordinatorLayout coordinatorLayout;
    String eventID;
    private PendingIntent pendingIntent;

    String prevName = "", prevLat = "", prevLng = "", prevLoc = "", prevStartDate = "", prevEndDate = "",
            prevStartTime = "", prevEndTime = "";
    String nextName = "", nextLat = "", nextLng = "", nextLoc = "", nextStartDate = "", nextEndDate = "",
            nextStartTime = "", nextEndTime = "";
    long diffInMinutesPrev, diffInMinutesNext;
    String diffPrev, diffNext;
    double latA, lngA, latB, lngB;
    String[] listid;
    String[] target;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);
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
        editEname = (EditText) findViewById(R.id.eName);
        editEname.setTypeface(custom_font);
        editDescription = (EditText) findViewById(R.id.desc);
        editDescription.setTypeface(custom_font);
        editSdate = (EditText) findViewById(R.id.sDate);
        editSdate.setTypeface(custom_font);
        editSdate.setInputType(InputType.TYPE_NULL);
        editEdate = (EditText) findViewById(R.id.eDate);
        editEdate.setTypeface(custom_font);
        editEdate.setInputType(InputType.TYPE_NULL);
        editStime = (EditText) findViewById(R.id.sTime);
        editStime.setTypeface(custom_font);
        editStime.setInputType(InputType.TYPE_NULL);
        editEtime = (EditText) findViewById(R.id.eTime);
        editEtime.setTypeface(custom_font);
        editEtime.setInputType(InputType.TYPE_NULL);
        allday = (CheckBox) findViewById(R.id.checkAllDay);
        allday.setTypeface(custom_font);
        editLoc = (EditText) findViewById(R.id.loc);
        editLoc.setTypeface(custom_font);
        editFriends = (EditText) findViewById(R.id.friends);
        editFriends.setTypeface(custom_font);
        buttonAddFriends = (Button) findViewById(R.id.addFriends);
        buttonAddFriends.setTypeface(custom_font);
        save = (FloatingActionButton) findViewById(R.id.fabSave);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinator_layout);
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

                new getDetails().execute();

            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });


        datePickerDialog = new DatePickerDialog(createEvent.this,
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
        AlertDialog.Builder builder = new AlertDialog.Builder(createEvent.this)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        for (int i = 0; i < invitedContacts.size(); i++) {

                            list = list + invitedContacts.get(i) + "\n";
                        }
                        editFriends.setText(list);
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

        ListView modeList = new ListView(createEvent.this);
        //modeList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        //modeList.setItemsCanFocus(false);
        // String[] stringArray = new String[] { "Bright Mode", "Normal Mode" };


        Cursor c = LandingActivity.db.getContacts();

        while (c.moveToNext()) {
            myContacts.add(c.getString(c.getColumnIndex(DBInfo.DataInfo.CONTACT_NAME)));
            Log.i("Event contacts", c.getString(c.getColumnIndex(DBInfo.DataInfo.CONTACT_EMAIL)));//this adds an element to the list.
        }

        ArrayAdapter<String> modeAdapter = new ArrayAdapter<String>(createEvent.this, android.R.layout.simple_list_item_1, android.R.id.text1, myContacts);
        modeList.setAdapter(modeAdapter);

        builder.setView(modeList);

        dialog = builder.create();


        modeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // When clicked, show a toast with the TextView text
                //Toast.makeText(getContext(), ((TextView) view).getText(),
                //        Toast.LENGTH_SHORT).show();

                int flag = 0;
                for (int i = 0; i < invitedContacts.size(); i++) {
                    if (invitedContacts.get(i).equals(((TextView) view).getText().toString()))//checking if naay duplicate.
                        flag = 1;
                }
                if (flag == 0) {
                    invitedContacts.add(((TextView) view).getText().toString());
                }
            }
        });

//
//        modeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            public void onItemClick(AdapterView<?> parent, View view,
//                                    int position, long id) {
//                // When clicked, show a toast with the TextView text
//                //Toast.makeText(getContext(), ((TextView) view).getText(),
//                //        Toast.LENGTH_SHORT).show();
//
//                int flag = 0;
//                for (int i = 0; i < invitedContacts.size(); i++) {
//                    if (invitedContacts.get(i).equals(((TextView) view).getText().toString()))
//                        flag = 1;
//                }
//                if (flag == 0) {
//                    invitedContacts.add(((TextView) view).getText().toString());
//                }
//            }
//        });


    }

    @Override
    public void onClick(View view) {

        if (view == editSdate) {
            final Calendar c = Calendar.getInstance();
            sYear = c.get(Calendar.YEAR);
            sMonth = c.get(Calendar.MONTH);
            sDay = c.get(Calendar.DAY_OF_MONTH);

            datePickerDialog.show();
        }
        if (view == editEdate) {
            final Calendar c = Calendar.getInstance();
            eYear = c.get(Calendar.YEAR);
            eMonth = c.get(Calendar.MONTH);
            eDay = c.get(Calendar.DAY_OF_MONTH);
            String datetime;
            //i'll just put a static starttime kay para mmugana ang validation whether ni
            if (editSdate.getText() == null) {
                editEdate.setClickable(false);
            } else {
                datetime = editSdate.getText().toString() + " 00:00";

                //    datetime = dateStart.getText().toString() + " " + timeStart.getText().toString();

                SimpleDateFormat sdf;
                try {

                    Log.i("Event dt", datetime);
                    sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());

                    Date mDate = sdf.parse(datetime);
                    String strdate = sdf.format(mDate);
                    Log.i("Event String", strdate);
                    Log.i("Event mDate", "" + mDate);
                    timeInMilliseconds = mDate.getTime();
                    Log.i("Date in milli :: ", "" + timeInMilliseconds);
                } catch (ParseException e) {
                    e.printStackTrace();
                }


                DatePickerDialog datePickerDialog = new DatePickerDialog(createEvent.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {

                                String ed = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;

                                SimpleDateFormat sdf;
                                try {


                                    sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

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


        if (allday.isChecked()) {

            allDay = "All Day";
        } else {
            allDay = "Daily";
        }


        if (view == editStime) {

            final Calendar cal = Calendar.getInstance();
            sHour = cal.get(Calendar.HOUR_OF_DAY);
            sMinute = cal.get(Calendar.MINUTE);

            long start = cal.getTimeInMillis();
            Log.i("Event start", "" + start);
            if (editStime.getText() == null) {
                editEtime.setEnabled(false);
            } else {
                // Launch Time Picker Dialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(createEvent.this,
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {


                                String st = hourOfDay + ":" + minute;

                                SimpleDateFormat sdf;
                                try {


                                    sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());

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

        }
        if (view == editEtime) {

            // Get Current Time

            final Calendar c = Calendar.getInstance();
            eHour = c.get(Calendar.HOUR_OF_DAY);
            eMinute = c.get(Calendar.MINUTE);


            // Launch Time Picker Dialog
            TimePickerDialog timePickerDialog = new TimePickerDialog(createEvent.this,
                    new TimePickerDialog.OnTimeSetListener() {

                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay,
                                              int minute) {

//
//                            if(cal.getTimeInMillis() > c.getTimeInMillis()) {
//
//                                Toast.makeText(getContext(), "Can't add time", Toast.LENGTH_SHORT).show();
//                            }else {
//                                timeEnd.setText(hourOfDay + ":" + minute);
//                            }

                            //  timeEnd.setText(hourOfDay + ":" + minute);

                            String st = hourOfDay + ":" + minute;

                            SimpleDateFormat sdf;
                            try {

                                sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());

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


        if (view == buttonAddFriends) {
            dialog.show();

        }

        if (view == save) {

            Random r = new Random();
            long unixtime = (long) (1293861599 + r.nextDouble() * 60 * 60 * 24 * 365);

            String unique_id = String.valueOf(unixtime);

            Log.d("Karen:", unique_id);

            String name = editEname.getText().toString();
            String eventDescription = editDescription.getText().toString();
            String eventLocation = editLoc.getText().toString();
            String lat = " " + latitude;
            String lng = " " + longtiude;
            String startDate = editSdate.getText().toString();
            String startTime = editStime.getText().toString();
            String endDate = editEdate.getText().toString();
            String endTime = editEtime.getText().toString();
            String eventAllDay = allDay;


            String invitedlist = editFriends.getText().toString();

            String[] separated = invitedlist.split("\n");
            Cursor b = LandingActivity.db.getContacts();
            ArrayList<String> invitesid = new ArrayList<String>();

//            ArrayList<String> invitesemail = new ArrayList<String>();

            String invitesemail = "";

            String[] listid = new String[separated.length];

            String listinvitesid="";

            while (b.moveToNext()){
                String contactsname = b.getString(b.getColumnIndex("contact_name"));
                String contactsid = b.getString(b.getColumnIndex("contact_user_id"));
                String contactsemail = b.getString(b.getColumnIndex("contact_email"));

                int i;

                for (i=0; i<separated.length; i++){

                    if (contactsname.equals(separated[i])){

                        listid[i] = contactsid;
                        listinvitesid = listinvitesid + contactsname + " (Pending)\n";
                        invitesid.add(contactsid);
//                        invitesemail.add(contactsemail);

                        invitesemail = invitesemail + contactsemail.replace("@gmail.com","").trim() + "\n";
                    }

                }

            }

            String invitedids = invitesid.toString();


            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mcontext);
            String sharedPrefUserId = sharedPreferences.getString("user_id", null);



            if (name.isEmpty() || eventLocation.isEmpty() || eventDescription.isEmpty() ||
                    startDate.isEmpty() || endDate.isEmpty() || startTime.isEmpty() || endTime.isEmpty()) {

                if (name.isEmpty()) {
                    editEname.setError("Enter event name");
                }
                if (eventDescription.isEmpty()) {
                    editDescription.setError("Enter description");
                }
                if (eventLocation.isEmpty()) {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(mcontext);
                    builder1.setMessage("Enter Location");
                    builder1.setCancelable(true);
                    builder1.setNeutralButton(
                            "Okay",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });


                    AlertDialog alert11 = builder1.create();
                    alert11.show();
                }
                if (startDate.isEmpty()) {
                    editSdate.setError("Enter start date");
                }
                if (endDate.isEmpty()) {
                    editEdate.setError("Enter end date");
                }
                if (startTime.isEmpty()) {
                    editStime.setError("Enter start time");
                }
                if (endTime.isEmpty()) {
                    editEtime.setError("Enter end time");
                }
            } else {
                StringTokenizer sd = new StringTokenizer(startDate, "-");
                StringTokenizer ed = new StringTokenizer(endDate, "-");
                StringTokenizer st = new StringTokenizer(startTime, ":");
                StringTokenizer et = new StringTokenizer(endTime, ":");

                String sdate = sd.nextToken() + sd.nextToken() + sd.nextToken();
                String edate = ed.nextToken() + ed.nextToken() + ed.nextToken();
                String stime = st.nextToken() + st.nextToken();
                String etime = et.nextToken() + et.nextToken();

                int sdd = Integer.parseInt(sdate);
                int edd = Integer.parseInt(edate);
                int stt = Integer.parseInt(stime);
                int ett = Integer.parseInt(etime);


                //Code for alarm

                int count;
        DataBaseHandler db  = new DataBaseHandler(this);
        Cursor c = db.getMaxId();
        c.moveToFirst();
        if (c.getCount() == 0) {
            count = 1;
        } else {
            count = Integer.parseInt(c.getString(0));
            count += 1;
        }

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
        myIntent.putExtra("people", recipients);
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

                eventAllDay = allDay;
                Log.i("Event allDay", eventAllDay);
                Log.i("Event name", name);
                Log.i("Event coord", lat + "4wz," + lng);
                Log.i("Event loc: ", eventLocation);
                Log.i("Event SD", startDate);
                Log.i("Event ED: ", endDate);
                Log.i("Event ST: ", startTime);
                Log.i("Event ET: ", endTime);
                Log.i("Event email: ", useremail);


                Cursor all = LandingActivity.db.getids();
                Log.i("Event all count", "" + all.getCount());

                OkHttp.getInstance(getBaseContext()).saveEvent(unique_id, sharedPrefUserId, name, eventDescription, eventLocation, lng,  lat, startDate, startTime, endDate, endTime, eventAllDay, listid);

                LandingActivity.db.saveEvent(Integer.valueOf(sharedPrefUserId), unique_id, name, eventDescription,
                        eventLocation, lng, lat, startDate, startTime, endDate, endTime, eventAllDay, "Creator", listinvitesid);


                Log.d("MyData1: ", sharedPrefUserId);
                Log.d("MyData2: ", unique_id);
                Log.d("MyData3: ", name);
                Log.d("MyData4: ", eventDescription);
                Log.d("MyData5: ", listinvitesid);

               String[] target = invitesemail.split("\n");

                for (int i=0; i<target.length; i++){
                    //codes for sending notif
                    OkHttp.getInstance(mcontext).sendNotification("Invitation", sharedPrefUserId, unique_id, name,
                            eventDescription, eventLocation, startDate, startTime, endDate, endTime, target[i]+ "Velma",
                            lat, lng, LandingActivity.useremail, listinvitesid);//eachemail[0]
                    Log.d("Emails:", target[i]);
                    Log.d("Emails without @: ",  target[0]);
                }

                Intent i = new Intent(createEvent.this, LandingActivity.class);
                finish();
                startActivity(i);


            }
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




    public String getPreviousLocation(final Cursor prev, final String name, final String eventLocation, final
    String startDate, final String startTime, final String endDate, final String endTime, final String lat,
                                      final String lng) {
        String disdur = "";
        Log.i("Event locA", "1");

        if ((prev != null && prev.getCount() > 0)) {
            prev.moveToFirst();
            prevName = prev.getString(prev.getColumnIndex("event_name"));
            prevLoc = prev.getString(prev.getColumnIndex("event_location"));
            prevLat = prev.getString(prev.getColumnIndex("latitude"));
            prevLng = prev.getString(prev.getColumnIndex("longitude"));
            prevStartDate = prev.getString(prev.getColumnIndex("start_date"));
            prevEndDate = prev.getString(prev.getColumnIndex("end_date"));
            prevStartTime = prev.getString(prev.getColumnIndex("start_time"));
            prevEndTime = prev.getString(prev.getColumnIndex("end_time"));

            Log.i("Event laaa", "im in tho2");
            String prevDT = prevStartDate + " " + prevEndTime;
            String cur_A = startDate + " " + startTime;
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm",
                    java.util.Locale.getDefault());

            try {
                Date locADT = simpleDateFormat.parse(prevDT);
                Date curA = simpleDateFormat.parse(cur_A);

                long durationA = curA.getTime() - locADT.getTime();
                diffInMinutesPrev = TimeUnit.MILLISECONDS.toMinutes(durationA);


                System.out.println("locSDT : " + simpleDateFormat.format(locADT));
                System.out.println("curA : " + simpleDateFormat.format(curA));
                System.out.println("diff:" + diffInMinutesPrev);
                Log.i("diffInMins :", "" + diffInMinutesPrev);
                diffPrev = "" + diffInMinutesPrev;
                Log.i("Event A Diff", diffPrev);

                String LL_Prev = prevLat + "," + prevLng;
                Log.i("EVENTPrevSTRING", LL_Prev);
                Log.i("Event laaa", "im in tho3");


                Log.i("Event laaa", "im in tho5");
                // new getLocationDetails().execute(latA, lngA);
                disdur = getjjson(LL_Prev);
                Log.i("json method: ", disdur);


            } catch (ParseException ex) {
                System.out.println("Exception " + ex);
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.i("Event after", "wa");
            Log.i("Event before", prevName + ": " + prevLoc);
        }

        return disdur;

    }

    public String getNextLocation(final Cursor next, final String name, final String eventLocation, final
    String startDate, final String startTime, final String endDate, final String endTime, final String lat,
                                  final String lng) {
        Log.i("Event locA", "1");
        String disdur = "";

        next.moveToFirst();
        nextName = next.getString(next.getColumnIndex("event_name"));
        nextLoc = next.getString(next.getColumnIndex("event_location"));
        nextLat = next.getString(next.getColumnIndex("latitude"));
        nextLng = next.getString(next.getColumnIndex("longitude"));
        nextStartDate = next.getString(next.getColumnIndex("start_date"));
        nextEndDate = next.getString(next.getColumnIndex("end_date"));
        nextStartTime = next.getString(next.getColumnIndex("start_time"));
        nextEndTime = next.getString(next.getColumnIndex("end_time"));
        Log.i("Event after", nextName + ": " + nextLoc);

        Log.i("Event laaa", "im in tho1");
        String locB_DT = nextStartDate + " " + nextStartTime;
        String cur_B = startDate + " " + endTime;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm",
                java.util.Locale.getDefault());


        try {
            Date locBDT = simpleDateFormat.parse(locB_DT);
            Date curB = simpleDateFormat.parse(cur_B);


            long durationB = locBDT.getTime() - curB.getTime();
            diffInMinutesNext = TimeUnit.MILLISECONDS.toMinutes(durationB);


            System.out.println("locSDT : " + simpleDateFormat.format(locBDT));
            System.out.println("curB : " + simpleDateFormat.format(curB));
            System.out.println("diff:" + diffInMinutesNext);

            Log.i("diffInMins :", "" + diffInMinutesNext);
            diffNext = "" + diffInMinutesNext;
            Log.i("Event B DIff", diffNext);


            String llB = nextLat + "," + nextLng;
            Log.i("EVENT B STRING", "" + llB);

            Log.i("Event laaa", "im in thoB4");
            disdur = getjjson(llB);
            Log.i("json method: ", disdur);


        } catch (ParseException ex) {
            System.out.println("Exception " + ex);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        return disdur;

    }

    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    public String getjjson(String latlng) throws IOException, JSONException {
        Log.i("Event latlngLOG", "here");

        Log.i("Event latlngLOG", latlng);

        String text = null;

        String regAPIURL = "https://maps.googleapis.com/maps/api/distancematrix/json?units=metric&origins=" + latlng + "&destinations=" + latitude + "," + longtiude +
                "&mode=driving&key=AIzaSyDWjoAbJf9uDrLCFAM_fCSWxP0muVEGbOA";

        JSONObject js = readJsonFromUrl(regAPIURL);
        text = js.toString();
        Log.i("getjjson: text", text);
        String s = jason(text);
        Log.i("getjjson: s", s);
        return s;

    }


    public static JSONObject readJsonFromUrl(String regAPIURL) throws IOException, JSONException {

        InputStream is = new URL(regAPIURL).openStream();
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName
                    ("UTF-8")));
            String jsonText = readAll(rd);
            JSONObject json = new JSONObject(jsonText);
            return json;
        } finally {
            is.close();
        }
    }

    public String jason(String s) {
        String durationplusdistance = "";
        if (s != null) {


            try {


                String distance = new JSONObject(s)
                        .getJSONArray("rows")
                        .getJSONObject(0)
                        .getJSONArray("elements")
                        .getJSONObject(0)
                        .getJSONObject("distance").getString("text");

                String durationinmin = new JSONObject(s)
                        .getJSONArray("rows")
                        .getJSONObject(0)
                        .getJSONArray("elements")
                        .getJSONObject(0)
                        .getJSONObject("duration").getString("value");

                String duration = new JSONObject(s)
                        .getJSONArray("rows")
                        .getJSONObject(0)
                        .getJSONArray("elements")
                        .getJSONObject(0)
                        .getJSONObject("duration").getString("text");
                Log.i("ResurltLOGDUR: ", duration);


                durationplusdistance = distance + "^" + duration + "^" + durationinmin;

                Log.i("ResurltLOGDUR: ", duration);


//
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return durationplusdistance;
    }


}
