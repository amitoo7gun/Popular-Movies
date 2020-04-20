package com.amit.popularmovies.ui.main

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.amit.popularmovies.R
import com.amit.popularmovies.databinding.ListItemMoviesBinding
import com.amit.popularmovies.model.MovieDiscoverResult
import com.amit.popularmovies.ui.detail.DetailActivity
import com.amit.popularmovies.ui.detail.DetailFragment

class MoviesAdapter : RecyclerView.Adapter<MoviesAdapter.ViewHolder>(){
//class MoviesAdapter internal constructor(private val mContext: Context?, private val mClickHandler: MoviesAdapterOnClickHandler, private val mEmptyView: View, choiceMode: Int) : RecyclerView.Adapter<MoviesAdapterViewHolder>() {

    private lateinit var moviesList:List<MovieDiscoverResult>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ListItemMoviesBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.list_item_movies, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(moviesList[position])
    }

    override fun getItemCount(): Int {
        return if(::moviesList.isInitialized) moviesList.size else 0
    }

    fun updatePostList(moviesList:List<MovieDiscoverResult>){
        this.moviesList = moviesList
        notifyDataSetChanged()
    }

    class ViewHolder(private val binding: ListItemMoviesBinding):RecyclerView.ViewHolder(binding.root), View.OnClickListener{
        private val viewModel = MovieViewModel()

        fun bind(movieDiscoverResult: MovieDiscoverResult){
            viewModel.bind(movieDiscoverResult)
            binding.viewModel = viewModel
        }

        override fun onClick(p0: View?) {
            val intent = Intent(this, DetailActivity::class.java)
            intent.putExtra(DetailFragment.MOVIE_DATA, bi)
            startActivity(intent)
        }
    }

//    private var movieResults: List<MovieDiscoverResult>? = null
//    private val mICM: ItemChoiceManager
//
//    inner class MoviesAdapterViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
//        val nameView: TextView
//        val posterView: ImageView
//
//        override fun onClick(v: View) {
//            val adapterPosition = adapterPosition
//            mClickHandler.onClick(movieResults!![adapterPosition], this)
//            mICM.onClick(this)
//        }
//
//        init {
//            nameView = view.findViewById(R.id.list_item_name_textview)
//            posterView = view.findViewById(R.id.mPosterView)
//            view.setOnClickListener(this)
//        }
//    }
//
    interface MoviesAdapterOnClickHandler {
        fun onClick(movieDiscoverResult: MovieDiscoverResult?)
    }
//
//    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): MoviesAdapterViewHolder {
//        return if (viewGroup is RecyclerView) {
//            var layoutId = -1
//            layoutId = R.layout.list_item_movies
//            val view = LayoutInflater.from(viewGroup.getContext()).inflate(layoutId, viewGroup, false)
//            view.isFocusable = true
//            MoviesAdapterViewHolder(view)
//        } else {
//            throw RuntimeException("Not bound to RecyclerView")
//        }
//    }
//
//    override fun onBindViewHolder(moviesAdapterViewHolder: MoviesAdapterViewHolder, position: Int) {
//        val item = movieResults!![position]
//        val name = item.title
//        val poster_base_url = "http://image.tmdb.org/t/p/w342"
//        val posterPath = poster_base_url + item.poster_path
//        moviesAdapterViewHolder.nameView.text = name
//        Picasso.with(mContext)
//                .load(posterPath)
//                .placeholder(R.drawable.ic_terrain_black_48dp)
//                .error(R.drawable.ic_error_black_48dp)
//                .into(moviesAdapterViewHolder.posterView)
//        mICM.onBindViewHolder(moviesAdapterViewHolder, position)
//    }
//
//    fun onRestoreInstanceState(savedInstanceState: Bundle) {
//        mICM.onRestoreInstanceState(savedInstanceState)
//    }
//
//    fun onSaveInstanceState(outState: Bundle) {
//        mICM.onSaveInstanceState(outState)
//    }
//
//    val selectedItemPosition: Int
//        get() = mICM.selectedItemPosition
//
//    override fun getItemCount(): Int {
//        return if (null == movieResults) 0 else movieResults!!.size
//    }
//
//    fun setMoviesData(newCursor: List<MovieDiscoverResult>?) {
//        movieResults = newCursor
//        notifyDataSetChanged()
//        mEmptyView.visibility = if (itemCount == 0) View.VISIBLE else View.GONE
//    }
//
//    fun selectView(viewHolder: RecyclerView.ViewHolder?) {
//        if (viewHolder is MoviesAdapterViewHolder) {
//            val vfh = viewHolder
//            vfh.onClick(vfh.itemView)
//        }
//    }
//
//    init {
//        mICM = ItemChoiceManager(this)
//        mICM.setChoiceMode(choiceMode)
//    }
}