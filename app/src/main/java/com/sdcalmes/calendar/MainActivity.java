package com.sdcalmes.calendar;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.ListActivity;
import android.bluetooth.BluetoothHealthAppConfiguration;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import android.widget.ListView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.gson.Gson;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    final Context context = this;
    private CalendarView mCalendar;
    final ArrayList<Event> events = new ArrayList<Event>();
    final ArrayList<String> eventTitles = new ArrayList<String>();
    private ArrayAdapter<String> eventTitleAdapter;
    private ArrayAdapter<Event> eventAdapter;
    final Calendar cal = Calendar.getInstance();
    private Date selDate = new Date();
    private boolean showAll = false;

    private MenuItem showAllMenuItem;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences mPrefs = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
       // prefsEditor.clear().commit();
        eventAdapter = new ArrayAdapter<Event>(this, android.R.layout.simple_list_item_1, events);
        eventTitleAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, eventTitles);
        listView = (ListView) findViewById(android.R.id.list);
        listView.setAdapter(eventTitleAdapter);
        updateEvents();
        mCalendar = (CalendarView) findViewById(R.id.calendarView);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddDialog();

            }
        });

        mCalendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                cal.set(year, month, dayOfMonth);
                long selectedDateInMillis = cal.getTimeInMillis();

                selDate = new Date(selectedDateInMillis);
                updateEvents();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Event obj = (Event) eventAdapter.getItem(position);
                cal.setTime(obj.getDate());
                String title = obj.getTitle();
                String month = new DateFormatSymbols().getMonths()[cal.get(Calendar.MONTH) - 1];
                int day = cal.get(Calendar.DAY_OF_MONTH);
                int hour = obj.getHour();
                int minute = obj.getMinute();
                String minuteString = Integer.toString(minute);
                if (minute < 10) {
                    minuteString = "0" + minute;
                }
                String descrip = obj.getDescription();

                Snackbar.make(view, title + " at " + hour + ":" + minuteString + " on " +
                        month + " " + day + ".\n" + descrip, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final Event eventObj = (Event) eventAdapter.getItem(position);

                String msg = "Are you sure you want to delete this event?";
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                alertDialogBuilder.setMessage(msg);
                alertDialogBuilder.setCancelable(true);
                alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //if this button is clicked, remove event from SharedPrefs
                        Gson gson = new Gson();
                        SharedPreferences mPrefs = getPreferences(MODE_PRIVATE);
                        SharedPreferences.Editor prefsEditor = mPrefs.edit();
                        Map<String, ?> allEntries = mPrefs.getAll();
                        for(Map.Entry<String, ?> entry: allEntries.entrySet()) {
                            String json = mPrefs.getString(entry.getKey(), "");
                            Event e1 = gson.fromJson(json, Event.class);

                            if(e1.getTitle().equals(eventObj.getTitle()) &&
                                    e1.getDescription().equals(eventObj.getDescription())){
                                prefsEditor.remove(entry.getKey());
                                prefsEditor.commit();
                                updateEvents();
                            }
                        }
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //if cancel, close dialog
                        dialog.cancel();
                    }
                });
                alertDialogBuilder.show();

                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        showAllMenuItem = (MenuItem)menu.findItem(R.id.show_all_events);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showAddDialog() {
        FragmentManager fm = getFragmentManager();
        final AddDialogFragment addDialogFragment = new AddDialogFragment();
        addDialogFragment.show(fm, "fragment_add_dialog");
    }

    public void getData(String title, String description, int hour, int minute) {
        SharedPreferences mPrefs = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        Map<String, ?> allEntries = mPrefs.getAll();

        int entrySize = allEntries.size();
        String eventName = "Event" + entrySize;
        Gson gson = new Gson();
        //Date selDate = new Date(mCalendar.getDate());
        Event e1 = new Event(title, description, selDate, hour, minute);
        String json = gson.toJson(e1);
        prefsEditor.putString(eventName, json);
        prefsEditor.commit();
        updateEvents();


    }

    public void updateEvents(){
        String pattern = "MMM dd";
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        eventTitles.clear();
        events.clear();
        Gson gson = new Gson();
        SharedPreferences mPrefs = getPreferences(MODE_PRIVATE);
        Map<String, ?> allEntries = mPrefs.getAll();
        String selDayMonth = sdf.format(selDate);
        for(Map.Entry<String, ?> entry: allEntries.entrySet()) {
            String json = mPrefs.getString(entry.getKey(), "");
            Event e1 = gson.fromJson(json, Event.class);
            String date = sdf.format(e1.getDate());
            if(!showAll) {
                if (date.equals(selDayMonth)) {
                    events.add(e1);
                }
            } else{
                events.add(e1);
            }
        }
        Collections.sort(events, new CustomComparator());

        //Collections.reverse(events);
        for(int i = 0; i < events.size(); i++){

            String date = sdf.format(events.get(i).getDate());
            eventTitles.add(events.get(i).getTitle() + " on " + date);
        }
        eventTitleAdapter.notifyDataSetChanged();
    }

    public void removeAllEvents(MenuItem item){
        SharedPreferences mPrefs = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        prefsEditor.clear().commit();
        updateEvents();

    }

    public void showAllEvents(MenuItem item){
        showAll = !showAll;
        showAllMenuItem.setChecked(showAll);
        updateEvents();
    }

}

