package info.duhovniy.maxim.placesresearcher.ui;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import info.duhovniy.maxim.placesresearcher.R;
import info.duhovniy.maxim.placesresearcher.network.NetworkConstants;
import info.duhovniy.maxim.placesresearcher.network.search.SearchServiceText;

public class MainActivity extends AppCompatActivity {

    private MyMapFragment mapFragment = MyMapFragment.getInstance();
    private ControlFragment controlFragment = ControlFragment.getInstance();

    private PlaceSearchReceiver receiver;

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        FragmentManager fragmentManager = getSupportFragmentManager();

        fragmentManager.beginTransaction().add(R.id.control_fragment_container,
                controlFragment, UIConstants.CONTROL_FRAGMENT).addToBackStack(null).commit();

        if (findViewById(R.id.map_fragment_container) != null) {
            fragmentManager.beginTransaction().add(R.id.map_fragment_container,
                    mapFragment, UIConstants.MAP_FRAGMENT).addToBackStack(null).commit();
        }

        receiver = getRegisteredReceiver();

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
        filter.addAction(NetworkConstants.AUTOCOMPLETE_SEARCH);
        filter.addAction(NetworkConstants.NEARBY_SEARCH);
        filter.addAction(NetworkConstants.RADAR_SEARCH);
        filter.addAction(NetworkConstants.TEXT_SEARCH);

        PlaceSearchReceiver r = new PlaceSearchReceiver(controlFragment);
        registerReceiver(r, filter);

        return r;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

/*
        fragmentManager.saveFragmentInstanceState(controlFragment);

        if (fragmentManager.findFragmentByTag("Map Frag").isVisible())
            fragmentManager.saveFragmentInstanceState(mapFragment);
*/
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    public void doMySearch(String query) {
        Intent intent = new Intent();
        intent.putExtra(NetworkConstants.REQUEST_STRING, query);

        intent.setClass(MainActivity.this, SearchServiceText.class);
        startService(intent);
    }
}
