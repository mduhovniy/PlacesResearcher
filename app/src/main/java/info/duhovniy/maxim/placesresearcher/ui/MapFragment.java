package info.duhovniy.maxim.placesresearcher.ui;

import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import info.duhovniy.maxim.placesresearcher.ui.map.LocationProvider;


public class MapFragment extends SupportMapFragment implements OnMapReadyCallback,
        LocationProvider.LocationCallback {

    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    private int locationCounter = 0;

    private LocationProvider mLocationProvider;

    private static MapFragment instance = null;

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
        getMapAsync(getInstance());
        mLocationProvider = new LocationProvider(getContext(), this);
    }

    @Override
    public void onMapReady(GoogleMap map) {

        mMap = map;

        mMap.setMyLocationEnabled(true);
/*
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);

            if (mLastLocation != null && mMap != null) {
            LatLng telAviv = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            mMap.addMarker(new MarkerOptions().position(telAviv).title("here I am!!!"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(telAviv, 14));
        }*/
    }

    @Override
    public void onResume() {
        super.onResume();
        getMapAsync(getInstance());
        mLocationProvider.connect();
    }

    @Override
    public void onPause() {
        super.onPause();
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

        //mMap.addMarker(new MarkerOptions().position(new LatLng(currentLatitude, currentLongitude)).title("Current Location"));
        MarkerOptions options = new MarkerOptions()
                .position(latLng)
                .title("I am here! " + ++locationCounter);
        if (mMap != null) {
//            mMap.clear();
            mMap.addMarker(options);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
        }
    }


}
