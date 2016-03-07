package com.sdcalmes.calendar;

import android.app.FragmentManager;
import android.app.ListActivity;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
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
import java.util.Date;
import java.util.Map;
import java.util.Random;

public class MainActivity extends ListActivity {

    private CalendarView mCalendar;
    final ArrayList<Event> events = new ArrayList<Event>();


    private ListView listView;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences mPrefs = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
       // prefsEditor.clear().commit();
        ArrayAdapter<Event> eventAdapter = new ArrayAdapter<Event>(this, android.R.layout.simple_list_item_1, events);
        listView = (ListView) findViewById(android.R.id.list);
        listView.setAdapter(eventAdapter);
        updateEvents();
        eventAdapter.notifyDataSetChanged();
        mCalendar = (CalendarView) findViewById(R.id.calendarView);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddDialog();
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Event obj = (Event)listView.getAdapter().getItem(position);
                String value = obj.getTitle();
                System.out.println(value);
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
        System.out.println("Entries: " + entrySize);
        Gson gson = new Gson();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date selDate = new Date(mCalendar.getDate());
        String selectedDate = sdf.format(new Date(mCalendar.getDate()));
        Event e1 = new Event(title, description, selDate, hour, minute);
        String json = gson.toJson(e1);
        prefsEditor.putString(eventName, json);
        prefsEditor.commit();
        Map<String, ?> allEntries2 = mPrefs.getAll();

        updateEvents();

    }

    public void updateEvents(){
        Gson gson = new Gson();
        SharedPreferences mPrefs = getPreferences(MODE_PRIVATE);
        Map<String, ?> allEntries = mPrefs.getAll();
        for(Map.Entry<String, ?> entry: allEntries.entrySet()) {
            String json = mPrefs.getString(entry.getKey(), "");
            Event e1 = gson.fromJson(json, Event.class);
            events.add(e1);
        }
    }

}
