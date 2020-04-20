package com.amit.popularmovies.reviews

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.amit.popularmovies.R
import java.util.*

class ReviewAdapter(context: Context?, layoutResourceId: Int, reviews: ArrayList<MovieReviewModel>?) : ArrayAdapter<MovieReviewModel>(context!!, layoutResourceId, reviews!!) {
    private var mReviewData = ArrayList<MovieReviewModel>()

    class ViewHolder {
        var author: TextView? = null
        var content: TextView? = null
    }

    override fun getCount(): Int {
        return mReviewData.size
    }

    override fun getView(pos: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        val viewHolder: ViewHolder
        if (convertView == null) {
            viewHolder = ViewHolder()
            val inflater = LayoutInflater.from(context)
            convertView = inflater.inflate(R.layout.list_item_review, parent, false)
            viewHolder.author = convertView.findViewById<View>(R.id.text_view_author) as TextView
            viewHolder.content = convertView.findViewById<View>(R.id.text_view_content) as TextView
            convertView.tag = viewHolder
        } else viewHolder = convertView.tag as ViewHolder
        val movieReviewModel = mReviewData[pos]
        Log.v("ReviewAdapter", movieReviewModel.mAuthor)
        viewHolder.author!!.text = context.getString(R.string.reviewby_text) + " " + movieReviewModel.mAuthor
        viewHolder.content!!.text = movieReviewModel.mContent
        return convertView!!
    }

    init {
        if (reviews != null) {
            mReviewData = reviews
        }
    }
}