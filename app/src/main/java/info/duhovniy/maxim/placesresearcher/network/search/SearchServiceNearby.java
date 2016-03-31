package info.duhovniy.maxim.placesresearcher.network.search;

import android.app.IntentService;
import android.content.Intent;
import android.preference.PreferenceManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import info.duhovniy.maxim.placesresearcher.db.DBHandler;
import info.duhovniy.maxim.placesresearcher.network.NetworkConstants;
import info.duhovniy.maxim.placesresearcher.ui.UIConstants;


public class SearchServiceNearby extends IntentService {

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public SearchServiceNearby() {
        super(NetworkConstants.NEARBY_SEARCH);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        int resultsNumber = -1;

        if (intent.hasExtra(NetworkConstants.REQUEST_STRING)) {
            String searchRequest = intent.getStringExtra(NetworkConstants.REQUEST_STRING);
            String searchType = intent.getStringExtra(NetworkConstants.REQUEST_TYPE);
            double lat = intent.getDoubleExtra(NetworkConstants.REQUEST_LAT, 0);
            double lng = intent.getDoubleExtra(NetworkConstants.REQUEST_LNG, 0);

            String url = null;
            if(!searchType.equals("NA")) {
                try {
                    url = NetworkConstants.NEAR_QUERY + lat + "," + lng
                            + NetworkConstants.TYPE_QUERY + searchType
                            + NetworkConstants.KEYWORD_QUERY + URLEncoder.encode(searchRequest, "utf-8")
                            + NetworkConstants.RADIUS_QUERY + PreferenceManager
                            .getDefaultSharedPreferences(getApplicationContext()).getInt(UIConstants.RADIUS, 1000)
                            + NetworkConstants.KEY + NetworkConstants.WEB_API_KEY;
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    url = NetworkConstants.NEAR_QUERY + lat + "," + lng
                            + NetworkConstants.KEYWORD_QUERY + URLEncoder.encode(searchRequest, "utf-8")
                            + NetworkConstants.RADIUS_QUERY + PreferenceManager
                            .getDefaultSharedPreferences(getApplicationContext()).getInt(UIConstants.RADIUS, 1000)
                            + NetworkConstants.KEY + NetworkConstants.WEB_API_KEY;
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
            DBHandler dbHandler = new DBHandler(getApplicationContext());

            try {
                resultsNumber = dbHandler
                        .updateLastNearbySearch(new JSONObject(NetworkConstants.sendHttpRequest(url)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(NetworkConstants.NEARBY_SEARCH);
        broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);

        broadcastIntent.putExtra(NetworkConstants.RESPONSE_MESSAGE, resultsNumber);
        sendBroadcast(broadcastIntent);
    }


}
