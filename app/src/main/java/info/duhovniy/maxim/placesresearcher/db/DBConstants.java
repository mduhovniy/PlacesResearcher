package info.duhovniy.maxim.placesresearcher.db;

/**
 * Created by maxduhovniy on 25/02/2016.
 */
public class DBConstants {

    // Tables fields list
    public static final String ID = "_id";
    public static final String NAME = "name";
    public static final String LAT = "lat";
    public static final String LNG = "lng";
    public static final String PLACE_ID = "place_id";
    public static final String PHOTO_LINK = "photo";
    public static final String ADDRESS = "address";
    public static final String PHONE = "phone";
    public static final String WEB_SITE = "website";
    public static final String TYPE = "type";

    // Data base and tables constants
    public static final String DB_NAME = "Places.db";
    public static final int DB_VERSION = 1;

    public static final String LAST_SEARCH_TABLE = "LastSearch";
    public static final String LAST_SEARCH_CREATE_QUERY = "CREATE TABLE IF NOT EXISTS "
            + LAST_SEARCH_TABLE + " ("
            + ID + " INTEGER PRIMARY KEY, " + NAME + " TEXT, "
            + PLACE_ID + " TEXT," + LAT + " REAL, " + LNG + " REAL, " + PHOTO_LINK + " TEXT,"
            + ADDRESS + " TEXT," + TYPE + " TEXT);";

    public static final String FAVORITE_TABLE = "Favorite";
    public static final String FAVORITE_CREATE_QUERY = "CREATE TABLE IF NOT EXISTS "
            + FAVORITE_TABLE + " ("
            + ID + " INTEGER PRIMARY KEY, " + NAME + " TEXT, "
            + PLACE_ID + " TEXT,"+ LAT + " REAL, " + LNG + " REAL, " + PHOTO_LINK + " TEXT,"
            + ADDRESS + " TEXT," + PHONE + " TEXT," + WEB_SITE + " TEXT);";

    public static final String[] CREATE_ALL = new String[]{LAST_SEARCH_CREATE_QUERY,
            FAVORITE_CREATE_QUERY};

    public static final String[] TABLES = new String[]{LAST_SEARCH_TABLE, FAVORITE_TABLE};

    public static final String SEARCH_PHOTO_DIR = "/SearchPhotos";
}
