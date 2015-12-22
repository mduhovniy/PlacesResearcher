package info.duhovniy.maxim.placesresearcher.ui;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import info.duhovniy.maxim.placesresearcher.R;
import info.duhovniy.maxim.placesresearcher.network.Place;

public class SearchListAdapter extends RecyclerView.Adapter<SearchListAdapter.RecyclerViewHolder> {

    private ArrayList<Place> listPlaces = new ArrayList<>();
    private View mView;
    private Context mContext;

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public class RecyclerViewHolder extends RecyclerView.ViewHolder {

        public TextView placeName, placeID, formattedAddrress;
        public ImageView placePhoto;
        public LinearLayout mLayout;
        public CardView cv;

        // We also create a constructor that accepts the entire item search_list_row
        // and does the view lookups to find each subview
        public RecyclerViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            placeName = (TextView) itemView.findViewById(R.id.place_name_text);
            placeID = (TextView) itemView.findViewById(R.id.place_id_text);
            formattedAddrress = (TextView) itemView.findViewById(R.id.place_address_text);
            placePhoto = (ImageView) itemView.findViewById(R.id.place_image);
            cv = (CardView) itemView.findViewById(R.id.cv);
            cv.setRadius(10);
            mLayout = (LinearLayout) itemView.findViewById(R.id.line_place);

        }
    }

    public SearchListAdapter(ArrayList<Place> list, View v, Context context) {
        listPlaces = list;
        mView = v;
        mContext = context;
    }

    // Involves inflating a layout from XML and returning the holder
    @Override
    public SearchListAdapter.RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        // Inflate the custom layout
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_list_row, parent, false);

        // Return a new holder instance
        return new RecyclerViewHolder(v);

    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(RecyclerViewHolder viewHolder, final int position) {

        // Set item views based on the data model
        viewHolder.placeName.setText(listPlaces.get(position).getPlaceName());
        viewHolder.placeID.setText(listPlaces.get(position).getPlaceID());
        viewHolder.formattedAddrress.setText(listPlaces.get(position).getFormattedAddress());

/*

        // Set image using Picasso library
        Picasso.with(mContext).setIndicatorsEnabled(true);
        Picasso.with(mContext)
                .load(listMovies.get(position).getUrlPoster())
                .placeholder(R.drawable.placeholder)
                .resize(100, 100)
                .centerCrop()
                .into(viewHolder.moviePoster);
*/

        viewHolder.mLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

/*
                // transfer ID to MainActivity
                mEditMovie.editMovie(listMovies.get(position));
*/

                Toast.makeText(mContext, "onClick", Toast.LENGTH_LONG).show();
            }

        });

        viewHolder.mLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                final View.OnClickListener clickListener = new View.OnClickListener() {
                    public void onClick(View v) {
/*
                        listMovies.remove(position);
                        notifyDataSetChanged();
*/
                    }
                };

                Snackbar.make(mView, "Delete " + listPlaces.get(position).getPlaceName() + "?",
                        Snackbar.LENGTH_LONG).setAction("Ok", clickListener).show();

                return true;
            }
        });

    }


    // Return the total count of items
    @Override
    public int getItemCount() {
        return listPlaces.size();
    }

}
