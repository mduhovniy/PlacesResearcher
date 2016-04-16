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

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import info.duhovniy.maxim.placesresearcher.network.NetworkConstants;
import info.duhovniy.maxim.placesresearcher.network.Place;


public class DBHandler {

    private DBHelper helper;
    private Context mContext;

    public DBHandler(Context context) {
        helper = new DBHelper(context, DBConstants.DB_NAME, null, DBConstants.DB_VERSION);
        mContext = context;
    }

    // Returns number of search results in given JSON result of Text Search if isLocal = false
    // or Nearby Search if isLocal = true
    public int updateLastSearch(JSONObject j, boolean isLocal) {

        int result = 0;
        SQLiteDatabase db = helper.getWritableDatabase();

        cleanUpPhotoDirectory();

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

                    if (isLocal)
                        values.put(DBConstants.ADDRESS, jsonArray.getJSONObject(i).getString(NetworkConstants.VICINITY));
                    else
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

                    if (db.insertOrThrow(DBConstants.LAST_SEARCH_TABLE, null, values) != -1)
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

    // add new place to Favorite table
    // return false if place already exists
    public boolean addFavorite(Place place) {
        boolean isAdded = false;
        SQLiteDatabase db = helper.getWritableDatabase();

        try {
            if (!isFavorite(place)) {
                if (place.getPlacePhotoReference() != null) {
                    int last = getFavoriteList().size();
                    String fileName = "Favorite" + (last + 1) + ".jpg";
                    File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                            + DBConstants.SEARCH_PHOTO_DIR, place.getPlacePhotoReference());
                    copyFile(file, new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                                + DBConstants.SEARCH_PHOTO_DIR, fileName));
                    place.setPlacePhotoReference(fileName);
                }
                if (db.insertOrThrow(DBConstants.FAVORITE_TABLE, null, place.toContentValues()) != -1)
                    isAdded = true;
            }
        } catch (SQLiteException e) {
            e.getMessage();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (db.isOpen())
                db.close();
        }

        return isAdded;
    }

    // delete place from Favorite table
    // returns a number of deleted rows from Favorite table
    public boolean deleteFavorite(Place place) {
        boolean isDeleted = false;
        SQLiteDatabase db = helper.getWritableDatabase();

        try {
            if (db.delete(DBConstants.FAVORITE_TABLE, DBConstants.PLACE_ID + "=?",
                    new String[]{place.getPlaceID()}) > 0) {
                isDeleted = true;

            }
        } catch (SQLiteException e) {
            e.getMessage();
        } finally {
            if (db.isOpen())
                db.close();
        }

        return isDeleted;
    }

    public boolean isFavorite(Place place) {
        boolean isFavorite = false;
        SQLiteDatabase db = helper.getReadableDatabase();

        try {
            if (db.query(DBConstants.FAVORITE_TABLE, null, DBConstants.PLACE_ID + "=?",
                    new String[]{place.getPlaceID()}, null, null, null).moveToFirst())
                isFavorite = true;

        } catch (SQLiteException e) {
            e.getMessage();
        }

        return isFavorite;
    }


    // returns list with all Last Search Results
    public ArrayList<Place> getLastSearch() {
        Cursor cursor = null;
        ArrayList<Place> result = new ArrayList<>();
        SQLiteDatabase db = helper.getReadableDatabase();

        try {
            cursor = db.query(DBConstants.LAST_SEARCH_TABLE, null, null, null, null, null, null);
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
        if (cursor != null) {

            cursor.moveToFirst();

            while (!cursor.isAfterLast()) {

                Place place = new Place();

                place.setPlaceName(cursor.getString(cursor.getColumnIndex(DBConstants.NAME)));
                place.setPlaceID(cursor.getString(cursor.getColumnIndex(DBConstants.PLACE_ID)));
                place.setPlaceLocation(new LatLng(cursor.getDouble(cursor.getColumnIndex(DBConstants.LAT)),
                        cursor.getDouble(cursor.getColumnIndex(DBConstants.LNG))));
                place.setPlacePhotoReference(cursor.getString(cursor.getColumnIndex(DBConstants.PHOTO_LINK)));
                place.setPlaceAddress(cursor.getString(cursor.getColumnIndex(DBConstants.ADDRESS)));
                place.setPlaceType(cursor.getString(cursor.getColumnIndex(DBConstants.TYPE)));

                result.add(place);
                cursor.moveToNext();
            }
            cursor.close();
        } else
            result = null;
        return result;
    }

    // returns a number of deleted rows from Last Search Results table
    public int deleteLastSearchItem(Place place) {
        int res = 0;
        SQLiteDatabase db = helper.getWritableDatabase();

        try {
            res = db.delete(DBConstants.LAST_SEARCH_TABLE, DBConstants.PLACE_ID + "=?",
                    new String[]{place.getPlaceID()});
            if (res > 0) {
                File file = new File(Environment.DIRECTORY_DOWNLOADS
                        + DBConstants.SEARCH_PHOTO_DIR, place.getPlacePhotoReference());
                file.delete();
            }
        } catch (SQLiteException e) {
            e.getMessage();
        } finally {
            if (db.isOpen())
                db.close();
        }
        return res;
    }

    // returns list with Favorites
    public ArrayList<Place> getFavoriteList() {
        Cursor cursor = null;
        ArrayList<Place> result = new ArrayList<>();
        SQLiteDatabase db = helper.getReadableDatabase();

        try {
            cursor = db.query(DBConstants.FAVORITE_TABLE, null, null, null, null, null, null);
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
        if (cursor != null) {

            cursor.moveToFirst();

            while (!cursor.isAfterLast()) {

                Place place = new Place();

                place.setPlaceName(cursor.getString(cursor.getColumnIndex(DBConstants.NAME)));
                place.setPlaceID(cursor.getString(cursor.getColumnIndex(DBConstants.PLACE_ID)));
                place.setPlaceLocation(new LatLng(cursor.getDouble(cursor.getColumnIndex(DBConstants.LAT)),
                        cursor.getDouble(cursor.getColumnIndex(DBConstants.LNG))));
                place.setPlacePhotoReference(cursor.getString(cursor.getColumnIndex(DBConstants.PHOTO_LINK)));
                place.setPlaceAddress(cursor.getString(cursor.getColumnIndex(DBConstants.ADDRESS)));
                place.setPlacePhoneNumber(cursor.getString(cursor.getColumnIndex(DBConstants.PHONE)));
                place.setPlaceWebsiteUrl(cursor.getString(cursor.getColumnIndex(DBConstants.WEB_SITE)));


                result.add(place);
                cursor.moveToNext();
            }
            cursor.close();
        } else
            result = null;
        return result;
    }

    private void cleanUpPhotoDirectory() {
        String folder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                + DBConstants.SEARCH_PHOTO_DIR;
        File dir = new File(folder);
        if (!dir.exists()) {
            if (dir.mkdirs()) {
                Log.i(NetworkConstants.LOG_TAG, "Photo Directory Created");
            }
        } else {
            File[] files = dir.listFiles();
            if (files != null)
                for (File f : files) {
                    String name = f.getName();
                    if (name.contains("TextSearchRes"))
                        f.delete();
                }
        }
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
                        + DBConstants.SEARCH_PHOTO_DIR, fileName);

        mgr.enqueue(request);
    }

    public void copyFile(File src, File dst) throws IOException {
        InputStream in = new FileInputStream(src);
        OutputStream out = new FileOutputStream(dst);

        // Transfer bytes from in to out
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        in.close();
        out.close();
    }
}
