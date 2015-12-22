package info.duhovniy.maxim.placesresearcher.network;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Created by maxduhovniy on 15/12/2015.
 */
public class Place implements Parcelable {

    private String placeID;
    private LatLng placeLocation;
    private String placeName;

    private String formattedAddress;
    private String formattedPhoneNumber;
    private ArrayList<String> types;
    private String websiteUrl;

    private String photoReference;
    private String mapsUrl;

    private String vicinity;

    public Place(String placeID) {
        this.placeID = placeID;
    }

    protected Place(Parcel in) {
        placeID = in.readString();
        placeLocation = in.readParcelable(LatLng.class.getClassLoader());
        placeName = in.readString();
        formattedAddress = in.readString();
        formattedPhoneNumber = in.readString();
        types = in.createStringArrayList();
        websiteUrl = in.readString();
        photoReference = in.readString();
        mapsUrl = in.readString();
        vicinity = in.readString();
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

    public String getFormattedAddress() {
        return formattedAddress;
    }

    public void setFormattedAddress(String formattedAddress) {
        this.formattedAddress = formattedAddress;
    }

    public String getFormattedPhoneNumber() {
        return formattedPhoneNumber;
    }

    public void setFormattedPhoneNumber(String formattedPhoneNumber) {
        this.formattedPhoneNumber = formattedPhoneNumber;
    }

    public ArrayList<String> getTypes() {
        return types;
    }

    public void setTypes(ArrayList<String> types) {
        this.types = types;
    }

    public String getWebsiteUrl() {
        return websiteUrl;
    }

    public void setWebsiteUrl(String websiteUrl) {
        this.websiteUrl = websiteUrl;
    }

    public String getPhotoReference() {
        return photoReference;
    }

    public void setPhotoReference(String photoReference) {
        this.photoReference = photoReference;
    }

    public String getMapsUrl() {
        return mapsUrl;
    }

    public void setMapsUrl(String mapsUrl) {
        this.mapsUrl = mapsUrl;
    }

    public String getVicinity() {
        return vicinity;
    }

    public void setVicinity(String vicinity) {
        this.vicinity = vicinity;
    }

    @Override
    public String toString() {
        return placeName;
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
        dest.writeString(formattedAddress);
        dest.writeString(formattedPhoneNumber);
        dest.writeStringList(types);
        dest.writeString(websiteUrl);
        dest.writeString(photoReference);
        dest.writeString(mapsUrl);
        dest.writeString(vicinity);
    }
}
