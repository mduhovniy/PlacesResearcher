package info.duhovniy.maxim.placesresearcher.ui;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.model.LatLng;

import info.duhovniy.maxim.placesresearcher.R;
import info.duhovniy.maxim.placesresearcher.db.DBConstants;
import info.duhovniy.maxim.placesresearcher.db.DBHandler;
import info.duhovniy.maxim.placesresearcher.network.Place;

public class ControlFragment extends Fragment implements SearchListAdapter.AdapterInterface {

    private SearchListAdapter searchListAdapter;
    private RecyclerView resultRecyclerView;
    private SwipeRefreshLayout swipeContainer;
    private Cursor lastSearchCursor;
    private ControlInterface mPlaceListener;

    private static ControlFragment instance = null;

    public static ControlFragment getInstance() {
        if (instance == null) {
            instance = new ControlFragment();
        }
        return instance;
    }

    public void setPlaceListener(ControlInterface placeListener) {
        mPlaceListener = placeListener;
    }

    public interface ControlInterface {
        void onPlaceListener(Place place);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_control, container, false);

        swipeContainer = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeContainer);

        resultRecyclerView = (RecyclerView) rootView.findViewById(R.id.search_list_result);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        resultRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        resultRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        searchListAdapter = new SearchListAdapter(resultRecyclerView, this, getContext());

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                onResultListChange();
            }
        });
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        onResultListChange();
    }

    public void onResultListChange() {
        DBHandler db = new DBHandler(getContext());
        lastSearchCursor = db.getLastSearch();
        searchListAdapter.updateList(lastSearchCursor);
        resultRecyclerView.setAdapter(searchListAdapter);
        swipeContainer.setRefreshing(false);
    }

    @Override
    public void itemPressed(int position) {
        if(lastSearchCursor.moveToPosition(position)) {
            Place place = new Place(lastSearchCursor.getString(lastSearchCursor.getColumnIndex(DBConstants.ID)));
            place.setPlaceAddress(lastSearchCursor.getString(lastSearchCursor.getColumnIndex(DBConstants.ADDRESS)));
            place.setPlaceName(lastSearchCursor.getString(lastSearchCursor.getColumnIndex(DBConstants.NAME)));
            place.setPlaceLocation(new LatLng(lastSearchCursor.getDouble(lastSearchCursor.getColumnIndex(DBConstants.LAT)),
                    lastSearchCursor.getDouble(lastSearchCursor.getColumnIndex(DBConstants.LNG))));
//            place.setPlacePhoneNumber(lastSearchCursor.getString(lastSearchCursor.getColumnIndex(DBConstants.PHONE)));
            place.setPlacePhotoReference(lastSearchCursor.getString(lastSearchCursor.getColumnIndex(DBConstants.PHOTO_LINK)));
//            place.setPlaceWebsiteUrl(lastSearchCursor.getString(lastSearchCursor.getColumnIndex(DBConstants.WEB_SITE)));

            mPlaceListener.onPlaceListener(place);
        }

/*
        Intent intent = new Intent(getContext(), MyMapActivity.class);
        if (lastSearchCursor.getDouble(lastSearchCursor.getColumnIndex(DBConstants.LAT)) != 0) {
            intent.putExtra(UIConstants.LAT,
                    lastSearchCursor.getDouble(lastSearchCursor.getColumnIndex(DBConstants.LAT)));
            intent.putExtra(UIConstants.LNG,
                    lastSearchCursor.getDouble(lastSearchCursor.getColumnIndex(DBConstants.LNG)));
        }
        getContext().startActivity(intent);
*/
    }
}
