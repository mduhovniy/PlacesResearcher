package info.duhovniy.maxim.placesresearcher.ui;

import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
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
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.OnStreetViewPanoramaReadyCallback;
import com.google.android.gms.maps.StreetViewPanorama;
import com.google.android.gms.maps.SupportStreetViewPanoramaFragment;
import com.google.android.gms.maps.model.LatLng;

import info.duhovniy.maxim.placesresearcher.R;
import info.duhovniy.maxim.placesresearcher.network.NetworkConstants;
import info.duhovniy.maxim.placesresearcher.network.Place;
import info.duhovniy.maxim.placesresearcher.network.search.SearchServiceNearby;
import info.duhovniy.maxim.placesresearcher.network.search.SearchServiceText;
import info.duhovniy.maxim.placesresearcher.ui.map.LocationProvider;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener,
        ControlFragment.ControlInterface, LocationProvider.LocationCallback,
        OnStreetViewPanoramaReadyCallback {

    private MyMapFragment mapFragment;
    private ControlFragment controlFragment;
    private FragmentManager fragmentManager;

    private DrawerLayout drawerLayout;
    private NavigationView navView;
    private View headerView;

    private PlaceSearchReceiver placeSearchReceiver;
    private PowerConnectionReceiver powerConnectionReceiver;

    private String searchType;
    private Switch searchSwitch;
    private Location mLocation;
    private LatLng mLatLng;
    private LocationProvider mLocationProvider;

    // becomes true if Favorite List is visible
    private boolean isFavorite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Check whether we're recreating a previously destroyed instance
        // Restore value of members from saved state
        isFavorite = savedInstanceState != null && savedInstanceState.getBoolean(UIConstants.FAVORITE_FLAG);

        setContentView(R.layout.activity_main);
        mLocationProvider = new LocationProvider(this, this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(null);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayShowTitleEnabled(false);

        searchSwitch = (Switch) findViewById(R.id.switch_local);

        setUpSpinner();

        checkGPS();

        mapFragment = MyMapFragment.getInstance();
        controlFragment = ControlFragment.getInstance();

        fragmentManager = getSupportFragmentManager();

        fragmentManager.beginTransaction().replace(R.id.control_fragment_container,
                controlFragment, UIConstants.CONTROL_FRAGMENT).commit();
        controlFragment.setPlaceListener(this);

        if (findViewById(R.id.map_fragment_container) != null) {
            fragmentManager.beginTransaction().replace(R.id.map_fragment_container,
                    mapFragment, UIConstants.MAP_FRAGMENT).commit();
        }

        if (mapFragment.isVisible()) {
            mapFragment.setUpCluster();
        }

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);

        navView = (NavigationView) findViewById(R.id.navigation_view);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);

        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            if (searchSwitch.isChecked()) {
                if (mLocation != null)
                    doMySearch(query, searchType);
                else
                    Snackbar.make(drawerLayout, "Location has not yet been found!", Snackbar.LENGTH_LONG).show();
            } else
                doMySearch(query);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(UIConstants.FAVORITE_FLAG, isFavorite);
        super.onSaveInstanceState(outState);
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

        MenuItem item = menu.findItem(R.id.action_faivorite);
        if (isFavorite) {
            item.setIcon(android.R.drawable.btn_star_big_on);
        } else {
            item.setIcon(android.R.drawable.btn_star_big_off);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_faivorite) {
            if (isFavorite) {
                isFavorite = false;
                item.setIcon(android.R.drawable.btn_star_big_off);
            } else {
                isFavorite = true;
                item.setIcon(android.R.drawable.btn_star_big_on);
            }
        }
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
        mLocationProvider.connect();
    }

    @Override
    protected void onPause() {
        unregisterReceiver(powerConnectionReceiver);
        unregisterReceiver(placeSearchReceiver);
        mLocationProvider.disconnect();

        super.onPause();
    }

    // text search service running
    public void doMySearch(String query) {

        Intent intent = new Intent();
        intent.putExtra(NetworkConstants.REQUEST_STRING, query);

        intent.setClass(MainActivity.this, SearchServiceText.class);
        startService(intent);
    }

    // nearby search service running
    public void doMySearch(String query, String type) {

        Intent intent = new Intent();
        intent.putExtra(NetworkConstants.REQUEST_STRING, query);
        intent.putExtra(NetworkConstants.REQUEST_TYPE, type);
        intent.putExtra(NetworkConstants.REQUEST_LAT, mLocation.getLatitude());
        intent.putExtra(NetworkConstants.REQUEST_LNG, mLocation.getLongitude());

        intent.setClass(MainActivity.this, SearchServiceNearby.class);
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

    // search type spin listener
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // On selecting a spinner item
        searchType = getResources().getStringArray(R.array.place_type)[position];

        // Showing selected spinner item
        // Toast.makeText(parent.getContext(), "Selected: " + searchType, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Toast.makeText(parent.getContext(), "Selected: NOTHING", Toast.LENGTH_LONG).show();
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

    @Override
    public void onPlaceListener(Place place) {
        if (!mapFragment.isVisible() && findViewById(R.id.map_fragment_container) == null)
            getSupportFragmentManager().beginTransaction().replace(R.id.control_fragment_container,
                    mapFragment, UIConstants.CONTROL_FRAGMENT).addToBackStack(null).commit();
        mapFragment.showPlace(place);
        if (navView != null) {
            setupDrawerContent(navView, place.getPlaceLocation(), place.getPlaceName());
        }
    }

    @Override
    public void handleNewLocation(Location location) {
        if (mLocation == null)
            Snackbar.make(drawerLayout, "Your location is found!", Snackbar.LENGTH_LONG).show();
        mLocation = location;
    }

    private class PlaceSearchReceiver extends BroadcastReceiver {

        public PlaceSearchReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.hasExtra(NetworkConstants.RESPONSE_MESSAGE))
                Toast.makeText(context, "Found "
                        + intent.getIntExtra(NetworkConstants.RESPONSE_MESSAGE, -1)
                        + " places!", Toast.LENGTH_SHORT).show();
            if (controlFragment != null) {
                controlFragment.onResultListChange();
            }

            if (mapFragment.isVisible()) {
                mapFragment.setUpCluster();
            }
        }
    }

    @Override
    public void onStreetViewPanoramaReady(StreetViewPanorama streetViewPanorama) {
        streetViewPanorama.setPosition(mLatLng);
    }

    private void setupDrawerContent(NavigationView navigationView, LatLng latLng, String placeName) {

        mLatLng = latLng;

        if (headerView == null)
            headerView = navigationView.inflateHeaderView(R.layout.drawer_header);

        TextView headerPlaceName = (TextView) navigationView.findViewById(R.id.street_view_place_name);
        headerPlaceName.setText(placeName);

        SupportStreetViewPanoramaFragment streetViewPanoramaFragment =
                (SupportStreetViewPanoramaFragment) fragmentManager
                        .findFragmentById(R.id.street_view_panorama);
        streetViewPanoramaFragment.getStreetViewPanoramaAsync(this);


        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                menuItem.setChecked(true);

                switch (menuItem.getItemId()) {
                    case R.id.drawer_search:
                        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                        break;
                    case R.id.drawer_edit:
                        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_OPEN);
                        break;
                }
                return true;
            }
        });
    }
}
