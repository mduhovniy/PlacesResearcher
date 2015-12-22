package info.duhovniy.maxim.placesresearcher.network.search;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import info.duhovniy.maxim.placesresearcher.network.NetworkConstants;
import info.duhovniy.maxim.placesresearcher.network.Place;

/**
 * Created by maxduhovniy on 17/12/2015.
 */
public class SearchServiceNearby extends IntentService {

    private static final String RESULT_HEADER = "predictions";

    private String searchRequest;
    private ArrayList<Place> searchResult = new ArrayList<>();

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public SearchServiceNearby() {
        super(NetworkConstants.NEARBY_SEARCH);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent.hasExtra(NetworkConstants.REQUEST_STRING)) {
            searchRequest = intent.getStringExtra(NetworkConstants.REQUEST_STRING);
        }
        String url = null;
        try {
            url = "https://maps.googleapis.com/maps/api/place/autocomplete/json?input="
                    + URLEncoder.encode(searchRequest, "utf-8")
                    + "&key=" + NetworkConstants.WEB_API_KEY;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String placeString = NetworkConstants.sendHttpRequest(url);

        try {
            JSONObject object = new JSONObject(placeString);
            JSONArray array = object.getJSONArray(RESULT_HEADER);

            for (int i = 0; i < array.length(); i++) {
                String description = array.getJSONObject(i).getString(NetworkConstants.DESCRIPTION);
                String place_id = array.getJSONObject(i).getString(NetworkConstants.PLACE_ID);

                Place place = new Place(place_id);
                place.setPlaceName(description);
                searchResult.add(place);

            }

        } catch (JSONException e) {
            Log.e(NetworkConstants.LOG_TAG, e.getMessage());
        }

        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(NetworkConstants.NEARBY_SEARCH);
        broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);

        Bundle newBundle = new Bundle();
        newBundle.putParcelableArrayList(NetworkConstants.RESPONSE_LIST, searchResult);

        broadcastIntent.putExtra(NetworkConstants.RESPONSE_MESSAGE, newBundle);
        sendBroadcast(broadcastIntent);

    }


}
