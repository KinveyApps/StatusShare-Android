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
import android.widget.TextView;
import com.google.api.client.json.GenericJson;
import com.google.api.client.util.ArrayMap;
import com.kinvey.sample.statusshare.MainActivity;
import com.kinvey.sample.statusshare.R;


import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CommentAdapter extends ArrayAdapter<GenericJson> {

    private LayoutInflater mInflater;
    private Typeface roboto;

    public CommentAdapter(Context context, List<GenericJson> objects,
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

        TextView comment = null;
        TextView author = null;
        TextView when = null;

        GenericJson rowData = getItem(position);

        if (null == convertView) {
            convertView = mInflater.inflate(R.layout.row_comment, null);
            holder = new UpdateViewHolder(convertView);
            convertView.setTag(holder);
        }
        holder = (UpdateViewHolder) convertView.getTag();

        if (rowData.get("text") != null){
            comment = holder.getBlurb();
            comment.setText(rowData.get("text").toString());
        }

        if (rowData.get("author") != null){
            author = holder.getAuthor();
            author.setText(rowData.get("author").toString());

        }



        ParsePosition pp = new ParsePosition(0);
        Date date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.US).parse(((ArrayMap)rowData.get("_kmd")).get("ect").toString(), pp);
        String since = MainActivity.getSince(date, Calendar.getInstance());
        if (since != null){
            when = holder.getWhen();
            when.setText(since);

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
        private View rowView;

        private TextView blurb = null;
        private TextView author = null;
        private TextView when = null;

        public UpdateViewHolder(View row) {
            rowView = row;
        }

        public TextView getWhen() {
            if (null == when) {
                when = (TextView) rowView.findViewById(R.id.row_comment_time);
            }
            when.setTypeface(roboto);
            return when;
        }

        public TextView getAuthor() {
            if (null == author) {
                author = (TextView) rowView.findViewById(R.id.row_comment_author);
            }
            author.setTypeface(roboto);
            return author;
        }

        public TextView getBlurb() {
            if (null == blurb) {
                blurb = (TextView) rowView.findViewById(R.id.row_comment_text);
            }
            blurb.setTypeface(roboto);
            return blurb;
        }

    }
}