package info.duhovniy.maxim.placesresearcher.network;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import info.duhovniy.maxim.placesresearcher.ui.UIConstants;

/**
 * Created by maxduhovniy on 15/12/2015.
 */
public class NetworkConstants {

    // Google Place Search API key
    public static final String WEB_API_KEY = "AIzaSyB2LrzIvoYmQb3kmbMcdMwmoXQ4b9gtlyc";

    public static final String LOG_TAG = "Network.LOG";

    // Place Search API JSON result TAGs
    public static final String RESULTS_HEADER = "results";

    public static final String PLACE_ID = "place_id";
    public static final String NAME = "name";

    public static final String GEOMETRY_HEADER = "geometry";
    public static final String LOCATION_HEADER = "location";
    public static final String LAT = "lat";
    public static final String LNG = "lng";

    public static final String FORMATTED_ADDRESS = "formatted_address";
    public static final String VICINITY = "vicinity";

    public static final String TYPES_HEADER = "types";

    public static final String PHOTOS_HEADER = "photos";
    public static final String PHOTO_REF = "photo_reference";

    // Place Search API WEB place search request parts
    public static final String TEXT_QUERY = "https://maps.googleapis.com/maps/api/place/textsearch/json?query=";
    public static final String NEAR_QUERY = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=";
    public static final String TYPE_QUERY = "&type=";
    public static final String KEYWORD_QUERY = "&keyword=";
    public static final String RADIUS_QUERY = "&radius=";

    // Place Search API WEB lace photo download request
    public static final String PHOTO_QUERY = "https://maps.googleapis.com/maps/api/place/photo?maxwidth="
            + UIConstants.PHOTO_SIZE + "&photoreference=";

    public static final String KEY = "&key=";

    public static final String REQUEST_STRING = "info.duhovniy.maxim.placesresearcher.REQUEST_STRING";
    public static final String REQUEST_TYPE = "info.duhovniy.maxim.placesresearcher.REQUEST_TYPE";
    public static final String REQUEST_LAT = "info.duhovniy.maxim.placesresearcher.REQUEST_LAT";
    public static final String REQUEST_LNG = "info.duhovniy.maxim.placesresearcher.REQUEST_LNG";

    public static final String RESPONSE_MESSAGE = "info.duhovniy.maxim.placesresearcher.RESPONSE_MESSAGE";
    public static final String NEARBY_SEARCH = "info.duhovniy.maxim.placesresearcher.NEARBY_SEARCH";
    public static final String TEXT_SEARCH = "info.duhovniy.maxim.placesresearcher.TEXT_SEARCH";

    public static String sendHttpRequest(String urlString) {

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

}
