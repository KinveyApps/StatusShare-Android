/** 
 * Copyright (c) 2013, Kinvey, Inc. All rights reserved.
 *
 * This software contains valuable confidential and proprietary information of
 * KINVEY, INC and is subject to applicable licensing agreements.
 * Unauthorized reproduction, transmission or distribution of this file and its
 * contents is a violation of applicable laws.
 * 
 */
package com.kinvey.sample.statusshare.component;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.kinvey.sample.statusshare.R;
import com.kinvey.sample.statusshare.model.UpdateEntity;


import java.util.List;

public class UpdateAdapter extends ArrayAdapter<UpdateEntity> {

    private LayoutInflater mInflater;
    private Typeface roboto;

    public UpdateAdapter(Context context, List<UpdateEntity> objects,
                         LayoutInflater inf) {
        // NOTE: I pass an arbitrary textViewResourceID to the super
        // constructor-- Below I override
        // getView(...), which causes the underlying adapter to ignore this
        // field anyways, it is just needed in the constructor.
        super(context, 0, objects);
        this.mInflater = inf;
        roboto = Typeface.createFromAsset(context.getAssets(), "Roboto-Thin.ttf");

    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        UpdateViewHolder holder = null;

        TextView mBlurb = null;
        TextView mAuthor = null;
        TextView mWhen = null;
        ImageView mAttachment = null;

        UpdateEntity rowData = getItem(position);

        if (null == convertView) {
            convertView = mInflater.inflate(R.layout.row_update, null);
            holder = new UpdateViewHolder(convertView);
            convertView.setTag(holder);
        }
        holder = (UpdateViewHolder) convertView.getTag();

        if (rowData.getText() != null){
            mBlurb = holder.getBlurb();
            mBlurb.setText(rowData.getText());
        }

        if (rowData.getAuthorName() != null){
            mAuthor = holder.getAuthor();
            mAuthor.setText(rowData.getAuthorName());
        }
        if (rowData.getSince() != null){
            mWhen = holder.getWhen();
            mWhen.setText(rowData.getSince());

        }
        mAttachment = holder.getAttachment();
        if (rowData.getThumbnail() != null){
            mAttachment.setImageBitmap(rowData.getThumbnail());
        }else{
            mAttachment.setBackgroundColor(R.color.ebony);
            mAttachment.setImageBitmap(null);
        }
        return convertView;
    }


    /**
     * This pattern is used as an optimization for Android ListViews.
     * <p/>
     * Since every row uses the same layout, the View object itself can be
     * recycled, only the data/content of the row has to be updated.
     * <p/>
     * This allows for Android to only inflate enough Row Views to fit on
     * screen, and then they are recycled. This allows us to avoid creating
     * a new view for every single row, which can have a negative effect on
     * performance (especially with large lists on large screen devices).
     */
    private class UpdateViewHolder {
        private View mRow;

        private ImageView attachment = null;
        private TextView blurb = null;
        private TextView author = null;
        private TextView when = null;

        public UpdateViewHolder(View row) {
            mRow = row;
        }

        public TextView getWhen() {
            if (null == when) {
                when = (TextView) mRow.findViewById(R.id.row_update_time);
            }
            when.setTypeface(roboto);
            return when;
        }

        public TextView getAuthor() {
            if (null == author) {
                author = (TextView) mRow.findViewById(R.id.row_update_author);
            }
            author.setTypeface(roboto);
            return author;
        }

        public TextView getBlurb() {
            if (null == blurb) {
                blurb = (TextView) mRow.findViewById(R.id.row_update_text);
            }
            blurb.setTypeface(roboto);
            return blurb;
        }

        public ImageView getAttachment() {
            if (null == attachment) {
                attachment = (ImageView) mRow.findViewById(R.id.row_update_image);
            }
            return attachment;
        }
    }
}