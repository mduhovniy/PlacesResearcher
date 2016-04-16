package info.duhovniy.maxim.placesresearcher.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import info.duhovniy.maxim.placesresearcher.R;
import info.duhovniy.maxim.placesresearcher.db.DBHandler;
import info.duhovniy.maxim.placesresearcher.network.Place;

public class ControlFragment extends Fragment implements SearchListAdapter.AdapterInterface {

    private SearchListAdapter searchListAdapter;
    private RecyclerView resultRecyclerView;
    private SwipeRefreshLayout swipeContainer;
    private ArrayList<Place> mList;
    private ControlInterface mPlaceListener;
    private boolean isFavorite;

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
                onResultListChange(isFavorite);
            }
        });
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        onResultListChange(isFavorite);
    }

    public void onResultListChange(boolean isFavorite) {
        this.isFavorite = isFavorite;
        DBHandler db = new DBHandler(getContext());
        if (isFavorite)
            mList = db.getFavoriteList();
        else
            mList = db.getLastSearch();
        searchListAdapter.updateList(mList);
        resultRecyclerView.setAdapter(searchListAdapter);
        swipeContainer.setRefreshing(false);
    }

    @Override
    public void itemPressed(int position) {
        mPlaceListener.onPlaceListener(mList.get(position));
    }
}
