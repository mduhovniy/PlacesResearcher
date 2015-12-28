package info.duhovniy.maxim.placesresearcher.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;

import info.duhovniy.maxim.placesresearcher.R;
import info.duhovniy.maxim.placesresearcher.network.NetworkConstants;
import info.duhovniy.maxim.placesresearcher.network.Place;
import info.duhovniy.maxim.placesresearcher.network.search.SearchServiceAutocomplete;
import info.duhovniy.maxim.placesresearcher.network.search.SearchServiceNearby;
import info.duhovniy.maxim.placesresearcher.network.search.SearchServiceRadar;
import info.duhovniy.maxim.placesresearcher.network.search.SearchServiceText;

public class ControlFragment extends Fragment implements View.OnClickListener {

    private PlaceSearchReceiver receiver;

    private ArrayList<Place> resultList = new ArrayList<>();
    private SearchListAdapter searchListAdapter;

    private static ControlFragment instance = null;

    private View rootView;
    private RecyclerView resultRecyclerView;
    private EditText searchText;

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

        searchText = (EditText) rootView.findViewById(R.id.search_text);
        resultRecyclerView = (RecyclerView) rootView.findViewById(R.id.search_list_result);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        resultRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        resultRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        searchListAdapter = new SearchListAdapter(resultList, resultRecyclerView, getContext());
        resultRecyclerView.setAdapter(searchListAdapter);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        IntentFilter filter = new IntentFilter();
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        filter.addAction(NetworkConstants.AUTOCOMPLETE_SEARCH);
        filter.addAction(NetworkConstants.NEARBY_SEARCH);
        filter.addAction(NetworkConstants.RADAR_SEARCH);
        filter.addAction(NetworkConstants.TEXT_SEARCH);

        receiver = new PlaceSearchReceiver();
        getActivity().registerReceiver(receiver, filter);

        Button searchButton1 = (Button) rootView.findViewById(R.id.autocomplete_search_button);
        Button searchButton2 = (Button) rootView.findViewById(R.id.text_search_button);
        Button searchButton3 = (Button) rootView.findViewById(R.id.radar_search_button);
        Button searchButton4 = (Button) rootView.findViewById(R.id.nearby_search_button);

        searchButton1.setOnClickListener(this);
        searchButton2.setOnClickListener(this);
        searchButton3.setOnClickListener(this);
        searchButton4.setOnClickListener(this);
    }

    @Override
    public void onDestroy() {
        getActivity().unregisterReceiver(receiver);
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId())
        {
            case R.id.autocomplete_search_button:
                intent.setClass(getActivity(), SearchServiceAutocomplete.class);
                break;
            case R.id.text_search_button:
                intent.setClass(getActivity(), SearchServiceText.class);
                break;
            case R.id.radar_search_button:
                intent.setClass(getActivity(), SearchServiceRadar.class);
                break;
            case R.id.nearby_search_button:
                intent.setClass(getActivity(), SearchServiceNearby.class);
                break;
        }
        intent.putExtra(NetworkConstants.REQUEST_STRING, searchText.getText().toString());
        getActivity().startService(intent);
    }

    public class PlaceSearchReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            resultList = intent.getBundleExtra(NetworkConstants.RESPONSE_MESSAGE)
                    .getParcelableArrayList(NetworkConstants.RESPONSE_LIST);

            searchListAdapter = new SearchListAdapter(resultList, resultRecyclerView, getContext());
            resultRecyclerView.setAdapter(searchListAdapter);

//            searchListAdapter.updateList(resultList);

            switch (intent.getAction()) {
                case NetworkConstants.AUTOCOMPLETE_SEARCH:
                    rootView.setBackgroundColor(Color.WHITE);
                    break;
                case NetworkConstants.TEXT_SEARCH:
                    rootView.setBackgroundColor(Color.RED);
                    break;
            }

        }
    }
}
