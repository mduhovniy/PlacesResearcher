package info.duhovniy.maxim.placesresearcher.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;

import info.duhovniy.maxim.placesresearcher.network.NetworkConstants;
import info.duhovniy.maxim.placesresearcher.network.Place;

/**
 * Created by maxduhovniy on 04/01/2016.
 */
public class PlaceSearchReceiver extends BroadcastReceiver {

    ControlFragment controlFragment;

    public PlaceSearchReceiver(ControlFragment controlFragment) {
        this.controlFragment = controlFragment;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        ArrayList<Place> resultList = intent.getBundleExtra(NetworkConstants.RESPONSE_MESSAGE)
                .getParcelableArrayList(NetworkConstants.RESPONSE_LIST);
        if (controlFragment != null && resultList != null) {
            controlFragment.onResultListChange(resultList);
        }

        switch (intent.getAction()) {
            case NetworkConstants.AUTOCOMPLETE_SEARCH:
                //TODO:
                break;
            case NetworkConstants.TEXT_SEARCH:
                //TODO:
                break;
            case NetworkConstants.RADAR_SEARCH:
                //TODO:
                break;
            case NetworkConstants.NEARBY_SEARCH:
                //TODO:
                break;
        }

    }
}