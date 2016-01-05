package info.duhovniy.maxim.placesresearcher.ui;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.maps.OnStreetViewPanoramaReadyCallback;
import com.google.android.gms.maps.StreetViewPanorama;
import com.google.android.gms.maps.StreetViewPanoramaFragment;
import com.google.android.gms.maps.model.LatLng;

import info.duhovniy.maxim.placesresearcher.R;

/**
 * Created by maxduhovniy on 05/01/2016.
 */
public class MyMapActivity extends AppCompatActivity implements OnStreetViewPanoramaReadyCallback {

    private LatLng latLng = new LatLng(0, 0); // TODO: check if needed?

    private MyMapFragment mapFragment = MyMapFragment.getInstance();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        // TODO: get latLng from Intent.Extra
        if(getIntent() != null) {
            latLng = new LatLng(getIntent().getDoubleExtra(UIConstants.LAT, 0),
                    getIntent().getDoubleExtra(UIConstants.LNG, 0));
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().add(R.id.map_fragment_container,
                mapFragment, UIConstants.MAP_FRAGMENT).addToBackStack(null).commit();

        StreetViewPanoramaFragment streetViewPanoramaFragment =
                (StreetViewPanoramaFragment) getFragmentManager()
                        .findFragmentById(R.id.street_view_panorama);
        streetViewPanoramaFragment.getStreetViewPanoramaAsync(this);
    }

    @Override
    public void onStreetViewPanoramaReady(StreetViewPanorama streetViewPanorama) {
        streetViewPanorama.setPosition(latLng);
    }
}
