package info.duhovniy.maxim.placesresearcher.ui;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;

import info.duhovniy.maxim.placesresearcher.R;
import info.duhovniy.maxim.placesresearcher.db.DBConstants;

public class SearchListAdapter extends RecyclerView.Adapter<SearchListAdapter.RecyclerViewHolder> {

    private Cursor cursorPlaces = null;
    private View mView;
    private Context mContext;

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public class RecyclerViewHolder extends RecyclerView.ViewHolder {

        public TextView placeName, formattedAddrress;
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
            formattedAddrress = (TextView) itemView.findViewById(R.id.place_address_text);
            placePhoto = (ImageView) itemView.findViewById(R.id.place_image);
            cv = (CardView) itemView.findViewById(R.id.cv);
            cv.setRadius(10);
            mLayout = (LinearLayout) itemView.findViewById(R.id.line_place);

        }
    }

    public SearchListAdapter(View v, Context context) {
        mView = v;
        mContext = context;
    }

    public void updateList(Cursor cursor) {
        cursorPlaces = cursor;
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
    public void onBindViewHolder(RecyclerViewHolder viewHolder, final int position) {

        // Set item views based on the data model
        if (cursorPlaces.moveToPosition(position)) {
            viewHolder.placeName.setText(cursorPlaces.getString(cursorPlaces.getColumnIndex(DBConstants.NAME)));
            viewHolder.formattedAddrress.setText(cursorPlaces.getString(cursorPlaces.getColumnIndex(DBConstants.ADDRESS)));

            File imageFile = new  File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                    + DBConstants.TEXT_SEARCH_PHOTO_DIR,
                    cursorPlaces.getString(cursorPlaces.getColumnIndex(DBConstants.PHOTO_LINK)));
            if(imageFile.exists()){
                viewHolder.placePhoto.setImageBitmap(BitmapFactory.decodeFile(imageFile.getAbsolutePath()));
            }

            viewHolder.mLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(mContext, MyMapActivity.class);
                    if (cursorPlaces.getDouble(cursorPlaces.getColumnIndex(DBConstants.LAT)) != 0) {
                        intent.putExtra(UIConstants.LAT,
                                cursorPlaces.getDouble(cursorPlaces.getColumnIndex(DBConstants.LAT)));
                        intent.putExtra(UIConstants.LNG,
                                cursorPlaces.getDouble(cursorPlaces.getColumnIndex(DBConstants.LNG)));
                    }
                    mContext.startActivity(intent);
                }

            });

            viewHolder.mLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    final View.OnClickListener clickListener = new View.OnClickListener() {
                        public void onClick(View v) {
                        }
                    };

                    Snackbar.make(mView, "Delete "
                                    + cursorPlaces.getString(cursorPlaces.getColumnIndex(DBConstants.NAME))
                                    + "?",
                            Snackbar.LENGTH_LONG).setAction("Ok", clickListener).show();

                    return true;
                }
            });
        }
    }


    // Return the total count of items
    @Override
    public int getItemCount() {
        if (cursorPlaces == null)
            return 0;
        else
            return cursorPlaces.getCount();
    }

}
