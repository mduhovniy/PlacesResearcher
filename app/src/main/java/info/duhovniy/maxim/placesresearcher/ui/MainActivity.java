package info.duhovniy.maxim.placesresearcher.ui;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import info.duhovniy.maxim.placesresearcher.R;

public class MainActivity extends FragmentActivity {

    private MapFragment mapFragment = MapFragment.getInstance();
    private ControlFragment controlFragment = ControlFragment.getInstance();

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

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

/*        fragmentManager.saveFragmentInstanceState(controlFragment);

        if (fragmentManager.findFragmentByTag("Map Frag").isVisible())
            fragmentManager.saveFragmentInstanceState(mapFragment);*/
        super.onSaveInstanceState(outState);
    }
}
