package com.example.amit.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.amit.popularmovies.data.MoviesContract;
import com.squareup.picasso.Picasso;

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MoviesAdapterViewHolder>{


    private Cursor mCursor;
    final private Context mContext;
    final private MoviesAdapterOnClickHandler mClickHandler;
    final private View mEmptyView;
    final private ItemChoiceManager mICM;

    public class MoviesAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public final TextView nameView;
        public final ImageView posterView;

        public MoviesAdapterViewHolder(View view) {
            super(view);
            nameView = (TextView) view.findViewById(R.id.list_item_name_textview);
            posterView = (ImageView)view.findViewById(R.id.movie_poster);
            view.setOnClickListener(this);

        }

                @Override
                public void onClick(View v) {
                        int adapterPosition = getAdapterPosition();
                        mCursor.moveToPosition(adapterPosition);
                        int idColumnIndex = mCursor.getColumnIndex(MoviesContract.MoviesEntry._ID);
                        int movie_idColumnIndex = mCursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_MOVIE_MOVID);

                        mClickHandler.onClick(mCursor.getInt(idColumnIndex),mCursor.getString(movie_idColumnIndex), this);
                        mICM.onClick(this);
                    }

            }


    public static interface MoviesAdapterOnClickHandler {
        void onClick(int id,String movie_id, MoviesAdapterViewHolder vh);
    }

    public MoviesAdapter(Context context, MoviesAdapterOnClickHandler dh, View emptyView, int choiceMode) {
        mContext = context;
        mClickHandler = dh;
        mEmptyView = emptyView;
        mICM = new ItemChoiceManager(this);
        mICM.setChoiceMode(choiceMode);
    }


    @Override
    public MoviesAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        if ( viewGroup instanceof RecyclerView ) {
            int layoutId = -1;
            layoutId = R.layout.list_item_movies;
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(layoutId, viewGroup, false);
            view.setFocusable(true);
            return new MoviesAdapterViewHolder(view);
        } else {
            throw new RuntimeException("Not bound to RecyclerView");
        }
    }


    @Override
    public void onBindViewHolder(MoviesAdapterViewHolder moviesAdapterViewHolder, int position) {
        mCursor.moveToPosition(position);


        String name = mCursor.getString(MoviesFragment.COL_MOVIES_TITLE);
        String poster_base_url = "http://image.tmdb.org/t/p/w342";
        String posterPath =poster_base_url + mCursor.getString(MoviesFragment.COL_MOVIES_POSTERPATH);
        moviesAdapterViewHolder.nameView.setText(name);

        Picasso.with(mContext)
                .load(posterPath)
                .placeholder(R.drawable.ic_terrain_black_48dp)
                .error(R.drawable.ic_error_black_48dp)
                .into(moviesAdapterViewHolder.posterView);
        mICM.onBindViewHolder(moviesAdapterViewHolder, position);
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        mICM.onRestoreInstanceState(savedInstanceState);
    }

    public void onSaveInstanceState(Bundle outState) {
        mICM.onSaveInstanceState(outState);
    }


    public int getSelectedItemPosition() {
        return mICM.getSelectedItemPosition();
    }


    @Override
    public int getItemCount() {
        if ( null == mCursor ) return 0;
        return mCursor.getCount();
    }

    public void swapCursor(Cursor newCursor) {
        mCursor = newCursor;
        notifyDataSetChanged();
        mEmptyView.setVisibility(getItemCount() == 0 ? View.VISIBLE : View.GONE);
    }

    public Cursor getCursor() {
        return mCursor;
    }
    public void selectView(RecyclerView.ViewHolder viewHolder) {
        if ( viewHolder instanceof MoviesAdapterViewHolder ) {
            MoviesAdapterViewHolder vfh = (MoviesAdapterViewHolder)viewHolder;
            vfh.onClick(vfh.itemView);
        }
    }

}
