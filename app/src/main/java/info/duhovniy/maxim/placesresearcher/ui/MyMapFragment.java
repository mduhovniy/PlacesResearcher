package info.duhovniy.maxim.placesresearcher.ui;

import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.util.Log;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.OnStreetViewPanoramaReadyCallback;
import com.google.android.gms.maps.StreetViewPanorama;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.SupportStreetViewPanoramaFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;

import info.duhovniy.maxim.placesresearcher.ui.map.LocationProvider;
import info.duhovniy.maxim.placesresearcher.ui.map.MyItem;


public class MyMapFragment extends SupportMapFragment implements OnMapReadyCallback,
        LocationProvider.LocationCallback, OnStreetViewPanoramaReadyCallback {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    private int locationCounter = 0;

    private LocationProvider mLocationProvider;

    private ClusterManager<MyItem> mClusterManager = null;

    private static MyMapFragment instance = null;

    private Circle circle;

    private LatLng latLng = new LatLng(0, 0);

    public static MyMapFragment getInstance() {
        if (instance == null) {
            instance = new MyMapFragment();
        }
        return instance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Create an instance of GoogleAPIClient.
        mLocationProvider = new LocationProvider(getContext(), this);

        getMapAsync(this);

        SupportStreetViewPanoramaFragment streetViewPanoramaFragment =
                new SupportStreetViewPanoramaFragment();

        getChildFragmentManager().beginTransaction()
                .add(streetViewPanoramaFragment, "panorama")
                .commit();

        streetViewPanoramaFragment.setUserVisibleHint(true);

        streetViewPanoramaFragment.getStreetViewPanoramaAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;

        mMap.setMyLocationEnabled(true);

        CircleOptions circleOptions = new CircleOptions()
                .center(latLng)
                .radius(1000); // In meters

        // Get back the mutable Circle
        circle = mMap.addCircle(circleOptions);
        circle.setStrokeColor(Color.RED);

    }

    @Override
    public void onResume() {
        super.onResume();
        mLocationProvider.connect();
    }

    @Override
    public void onPause() {
        super.onPause();
        mLocationProvider.disconnect();
    }

    @Override
    public void handleNewLocation(Location location) {
        Log.d(UIConstants.LOG_TAG, location.toString());

        latLng = new LatLng(location.getLatitude(), location.getLongitude());

        circle.setCenter(latLng);

        MarkerOptions options = new MarkerOptions()
                .position(latLng)
                .title("I am here! " + ++locationCounter);
        if (mClusterManager == null && mMap != null) {
            setUpCluster();
        } else {
            assert mMap != null;
            mClusterManager.addItem(new MyItem(mMap.addMarker(options).getPosition()));
        }
    }

    private void setUpCluster() {
        mClusterManager = new ClusterManager<>(getActivity(), mMap);
        mMap.setOnCameraChangeListener(mClusterManager);
        mMap.setOnMarkerClickListener(mClusterManager);

        Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(mLocationProvider.getGoogleApiClient());

        if (lastLocation != null) {
            Snackbar.make(getView(), "Your location is found!", Snackbar.LENGTH_LONG).show();
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lastLocation.getLatitude(),
                    lastLocation.getLongitude()), 15));
        }
    }

    @Override
    public void onStreetViewPanoramaReady(StreetViewPanorama streetViewPanorama) {
        streetViewPanorama.setPosition(latLng);
    }
}
