/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.amit.popularmovies

import android.os.Bundle
import android.os.Parcel
import android.util.Log
import android.util.SparseBooleanArray
import android.widget.AbsListView
import android.widget.Checkable
import androidx.collection.LongSparseArray
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
import com.example.amit.popularmovies.MainActivity

/**
 * The ItemChoiceManager class keeps track of which positions have been selected.  Note that it
 * doesn't take advantage of new adapter features to track changes in the underlying data.
 */
class ItemChoiceManager {
    private val LOG_TAG = MainActivity::class.java.simpleName
    private val SELECTED_ITEMS_KEY = "SIK"
    private var mChoiceMode = 0
    private lateinit var mAdapter: RecyclerView.Adapter<*>
    private val mAdapterDataObserver: AdapterDataObserver = object : AdapterDataObserver() {
        override fun onChanged() {
            super.onChanged()
            if (mAdapter != null && mAdapter.hasStableIds()) confirmCheckedPositionsById(mAdapter.getItemCount())
        }
    }

    private constructor() {}
    constructor(adapter: RecyclerView.Adapter<*>?) {
        mAdapter = adapter!!
    }

    /**
     * Running state of which positions are currently checked
     */
    var mCheckStates: SparseBooleanArray? = SparseBooleanArray()

    /**
     * Running state of which IDs are currently checked.
     * If there is a value for a given key, the checked state for that ID is true
     * and the value holds the last known position in the adapter for that id.
     */
    var mCheckedIdStates = LongSparseArray<Int>()
    fun onClick(vh: RecyclerView.ViewHolder) {
        if (mChoiceMode == AbsListView.CHOICE_MODE_NONE) return
        val checkedItemCount = mCheckStates!!.size()
        val position = vh.adapterPosition
        if (position == RecyclerView.NO_POSITION) {
            Log.d(LOG_TAG, "Unable to Set Item State")
            return
        }
        when (mChoiceMode) {
            AbsListView.CHOICE_MODE_NONE -> {
            }
            AbsListView.CHOICE_MODE_SINGLE -> {
                val checked = mCheckStates!![position, false]
                if (!checked) {
                    var i = 0
                    while (i < checkedItemCount) {
                        mAdapter!!.notifyItemChanged(mCheckStates!!.keyAt(i))
                        i++
                    }
                    mCheckStates!!.clear()
                    mCheckStates!!.put(position, true)
                    mCheckedIdStates.clear()
                    mCheckedIdStates.put(mAdapter!!.getItemId(position), position)
                }
                // We directly call onBindViewHolder here because notifying that an item has
                // changed on an item that has the focus causes it to lose focus, which makes
                // keyboard navigation a bit annoying
                mAdapter!!.onBindViewHolder(vh as Nothing, position)
            }
            AbsListView.CHOICE_MODE_MULTIPLE -> {
                val checked = mCheckStates!![position, false]
                mCheckStates!!.put(position, !checked)
                // We directly call onBindViewHolder here because notifying that an item has
                // changed on an item that has the focus causes it to lose focus, which makes
                // keyboard navigation a bit annoying
                mAdapter!!.onBindViewHolder(vh as Nothing, position)
            }
            AbsListView.CHOICE_MODE_MULTIPLE_MODAL -> {
                throw RuntimeException("Multiple Modal not implemented in ItemChoiceManager.")
            }
        }
    }

    /**
     * Defines the choice behavior for the RecyclerView. By default, RecyclerViewChoiceMode does
     * not have any choice behavior (AbsListView.CHOICE_MODE_NONE). By setting the choiceMode to
     * AbsListView.CHOICE_MODE_SINGLE, the RecyclerView allows up to one item to  be in a
     * chosen state.
     *
     * @param choiceMode One of AbsListView.CHOICE_MODE_NONE, AbsListView.CHOICE_MODE_SINGLE
     */
    fun setChoiceMode(choiceMode: Int) {
        if (mChoiceMode != choiceMode) {
            mChoiceMode = choiceMode
            clearSelections()
        }
    }

    /**
     * Returns the checked state of the specified position. The result is only
     * valid if the choice mode has been set to AbsListView.CHOICE_MODE_SINGLE,
     * but the code does not check this.
     *
     * @param position The item whose checked state to return
     * @return The item's checked state
     * @see .setChoiceMode
     */
    fun isItemChecked(position: Int): Boolean {
        return mCheckStates!![position]
    }

    fun clearSelections() {
        mCheckStates!!.clear()
        mCheckedIdStates.clear()
    }

    fun confirmCheckedPositionsById(oldItemCount: Int) {
        // Clear out the positional check states, we'll rebuild it below from IDs.
        mCheckStates!!.clear()
        var checkedIndex = 0
        while (checkedIndex < mCheckedIdStates.size()) {
            val id = mCheckedIdStates.keyAt(checkedIndex)
            val lastPos = mCheckedIdStates.valueAt(checkedIndex)
            val lastPosId = mAdapter!!.getItemId(lastPos)
            if (id != lastPosId) {
                // Look around to see if the ID is nearby. If not, uncheck it.
                val start = Math.max(0, lastPos - CHECK_POSITION_SEARCH_DISTANCE)
                val end = Math.min(lastPos + CHECK_POSITION_SEARCH_DISTANCE, oldItemCount)
                var found = false
                for (searchPos in start until end) {
                    val searchId = mAdapter!!.getItemId(searchPos)
                    if (id == searchId) {
                        found = true
                        mCheckStates!!.put(searchPos, true)
                        mCheckedIdStates.setValueAt(checkedIndex, searchPos)
                        break
                    }
                }
                if (!found) {
                    mCheckedIdStates.delete(id)
                    checkedIndex--
                }
            } else {
                mCheckStates!!.put(lastPos, true)
            }
            checkedIndex++
        }
    }

    fun onBindViewHolder(vh: RecyclerView.ViewHolder, position: Int) {
        val checked = isItemChecked(position)
        if (vh.itemView is Checkable) {
            (vh.itemView as Checkable).isChecked = checked
        }
        ViewCompat.setActivated(vh.itemView, checked)
    }

    fun onRestoreInstanceState(savedInstanceState: Bundle) {
        val states = savedInstanceState.getByteArray(SELECTED_ITEMS_KEY)
        if (null != states) {
            val inParcel = Parcel.obtain()
            inParcel.unmarshall(states, 0, states.size)
            inParcel.setDataPosition(0)
            mCheckStates = inParcel.readSparseBooleanArray()
            val numStates = inParcel.readInt()
            mCheckedIdStates.clear()
            for (i in 0 until numStates) {
                val key = inParcel.readLong()
                val value = inParcel.readInt()
                mCheckedIdStates.put(key, value)
            }
        }
    }

    fun onSaveInstanceState(outState: Bundle) {
        val outParcel = Parcel.obtain()
        outParcel.writeSparseBooleanArray(mCheckStates)
        val numStates = mCheckedIdStates.size()
        outParcel.writeInt(numStates)
        for (i in 0 until numStates) {
            outParcel.writeLong(mCheckedIdStates.keyAt(i))
            outParcel.writeInt(mCheckedIdStates.valueAt(i))
        }
        val states = outParcel.marshall()
        outState.putByteArray(SELECTED_ITEMS_KEY, states)
        outParcel.recycle()
    }

    val selectedItemPosition: Int
        get() = if (mCheckStates!!.size() == 0) {
            RecyclerView.NO_POSITION
        } else {
            mCheckStates!!.keyAt(0)
        }

    companion object {
        /**
         * How many positions in either direction we will search to try to
         * find a checked item with a stable ID that moved position across
         * a data set change. If the item isn't found it will be unselected.
         */
        private const val CHECK_POSITION_SEARCH_DISTANCE = 20
    }
}