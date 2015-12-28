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
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;

import info.duhovniy.maxim.placesresearcher.ui.map.LocationProvider;
import info.duhovniy.maxim.placesresearcher.ui.map.MyItem;


public class MapFragment extends SupportMapFragment implements OnMapReadyCallback,
        LocationProvider.LocationCallback {

//    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    private int locationCounter = 0;

    private LocationProvider mLocationProvider;

    private ClusterManager<MyItem> mClusterManager = null;

    private static MapFragment instance = null;

    private Circle circle;

    public static MapFragment getInstance() {
        if (instance == null) {
            instance = new MapFragment();
        }
        return instance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Create an instance of GoogleAPIClient.
        mLocationProvider = new LocationProvider(getContext(), this);

        getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;

        mMap.setMyLocationEnabled(true);

        CircleOptions circleOptions = new CircleOptions()
                .center(new LatLng(0, 0))
                .radius(1000); // In meters

        // Get back the mutable Circle
        circle = mMap.addCircle(circleOptions);
        circle.setStrokeColor(Color.RED);
    }

    @Override
    public void onResume() {
        super.onResume();
//        getMapAsync(this);
        mLocationProvider.connect();
    }

    @Override
    public void onPause() {
        super.onPause();
//        mMap.clear();
        mLocationProvider.disconnect();
    }

/*
    @Override
    public void onConnected(Bundle bundle) {
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null && mMap != null) {
            Snackbar.make(getInstance().getView(), "Your location is found!", Snackbar.LENGTH_LONG).show();
            LatLng telAviv = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            mMap.addMarker(new MarkerOptions().position(telAviv).title("here I am!!!"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(telAviv, 15));
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(getActivity(), CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            Log.i(UIConstants.LOG_TAG, "Location services connection failed with code " + connectionResult.getErrorCode());

        }
    }
*/

    @Override
    public void handleNewLocation(Location location) {
        Log.d(UIConstants.LOG_TAG, location.toString());

        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

        circle.setCenter(latLng);

        MarkerOptions options = new MarkerOptions()
                .position(latLng)
                .title("I am here! " + ++locationCounter);
        if(mClusterManager == null && mMap != null) {
            setUpCluster();
        } else {
//            mMap.clear();
            assert mMap != null;
            mClusterManager.addItem(new MyItem(mMap.addMarker(options).getPosition()));
//            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14));
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

}
