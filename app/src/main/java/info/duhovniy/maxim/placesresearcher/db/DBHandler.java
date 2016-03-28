package info.duhovniy.maxim.placesresearcher.db;

import android.app.DownloadManager;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import info.duhovniy.maxim.placesresearcher.network.NetworkConstants;

/**
 * Created by maxduhovniy on 25/02/2016.
 */
public class DBHandler {

    private DBHelper helper;
    private Context mContext;

    public DBHandler(Context context) {
        helper = new DBHelper(context, DBConstants.DB_NAME, null, DBConstants.DB_VERSION);
        mContext = context;
    }

    // Returns number of search results in given JSON result of Text Search
    public int updateLastTextSearch(JSONObject j) {
        int result = 0;
        SQLiteDatabase db = helper.getWritableDatabase();

        String folder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                + DBConstants.TEXT_SEARCH_PHOTO_DIR;
        File dir = new File(folder);
        if (!dir.exists()) {
            if (dir.mkdirs()) {
                Log.i(NetworkConstants.LOG_TAG, "Photo Directory Created");
            }
        } else {
            File[] files = dir.listFiles();
            if (files != null)
                for (File f : files) f.delete();
        }

        db.execSQL("DELETE FROM " + DBConstants.LAST_SEARCH_TABLE);

//        db.beginTransactionNonExclusive();
//        db.beginTransaction();

        try {
            JSONArray jsonArray = j.getJSONArray(NetworkConstants.RESULTS_HEADER);
            if (jsonArray != null) {

                for (int i = 0; i < jsonArray.length(); i++) {

                    ContentValues values = new ContentValues();
                    values.put(DBConstants.NAME, jsonArray.getJSONObject(i).getString(NetworkConstants.NAME));
                    values.put(DBConstants.PLACE_ID, jsonArray.getJSONObject(i).getString(NetworkConstants.PLACE_ID));
                    values.put(DBConstants.LAT, jsonArray.getJSONObject(i).getJSONObject(NetworkConstants.GEOMETRY_HEADER)
                            .getJSONObject(NetworkConstants.LOCATION_HEADER).getDouble(NetworkConstants.LAT));
                    values.put(DBConstants.LNG, jsonArray.getJSONObject(i).getJSONObject(NetworkConstants.GEOMETRY_HEADER)
                            .getJSONObject(NetworkConstants.LOCATION_HEADER).getDouble(NetworkConstants.LNG));
                    values.put(DBConstants.ADDRESS, jsonArray.getJSONObject(i).getString(NetworkConstants.FORMATTED_ADDRESS));
                    values.put(DBConstants.TYPE, jsonArray.getJSONObject(i).getJSONArray(NetworkConstants.TYPES_HEADER)
                            .getString(0));

                    String photoRef = jsonArray.getJSONObject(i).getJSONArray(NetworkConstants.PHOTOS_HEADER)
                            .getJSONObject(0).getString(NetworkConstants.PHOTO_REF);

                    if (photoRef != null) {
                        photoRef = NetworkConstants.PHOTO_QUERY
                                + photoRef
                                + NetworkConstants.KEY
                                + NetworkConstants.WEB_API_KEY;

                        String fileName = "TextSearchRes" + (i + 1) + ".jpg";

                        downloadPhoto(photoRef, fileName, mContext);

                        values.put(DBConstants.PHOTO_LINK, fileName);
                    }

                    if (db.insert(DBConstants.LAST_SEARCH_TABLE, null, values) != -1)
                        result++;
                }

//                db.setTransactionSuccessful();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (SQLiteException e) {
            e.getMessage();
        } finally {
//            db.endTransaction();
            if (db.isOpen())
                db.close();
        }

        return result;
    }

    // Returns number of search results in given JSON result of Nearby Search
    public int updateLastNearbySearch(JSONObject j) {
        int result = 0;
        SQLiteDatabase db = helper.getWritableDatabase();

        String folder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                + DBConstants.TEXT_SEARCH_PHOTO_DIR;
        File dir = new File(folder);
        if (!dir.exists()) {
            if (dir.mkdirs()) {
                Log.i(NetworkConstants.LOG_TAG, "Photo Directory Created");
            }
        } else {
            File[] files = dir.listFiles();
            if (files != null)
                for (File f : files) f.delete();
        }

        db.execSQL("DELETE FROM " + DBConstants.LAST_SEARCH_TABLE);

//        db.beginTransactionNonExclusive();
//        db.beginTransaction();

        try {
            JSONArray jsonArray = j.getJSONArray(NetworkConstants.RESULTS_HEADER);
            if (jsonArray != null) {

                for (int i = 0; i < jsonArray.length(); i++) {

                    ContentValues values = new ContentValues();
                    values.put(DBConstants.NAME, jsonArray.getJSONObject(i).getString(NetworkConstants.NAME));
                    values.put(DBConstants.PLACE_ID, jsonArray.getJSONObject(i).getString(NetworkConstants.PLACE_ID));
                    values.put(DBConstants.LAT, jsonArray.getJSONObject(i).getJSONObject(NetworkConstants.GEOMETRY_HEADER)
                            .getJSONObject(NetworkConstants.LOCATION_HEADER).getDouble(NetworkConstants.LAT));
                    values.put(DBConstants.LNG, jsonArray.getJSONObject(i).getJSONObject(NetworkConstants.GEOMETRY_HEADER)
                            .getJSONObject(NetworkConstants.LOCATION_HEADER).getDouble(NetworkConstants.LNG));
                    values.put(DBConstants.ADDRESS, jsonArray.getJSONObject(i).getString(NetworkConstants.VICINITY));
                    values.put(DBConstants.TYPE, jsonArray.getJSONObject(i).getJSONArray(NetworkConstants.TYPES_HEADER)
                            .getString(0));

                    String photoRef = jsonArray.getJSONObject(i).getJSONArray(NetworkConstants.PHOTOS_HEADER)
                            .getJSONObject(0).getString(NetworkConstants.PHOTO_REF);

                    if (photoRef != null) {
                        photoRef = NetworkConstants.PHOTO_QUERY
                                + photoRef
                                + NetworkConstants.KEY
                                + NetworkConstants.WEB_API_KEY;

                        String fileName = "TextSearchRes" + (i + 1) + ".jpg";

                        downloadPhoto(photoRef, fileName, mContext);

                        values.put(DBConstants.PHOTO_LINK, fileName);
                    }

                    if (db.insert(DBConstants.LAST_SEARCH_TABLE, null, values) != -1)
                        result++;
                }

//                db.setTransactionSuccessful();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (SQLiteException e) {
            e.getMessage();
        } finally {
//            db.endTransaction();
            if (db.isOpen())
                db.close();
        }

        return result;
    }

    // returns a cursor with all Last Search Results
    public Cursor getLastSearch() {
        Cursor cursor = null;
        SQLiteDatabase db = helper.getReadableDatabase();

        try {
            cursor = db.query(DBConstants.LAST_SEARCH_TABLE, null, null, null, null, null, null,
                    null);
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
        return cursor;
    }

    // returns a number of deleted rows from Last Search Results table
    public int deleteLastSearchItem(int id) {
        int res = 0;
        SQLiteDatabase db = helper.getWritableDatabase();

        try {
            res = db.delete(DBConstants.LAST_SEARCH_TABLE, DBConstants.ID + "=?",
                    new String[]{String.valueOf(id)});
        } catch (SQLiteException e) {
            e.getMessage();
        } finally {
            if (db.isOpen())
                db.close();
        }
        return res;
    }

    private void downloadPhoto(String uRl, String fileName, Context context) {

        DownloadManager mgr = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);

        Uri downloadUri = Uri.parse(uRl);
        DownloadManager.Request request = new DownloadManager.Request(
                downloadUri);

        request.setAllowedNetworkTypes(
                DownloadManager.Request.NETWORK_WIFI
                        | DownloadManager.Request.NETWORK_MOBILE)
                .setAllowedOverRoaming(false)
                .setTitle("Place Photo Loader")
                .setDescription(fileName)
                .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS
                        + DBConstants.TEXT_SEARCH_PHOTO_DIR, fileName);

        mgr.enqueue(request);

    }

}
