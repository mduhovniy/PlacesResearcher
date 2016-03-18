package info.duhovniy.maxim.placesresearcher.ui;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.maps.OnStreetViewPanoramaReadyCallback;
import com.google.android.gms.maps.StreetViewPanorama;
import com.google.android.gms.maps.SupportStreetViewPanoramaFragment;
import com.google.android.gms.maps.model.LatLng;

import info.duhovniy.maxim.placesresearcher.R;

/**
 * Created by maxduhovniy on 05/01/2016.
 */
public class MyMapActivity extends AppCompatActivity implements OnStreetViewPanoramaReadyCallback {

    private LatLng latLng = new LatLng(0, 0); // TODO: check if needed?

    private MyMapFragment mapFragment = MyMapFragment.getInstance();

    private DrawerLayout drawerLayout;

    private FragmentManager fragmentManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        // TODO: get latLng from Intent.Extra
        if(getIntent() != null) {
            latLng = new LatLng(getIntent().getDoubleExtra(UIConstants.LAT, 0),
                    getIntent().getDoubleExtra(UIConstants.LNG, 0));
        }

        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().add(R.id.map_fragment_container,
                mapFragment, UIConstants.MAP_FRAGMENT).commit();

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        NavigationView navView = (NavigationView) findViewById(R.id.navigation_view);
        if (navView != null) {

            setupDrawerContent(navView);
        }

    }

    @Override
    public void onStreetViewPanoramaReady(StreetViewPanorama streetViewPanorama) {
        streetViewPanorama.setPosition(latLng);
    }

    private void setupDrawerContent(NavigationView navigationView) {

        View header = navigationView.inflateHeaderView(R.layout.drawer_header);


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
//                        viewPager.setCurrentItem(0);
                        break;
                    case R.id.drawer_edit:
                        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_OPEN);
//                        viewPager.setCurrentItem(1);
                        break;
                    case R.id.drawer_base:
                        drawerLayout.closeDrawers();
//                        viewPager.setCurrentItem(2);
                        break;
                }

                //
                return true;
            }
        });
    }
}
