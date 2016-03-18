package info.duhovniy.maxim.placesresearcher.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import info.duhovniy.maxim.placesresearcher.R;
import info.duhovniy.maxim.placesresearcher.db.DBHandler;

public class ControlFragment extends Fragment {

    private SearchListAdapter searchListAdapter;
    private RecyclerView resultRecyclerView;
    private SwipeRefreshLayout swipeContainer;

    private static ControlFragment instance = null;

    private View rootView;

    public static ControlFragment getInstance() {
        if (instance == null) {
            instance = new ControlFragment();
        }
        return instance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_control, container, false);

        swipeContainer = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeContainer);

        resultRecyclerView = (RecyclerView) rootView.findViewById(R.id.search_list_result);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        resultRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        resultRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        searchListAdapter = new SearchListAdapter(resultRecyclerView, getContext());

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
        onResultListChange();
        super.onResume();
    }

    public void onResultListChange() {
        DBHandler db = new DBHandler(getContext());
        searchListAdapter.updateList(db.getLastSearch());
        resultRecyclerView.setAdapter(searchListAdapter);
        swipeContainer.setRefreshing(false);
    }

}
