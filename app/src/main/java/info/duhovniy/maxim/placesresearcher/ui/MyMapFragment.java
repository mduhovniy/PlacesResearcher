package info.duhovniy.maxim.placesresearcher.ui;

import android.database.Cursor;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;

import info.duhovniy.maxim.placesresearcher.db.DBConstants;
import info.duhovniy.maxim.placesresearcher.db.DBHandler;
import info.duhovniy.maxim.placesresearcher.network.Place;
import info.duhovniy.maxim.placesresearcher.ui.map.LocationProvider;
import info.duhovniy.maxim.placesresearcher.ui.map.MyItem;


public class MyMapFragment extends SupportMapFragment implements OnMapReadyCallback,
        LocationProvider.LocationCallback {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

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
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;

        mMap.setMyLocationEnabled(true);

        Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(mLocationProvider.getGoogleApiClient());

        if (lastLocation != null) {
            latLng = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lastLocation.getLatitude(),
                    lastLocation.getLongitude()), 15));

            CircleOptions circleOptions = new CircleOptions()
                    .center(latLng)
                    .radius(1000); // In meters

            // Get back the mutable Circle
            circle = mMap.addCircle(circleOptions);
            circle.setStrokeColor(Color.RED);
            circle.setStrokeWidth(2);
        }

        if (mClusterManager == null && mMap != null) {
            setUpCluster();
        }
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
        //Log.d(UIConstants.LOG_TAG, location.toString());

        latLng = new LatLng(location.getLatitude(), location.getLongitude());

        if(circle == null) {
            CircleOptions circleOptions = new CircleOptions()
                    .center(latLng)
                    .radius(1000); // In meters

            // Get back the mutable Circle
            circle = mMap.addCircle(circleOptions);
            circle.setStrokeColor(Color.RED);
            circle.setStrokeWidth(2);
        }
        circle.setCenter(latLng);
    }

    public void setUpCluster() {
        mClusterManager = new ClusterManager<>(getActivity(), mMap);
        mMap.setOnCameraChangeListener(mClusterManager);
        mMap.setOnMarkerClickListener(mClusterManager);

        DBHandler db = new DBHandler(getContext());
        Cursor cursor = db.getLastSearch();

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            MarkerOptions options = new MarkerOptions()
                    .position(new LatLng(cursor.getDouble(cursor.getColumnIndex(DBConstants.LAT)),
                            cursor.getDouble(cursor.getColumnIndex(DBConstants.LNG))))
                    .title(cursor.getString(cursor.getColumnIndex(DBConstants.NAME)));
            assert mMap != null;
            mClusterManager.addItem(new MyItem(mMap.addMarker(options).getPosition()));

            cursor.moveToNext();
        }
    }

    public void showPlace(Place place) {

        if (mClusterManager != null) {
            MarkerOptions options = new MarkerOptions()
                    .position(place.getPlaceLocation())
                    .title(place.getPlaceName())
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
            assert mMap != null;
            mClusterManager.addItem(new MyItem(mMap.addMarker(options).getPosition()));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(place.getPlaceLocation(), 15));
        }
    }

}
