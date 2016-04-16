package info.duhovniy.maxim.placesresearcher.ui;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;

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
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

import java.io.File;
import java.util.ArrayList;

import info.duhovniy.maxim.placesresearcher.R;
import info.duhovniy.maxim.placesresearcher.db.DBConstants;
import info.duhovniy.maxim.placesresearcher.db.DBHandler;
import info.duhovniy.maxim.placesresearcher.network.Place;
import info.duhovniy.maxim.placesresearcher.ui.map.LocationProvider;


public class MyMapFragment extends SupportMapFragment implements OnMapReadyCallback,
        LocationProvider.LocationCallback {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    private LocationProvider mLocationProvider;

    private ClusterManager<Place> mClusterManager = null;

    private static MyMapFragment instance = null;

    private Circle circle;

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

        circle = null;

        getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;

        if (mMap != null) {
            mMap.setMyLocationEnabled(true);
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

        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

        if (circle == null) {
            CircleOptions circleOptions = new CircleOptions()
                    .center(latLng)
                    .radius(getRadius()); // In meters

            // Get back the mutable Circle
            circle = mMap.addCircle(circleOptions);
            circle.setStrokeColor(Color.RED);
            circle.setStrokeWidth(2);
        }
        circle.setCenter(latLng);       // move circle to new location
        circle.setRadius(getRadius());  // double check if user changed preferences
    }

    public void setUpCluster() {
        if (mClusterManager != null)
            mClusterManager.clearItems();

        mClusterManager = new ClusterManager<>(getActivity(), mMap);
        mMap.setOnCameraChangeListener(mClusterManager);
        mMap.setOnMarkerClickListener(mClusterManager);

        DBHandler db = new DBHandler(getContext());
        ArrayList<Place> list = db.getLastSearch();

        mClusterManager.setRenderer(new OwnRendering(getActivity(), mMap, mClusterManager));

        for (Place place : list) {
            assert mMap != null;
            mClusterManager.addItem(place);
        }
    }

    private class OwnRendering extends DefaultClusterRenderer<Place> {

        public OwnRendering(Context context, GoogleMap map,
                            ClusterManager<Place> clusterManager) {
            super(context, map, clusterManager);
        }

        protected void onBeforeClusterItemRendered(Place item, MarkerOptions markerOptions) {

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = UIConstants.MAP_PHOTO_SIZE;
            File imageFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                    + DBConstants.SEARCH_PHOTO_DIR,
                    item.getPlacePhotoReference());
            if (imageFile.exists())
                markerOptions.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.
                        decodeFile(imageFile.getAbsolutePath(), options)));

            markerOptions.title(item.getPlaceName());
            super.onBeforeClusterItemRendered(item, markerOptions);
        }
    }

    public void showPlace(Place place) {

        if (mClusterManager != null) {
/*            MarkerOptions options = new MarkerOptions()
                    .position(place.getPlaceLocation())
                    .title(place.getPlaceName())
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
 */
            assert mMap != null;
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(place.getPlaceLocation(), 15));
        }
    }

    private double getRadius() {
        double radius = PreferenceManager.getDefaultSharedPreferences(getContext())
                .getInt(getString(R.string.search_distance), 1) * 1000;
        if (!PreferenceManager.getDefaultSharedPreferences(getContext())
                .getBoolean(getString(R.string.unit_checkbox), true))
            radius *= UIConstants.MILE_TO_KM;
        return radius;
    }
}
