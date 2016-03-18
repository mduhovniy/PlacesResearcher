package info.duhovniy.maxim.placesresearcher.ui;

import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import info.duhovniy.maxim.placesresearcher.R;
import info.duhovniy.maxim.placesresearcher.network.NetworkConstants;
import info.duhovniy.maxim.placesresearcher.network.search.SearchServiceText;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private MyMapFragment mapFragment;
    private ControlFragment controlFragment;

    private PlaceSearchReceiver placeSearchReceiver;
    private PowerConnectionReceiver powerConnectionReceiver;

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

//        setUpSpinner();

        checkGPS();

        mapFragment = MyMapFragment.getInstance();
        controlFragment = ControlFragment.getInstance();

        FragmentManager fragmentManager = getSupportFragmentManager();

        fragmentManager.beginTransaction().replace(R.id.control_fragment_container,
                controlFragment, UIConstants.CONTROL_FRAGMENT).commit();

        if (findViewById(R.id.map_fragment_container) != null) {
            fragmentManager.beginTransaction().replace(R.id.map_fragment_container,
                    mapFragment, UIConstants.MAP_FRAGMENT).commit();
        }

        // Get the intent, verify the action and get the query
        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);

        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            doMySearch(query);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    private PlaceSearchReceiver getRegisteredReceiver() {

        IntentFilter filter = new IntentFilter();

        filter.addCategory(Intent.CATEGORY_DEFAULT);
        filter.addAction(NetworkConstants.NEARBY_SEARCH);
        filter.addAction(NetworkConstants.TEXT_SEARCH);

        PlaceSearchReceiver r = new PlaceSearchReceiver();
        registerReceiver(r, filter);

        return r;
    }

    private PowerConnectionReceiver getPowerConnectionReceiver() {

        IntentFilter filter = new IntentFilter();

        filter.addAction(Intent.ACTION_POWER_CONNECTED);
        filter.addAction(Intent.ACTION_POWER_DISCONNECTED);

        PowerConnectionReceiver r = new PowerConnectionReceiver();
        registerReceiver(r, filter);

        return r;
    }

    @Override
    protected void onResume() {
        super.onResume();

        placeSearchReceiver = getRegisteredReceiver();
        powerConnectionReceiver = getPowerConnectionReceiver();
    }

    @Override
    protected void onPause() {
        unregisterReceiver(powerConnectionReceiver);
        unregisterReceiver(placeSearchReceiver);

        super.onPause();
    }

    public void doMySearch(String query) {

        Intent intent = new Intent();
        intent.putExtra(NetworkConstants.REQUEST_STRING, query);

        intent.setClass(MainActivity.this, SearchServiceText.class);
        startService(intent);
    }

    public void setUpSpinner() {
        // Spinner element
        Spinner spinner = (Spinner) findViewById(R.id.spinner_place_types);

        // Spinner click listener
        spinner.setOnItemSelectedListener(this);

        // Spinner Drop down elements
        String[] categories = getResources().getStringArray(R.array.place_header);

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // On selecting a spinner item
        String item = getResources().getStringArray(R.array.place_type)[position];

        // Showing selected spinner item
        Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        Toast.makeText(parent.getContext(), "Selected: NOTHING", Toast.LENGTH_LONG).show();
    }

    private void checkGPS() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
        }
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.GPS_check_response))
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog,
                                        @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    private class PlaceSearchReceiver extends BroadcastReceiver {

        public PlaceSearchReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {

            if(intent.hasExtra(NetworkConstants.RESPONSE_MESSAGE))
                Toast.makeText(getApplicationContext(), "Found "
                        + intent.getIntExtra(NetworkConstants.RESPONSE_MESSAGE, -1)
                        + " places!", Toast.LENGTH_SHORT).show();
            if (controlFragment != null) {
                controlFragment.onResultListChange();
            }
/*
            switch (intent.getAction()) {
                case NetworkConstants.AUTOCOMPLETE_SEARCH:
                    //TODO:
                    break;
                case NetworkConstants.TEXT_SEARCH:
                    //TODO:
                    break;
                case NetworkConstants.RADAR_SEARCH:
                    //TODO:
                    break;
                case NetworkConstants.NEARBY_SEARCH:
                    //TODO:
                    break;
            }
*/
        }
    }
}
