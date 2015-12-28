
package info.duhovniy.maxim.placesresearcher.ui.map;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

/**
 * Created by maxduhovniy on 22/12/2015.
 */

public class MyItem implements ClusterItem {
    private final LatLng mPosition;

    public MyItem(LatLng latLng) {
        mPosition = latLng;
    }

    @Override
    public LatLng getPosition() {
        return mPosition;
    }
}
