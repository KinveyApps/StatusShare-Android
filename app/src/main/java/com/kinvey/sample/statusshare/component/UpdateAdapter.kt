/**
 * Copyright (c) 2019, Kinvey, Inc. All rights reserved.
 *
 * This software contains valuable confidential and proprietary information of
 * KINVEY, INC and is subject to applicable licensing agreements.
 * Unauthorized reproduction, transmission or distribution of this file and its
 * contents is a violation of applicable laws.
 *
 */
package com.kinvey.sample.statusshare.component

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.kinvey.sample.statusshare.R.*
import com.kinvey.sample.statusshare.model.UpdateEntity

class UpdateAdapter 
// NOTE: I pass an arbitrary textViewResourceID to the super
// constructor-- Below I override
// getView(...), which causes the underlying adapter to ignore this
// field anyways, it is just needed in the constructor.
(context: Context, objects: List<UpdateEntity?>, private val mInflater: LayoutInflater) 
    : ArrayAdapter<UpdateEntity?>(context, 0, objects) {
    
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        
        var convertView = convertView
        var holder: UpdateViewHolder? = null
        var mBlurb: TextView? = null
        var mAuthor: TextView? = null
        var mWhen: TextView? = null
        var mAttachment: ImageView? = null
        
        val rowData = getItem(position)
        if (null == convertView) {
            convertView = mInflater.inflate(layout.row_update, null)
            holder = UpdateViewHolder(convertView)
            convertView.tag = holder
        }
        holder = convertView?.tag as UpdateViewHolder
        if (rowData?.text != null) {
            mBlurb = holder.blurb
            mBlurb?.text = rowData.text
        }
        if (rowData?.authorName != null) {
            mAuthor = holder?.author
            mAuthor?.text = rowData.authorName
        }
        if (rowData?.since != null) {
            mWhen = holder?.whenText
            mWhen?.text = rowData.since
        }
        mAttachment = holder?.attachment
        if (rowData?.thumbnail != null) {
            mAttachment?.setImageBitmap(rowData.thumbnail)
        } else {
            mAttachment?.setBackgroundColor(color.ebony)
            mAttachment?.setImageBitmap(null)
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
    private inner class UpdateViewHolder(private val mRow: View) {
        var attachment: ImageView? = null
            get() {
                if (null == field) {
                    field = mRow.findViewById<View>(id.row_update_image) as ImageView
                }
                return field
            }
            private set
        var blurb: TextView? = null
            get() {
                if (null == field) {
                    field = mRow.findViewById<View>(id.row_update_text) as TextView
                }
                return field
            }
            private set
        var author: TextView? = null
            get() {
                if (null == field) {
                    field = mRow.findViewById<View>(id.row_update_author) as TextView
                }
                return field
            }
            private set
        var whenText: TextView? = null
            get() {
                if (null == field) {
                    field = mRow.findViewById<View>(id.row_update_time) as TextView
                }
                return field
            }
            private set
    }
}