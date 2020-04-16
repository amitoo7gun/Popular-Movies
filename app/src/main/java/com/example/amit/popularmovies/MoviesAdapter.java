package com.example.amit.popularmovies;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.amit.popularmovies.model.MovieDiscoverResult;
import com.squareup.picasso.Picasso;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MoviesAdapterViewHolder>{


    private List<MovieDiscoverResult> movieResults;
    final private Context mContext;
    final private MoviesAdapterOnClickHandler mClickHandler;
    final private View mEmptyView;
    final private ItemChoiceManager mICM;

    public class MoviesAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public final TextView nameView;
        public final ImageView posterView;

        public MoviesAdapterViewHolder(View view) {
            super(view);
            nameView = view.findViewById(R.id.list_item_name_textview);
            posterView = view.findViewById(R.id.movie_poster);
            view.setOnClickListener(this);

        }

                @Override
                public void onClick(View v) {
                        int adapterPosition = getAdapterPosition();
                        mClickHandler.onClick(movieResults.get(adapterPosition), this);
                        mICM.onClick(this);
                    }

            }


    public static interface MoviesAdapterOnClickHandler {
        void onClick(MovieDiscoverResult movieDiscoverResult, MoviesAdapterViewHolder vh);
    }

    MoviesAdapter(Context context, MoviesAdapterOnClickHandler dh, View emptyView, int choiceMode) {
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
        MovieDiscoverResult item = movieResults.get(position);


        String name = item.getTitle();
        String poster_base_url = "http://image.tmdb.org/t/p/w342";
        String posterPath =poster_base_url + item.getPosterPath();
        moviesAdapterViewHolder.nameView.setText(name);

        Picasso.with(mContext)
                .load(posterPath)
                .placeholder(R.drawable.ic_terrain_black_48dp)
                .error(R.drawable.ic_error_black_48dp)
                .into(moviesAdapterViewHolder.posterView);
        mICM.onBindViewHolder(moviesAdapterViewHolder, position);
    }

    void onRestoreInstanceState(Bundle savedInstanceState) {
        mICM.onRestoreInstanceState(savedInstanceState);
    }

    void onSaveInstanceState(Bundle outState) {
        mICM.onSaveInstanceState(outState);
    }


    int getSelectedItemPosition() {
        return mICM.getSelectedItemPosition();
    }


    @Override
    public int getItemCount() {
        if ( null == movieResults) return 0;
        return movieResults.size();
    }

    void setMoviesData(List<MovieDiscoverResult> newCursor) {
        movieResults = newCursor;
        notifyDataSetChanged();
        mEmptyView.setVisibility(getItemCount() == 0 ? View.VISIBLE : View.GONE);
    }

    void selectView(RecyclerView.ViewHolder viewHolder) {
        if ( viewHolder instanceof MoviesAdapterViewHolder ) {
            MoviesAdapterViewHolder vfh = (MoviesAdapterViewHolder)viewHolder;
            vfh.onClick(vfh.itemView);
        }
    }

}
