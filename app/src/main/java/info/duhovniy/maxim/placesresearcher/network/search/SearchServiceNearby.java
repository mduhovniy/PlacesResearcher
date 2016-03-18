package info.duhovniy.maxim.placesresearcher.network.search;

import android.app.IntentService;
import android.content.Intent;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import info.duhovniy.maxim.placesresearcher.db.DBHandler;
import info.duhovniy.maxim.placesresearcher.network.NetworkConstants;

/**
 * Created by maxduhovniy on 17/12/2015.
 */
public class SearchServiceNearby extends IntentService {

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public SearchServiceNearby() {
        super(NetworkConstants.NEARBY_SEARCH);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        int resultsNumber = 0;

        if (intent.hasExtra(NetworkConstants.REQUEST_STRING)) {
            String searchRequest = intent.getStringExtra(NetworkConstants.REQUEST_STRING);

            String url = null;
            try {
                url = NetworkConstants.TEXT_QUERY + URLEncoder.encode(searchRequest, "utf-8")
                        + NetworkConstants.KEY + NetworkConstants.WEB_API_KEY;
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            DBHandler dbHandler = new DBHandler(getApplicationContext());

            try {
                resultsNumber = dbHandler
                        .updateLastTextSearch(new JSONObject(NetworkConstants.sendHttpRequest(url)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(NetworkConstants.TEXT_SEARCH);
        broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);

        broadcastIntent.putExtra(NetworkConstants.RESPONSE_MESSAGE, resultsNumber);
        sendBroadcast(broadcastIntent);

    }


}
