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
 * Created by maxduhovniy on 14/12/2015.
 */
public class SearchServiceAutocomplete extends IntentService {

    private static final String RESULT_HEADER = "predictions";

    private String searchRequest;
    private ArrayList<Place> searchResult = new ArrayList<>();

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public SearchServiceAutocomplete() {
        super(NetworkConstants.AUTOCOMPLETE_SEARCH);
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
        broadcastIntent.setAction(NetworkConstants.AUTOCOMPLETE_SEARCH);
        broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);

        Bundle newBundle = new Bundle();
        newBundle.putParcelableArrayList(NetworkConstants.RESPONSE_LIST, searchResult);

        broadcastIntent.putExtra(NetworkConstants.RESPONSE_MESSAGE, newBundle);
        sendBroadcast(broadcastIntent);

    }

/*
    public String sendHttpRequest(String urlString) {

        BufferedReader input = null;
        HttpURLConnection httpCon = null;
        InputStream input_stream = null;
        InputStreamReader input_stream_reader = null;
        StringBuilder response = new StringBuilder();

        try {
            URL url = new URL(urlString);
            httpCon = (HttpURLConnection) url.openConnection();

            if (httpCon.getResponseCode() != HttpURLConnection.HTTP_OK) {
                Log.e(NetworkConstants.LOG_TAG, "Cannot Connect to : " + urlString);
                return null;
            }

            input_stream = httpCon.getInputStream();
            input_stream_reader = new InputStreamReader(input_stream);
            input = new BufferedReader(input_stream_reader);
            String line;

            while ((line = input.readLine()) != null) {
                response.append(line).append("\n");
            }
        } catch (Exception e) {
            Log.e(NetworkConstants.LOG_TAG, e.getMessage());
        } finally {
            if (input != null) {
                try {
                    input_stream_reader.close();
                    input_stream.close();
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                httpCon.disconnect();
            }
        }
        return response.toString();
    }
*/


}
