package info.duhovniy.maxim.placesresearcher.ui;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.io.File;
import java.util.ArrayList;

import info.duhovniy.maxim.placesresearcher.R;
import info.duhovniy.maxim.placesresearcher.db.DBConstants;
import info.duhovniy.maxim.placesresearcher.db.DBHandler;
import info.duhovniy.maxim.placesresearcher.network.Place;

public class SearchListAdapter extends RecyclerView.Adapter<SearchListAdapter.RecyclerViewHolder> {

    private AdapterInterface mItemListener;
    private ArrayList<Place> mList = null;
    private View mView;
    private Context mContext;

    public interface AdapterInterface {
        void itemPressed(int position);
    }

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public class RecyclerViewHolder extends RecyclerView.ViewHolder {

        public TextView placeName, formattedAddrress;
        public ImageView placePhoto;
        public LinearLayout mLayout;
        public CardView cv;
        public ToggleButton toggleFavorite;

        // We also create a constructor that accepts the entire item search_list_row
        // and does the view lookups to find each subview
        public RecyclerViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            placeName = (TextView) itemView.findViewById(R.id.place_name_text);
            formattedAddrress = (TextView) itemView.findViewById(R.id.place_address_text);
            placePhoto = (ImageView) itemView.findViewById(R.id.place_image);
            cv = (CardView) itemView.findViewById(R.id.cv);
            cv.setRadius(10);
            mLayout = (LinearLayout) itemView.findViewById(R.id.line_place);
            toggleFavorite = (ToggleButton) itemView.findViewById(R.id.toggle_favorite);

        }
    }

    public SearchListAdapter(View v, AdapterInterface itemListener, Context context) {
        mView = v;
        mItemListener = itemListener;
        mContext = context;

    }

    public void updateList(ArrayList<Place> list) {
        mList = list;
        notifyDataSetChanged();
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
    public void onBindViewHolder(final RecyclerViewHolder viewHolder, final int position) {

        // Set item views based on the data model

        viewHolder.placeName.setText(mList.get(position).getPlaceName());
        viewHolder.formattedAddrress.setText(mList.get(position).getPlaceAddress());

        File imageFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                + DBConstants.SEARCH_PHOTO_DIR,
                mList.get(position).getPlacePhotoReference());
        if (imageFile.exists())
            viewHolder.placePhoto.setImageBitmap(BitmapFactory.decodeFile(imageFile.getAbsolutePath()));

        viewHolder.toggleFavorite.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // Add to favorite
                    //viewHolder.toggleFavorite.setChecked(true);
                } else {
                    // Delete from favorite
                    //viewHolder.toggleFavorite.setChecked(false);
                }
            }
        });

        viewHolder.mLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mItemListener.itemPressed(position);

            }

        });

        viewHolder.mLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                final View.OnClickListener clickListener = new View.OnClickListener() {
                    public void onClick(View v) {
                        DBHandler db = new DBHandler(mContext);
                        int rowNumber = db.deleteLastSearchItem(mList.get(position).getPlaceID());
                        mList = db.getLastSearch();
                        notifyDataSetChanged();
                        Snackbar.make(mView, "#" + rowNumber + " places successfully deleted",
                                Snackbar.LENGTH_LONG).show();
                    }
                };

                Snackbar.make(mView, "Delete "
                                + mList.get(position).getPlaceName()
                                + " from list?",
                        Snackbar.LENGTH_LONG).setAction("Ok", clickListener).show();

                return true;
            }
        });

    }

    // Return the total count of items
    @Override
    public int getItemCount() {
        return mList.size();
    }
}
