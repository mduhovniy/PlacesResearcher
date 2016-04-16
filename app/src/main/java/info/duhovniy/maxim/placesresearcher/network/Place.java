package info.duhovniy.maxim.placesresearcher.network;

import android.content.ContentValues;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

import info.duhovniy.maxim.placesresearcher.db.DBConstants;


public class Place implements Parcelable, ClusterItem {

    private String placeID;
    private LatLng placeLocation;
    private String placeName;
    private String placeAddress;
    private String placePhoneNumber;
    private String placeType;
    private String placeWebsiteUrl;
    private String placePhotoReference;

    public Place() {
    }

    public Place(String placeID) {
        this.placeID = placeID;
    }

    protected Place(Parcel in) {
        placeID = in.readString();
        placeLocation = in.readParcelable(LatLng.class.getClassLoader());
        placeName = in.readString();
        placeAddress = in.readString();
        placePhoneNumber = in.readString();
        placeType = in.readString();
        placeWebsiteUrl = in.readString();
        placePhotoReference = in.readString();
    }

    public static final Creator<Place> CREATOR = new Creator<Place>() {
        @Override
        public Place createFromParcel(Parcel in) {
            return new Place(in);
        }

        @Override
        public Place[] newArray(int size) {
            return new Place[size];
        }
    };

    public String getPlaceID() {
        return placeID;
    }

    public void setPlaceID(String placeID) {
        this.placeID = placeID;
    }

    public LatLng getPlaceLocation() {
        return placeLocation;
    }

    public void setPlaceLocation(LatLng placeLocation) {
        this.placeLocation = placeLocation;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public String getPlaceAddress() {
        return placeAddress;
    }

    public void setPlaceAddress(String placeAddress) {
        this.placeAddress = placeAddress;
    }

    public String getPlacePhoneNumber() {
        return placePhoneNumber;
    }

    public void setPlacePhoneNumber(String placePhoneNumber) {
        this.placePhoneNumber = placePhoneNumber;
    }

    public String getPlaceType() {
        return placeType;
    }

    public void setPlaceType(String placeType) {
        this.placeType = placeType;
    }

    public String getPlaceWebsiteUrl() {
        return placeWebsiteUrl;
    }

    public void setPlaceWebsiteUrl(String placeWebsiteUrl) {
        this.placeWebsiteUrl = placeWebsiteUrl;
    }

    public String getPlacePhotoReference() {
        return placePhotoReference;
    }

    public void setPlacePhotoReference(String placePhotoReference) {
        this.placePhotoReference = placePhotoReference;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(placeID);
        dest.writeParcelable(placeLocation, flags);
        dest.writeString(placeName);
        dest.writeString(placeAddress);
        dest.writeString(placePhoneNumber);
        dest.writeString(placeType);
        dest.writeString(placeWebsiteUrl);
        dest.writeString(placePhotoReference);
    }

    @Override
    public LatLng getPosition() {
        return placeLocation;
    }

    public ContentValues toContentValues() {
        ContentValues values = new ContentValues();

        values.put(DBConstants.PLACE_ID, getPlaceID());
        values.put(DBConstants.LAT, getPlaceLocation().latitude);
        values.put(DBConstants.LNG, getPlaceLocation().longitude);
        values.put(DBConstants.NAME, getPlaceName());
        values.put(DBConstants.ADDRESS, getPlaceAddress());
        values.put(DBConstants.PHONE, getPlacePhoneNumber());
//        values.put(DBConstants.TYPE, getPlaceType());
        values.put(DBConstants.WEB_SITE, getPlaceWebsiteUrl());
        values.put(DBConstants.PHOTO_LINK, getPlacePhotoReference());

        return values;
    }
}
