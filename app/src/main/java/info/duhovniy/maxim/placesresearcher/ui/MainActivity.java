package info.duhovniy.maxim.placesresearcher.ui;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import info.duhovniy.maxim.placesresearcher.R;
import info.duhovniy.maxim.placesresearcher.network.NetworkConstants;

public class MainActivity extends FragmentActivity {

    private MapFragment mapFragment = MapFragment.getInstance();
    private ControlFragment controlFragment = ControlFragment.getInstance();

    private PlaceSearchReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().add(R.id.control_fragment_container,
                controlFragment, UIConstants.CONTROL_FRAGMENT).addToBackStack(null).commit();
        if (findViewById(R.id.map_fragment_container) != null) {
            fragmentManager.beginTransaction().add(R.id.map_fragment_container,
                    mapFragment, UIConstants.MAP_FRAGMENT).addToBackStack(null).commit();
        }

        receiver = getRegisteredReceiver();
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

}
