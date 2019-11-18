/**
 * Copyright (c) 2019, Kinvey, Inc. All rights reserved.
 *
 * This software contains valuable confidential and proprietary information of
 * KINVEY, INC and is subject to applicable licensing agreements.
 * Unauthorized reproduction, transmission or distribution of this file and its
 * contents is a violation of applicable laws.
 *
 */
package com.kinvey.sample.statusshare.ui.component

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.kinvey.sample.statusshare.utils.Constants.ECT_FIELD_NAME
import com.kinvey.sample.statusshare.utils.Constants.SERVER_DATE_FMT
import com.kinvey.sample.statusshare.ui.MainActivity
import com.kinvey.sample.statusshare.R.id
import com.kinvey.sample.statusshare.R.layout
import com.kinvey.sample.statusshare.model.CommentEntity
import org.json.JSONException
import org.json.JSONObject
import timber.log.Timber
import java.text.ParsePosition
import java.text.SimpleDateFormat
import java.util.*

class CommentAdapter // NOTE: I pass an arbitrary textViewResourceID to the super
// constructor-- Below I override
// getView(...), which causes the underlying adapter to ignore this
// field anyways, it is just needed in the constructor.
(context: Context?, objects: List<CommentEntity?>, private val mInflater: LayoutInflater) 
    : ArrayAdapter<CommentEntity?>(context!!, 0, objects) {
    
    private val format = SimpleDateFormat(SERVER_DATE_FMT, Locale.US)
    private val zeroPp = ParsePosition(0)
    
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        var holder: UpdateViewHolder? = null
        var commentText: TextView? = null
        var authorText: TextView? = null
        var whenText: TextView? = null
        val rowData = getItem(position)
        if (null == convertView) {
            convertView = mInflater.inflate(layout.row_comment, null)
            holder = UpdateViewHolder(convertView)
            convertView.tag = holder
        }
        holder = convertView?.tag as UpdateViewHolder
        if (rowData?.text != null) {
            commentText = holder.blurb
            commentText?.text = rowData.text
        }
        if (rowData?.author != null) {
            authorText = holder?.author
            authorText?.text = rowData.author
        }
        val dateString = rowData?.meta.toString()
        try {
            val jsonObj = JSONObject(dateString)
            val ectDateStr: String = jsonObj.getString(ECT_FIELD_NAME)
            val date: Date? = format.parse(ectDateStr, zeroPp)
            val since: String? = MainActivity.getSince(date, Calendar.getInstance())
            if (since != null) {
                whenText = holder?.whenText
                whenText?.text = since
            }
        } catch (e: JSONException) {
            Timber.d("comment JSONException")
        }
        return convertView
    }

    /**
     * This pattern is used as an optimization for Android ListViews.
     *
     *
     * Since every row uses the same layout, the View object itself can be
     * recycled, only the data/content of the row has to be updated.
     *
     *
     * This allows for Android to only inflate enough Row Views to fit on
     * screen, and then they are recycled. This allows us to avoid creating
     * a new view for every single row, which can have a negative effect on
     * performance (especially with large lists on large screen devices).
     */
    private inner class UpdateViewHolder(private val rowView: View) {
        var blurb: TextView? = null
            get() {
                if (null == field) {
                    field = rowView.findViewById<View>(id.row_comment_text) as TextView
                }
                return field
            }
            private set
        var author: TextView? = null
            get() {
                if (null == field) {
                    field = rowView.findViewById<View>(id.row_comment_author) as TextView
                }
                return field
            }
            private set
        var whenText: TextView? = null
            get() {
                if (null == field) {
                    field = rowView.findViewById<View>(id.row_comment_time) as TextView
                }
                return field
            }
            private set
    }
}