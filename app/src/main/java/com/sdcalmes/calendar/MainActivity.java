package com.sdcalmes.calendar;

import android.app.FragmentManager;
import android.app.ListActivity;
import android.bluetooth.BluetoothHealthAppConfiguration;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private CalendarView mCalendar;
    final ArrayList<Event> events = new ArrayList<Event>();
    final ArrayList<String> eventTitles = new ArrayList<String>();
    private ArrayAdapter<String> eventTitleAdapter;
    private ArrayAdapter<Event> eventAdapter;


    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences mPrefs = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        //prefsEditor.clear().commit();
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

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Event obj = (Event)eventAdapter.getItem(position);
                String value = obj.getTitle();
                Snackbar.make(view, "You clicked on " + value, Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
        Date selDate = new Date(mCalendar.getDate());
        Event e1 = new Event(title, description, selDate, hour, minute);
        String json = gson.toJson(e1);
        prefsEditor.putString(eventName, json);
        prefsEditor.commit();
        updateEvents();


    }

    public void updateEvents(){
        System.out.println("UPDATING EVENTS");
        eventTitles.clear();
        events.clear();
        Gson gson = new Gson();
        SharedPreferences mPrefs = getPreferences(MODE_PRIVATE);
        Map<String, ?> allEntries = mPrefs.getAll();
        for(Map.Entry<String, ?> entry: allEntries.entrySet()) {
            String json = mPrefs.getString(entry.getKey(), "");
            Event e1 = gson.fromJson(json, Event.class);
            events.add(e1);
        }
        Collections.sort(events, new CustomComparator());
       // Collections.reverse(events);
        System.out.println(events.size());
        for(int i = 0; i < events.size(); i++){
            System.out.println(events.get(i).getTitle());
            eventTitles.add(events.get(i).getTitle());
        }
        eventTitleAdapter.notifyDataSetChanged();
    }

    public void removeAllEvents(MenuItem item){
        SharedPreferences mPrefs = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        prefsEditor.clear().commit();
        updateEvents();

    }

}

