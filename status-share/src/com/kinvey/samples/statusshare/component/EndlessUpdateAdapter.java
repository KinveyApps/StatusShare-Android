/*
 * Copyright (c) 2012 Kinvey, Inc. All rights reserved.
 *
 * Licensed to Kinvey, Inc. under one or more contributor
 * license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership.  Kinvey, Inc. licenses this file to you under the
 * Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You
 * may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */

package com.kinvey.samples.statusshare.component;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;

import com.kinvey.java.Query;
import com.kinvey.samples.statusshare.R;
import com.kinvey.samples.statusshare.model.Update;

/**
 * Adapter for Endlessly drawing Status Updates.
 * <p/>
 * This Adapter is used to maintain data and push individual row views to
 * the ListView object, note it constructs the Views used by each row and
 * uses the ViewHolder pattern.
 */
public class EndlessUpdateAdapter extends EndlessAdapter {

    public EndlessUpdateAdapter(Context context,List<Update> updates, LayoutInflater inf) {
        super(context, new UpdateAdapter(context, updates, inf), R.layout.row_endless, false);
    }

    @Override
    protected boolean cacheInBackground() throws Exception {
        Query q = new Query();
        q.setSkip(getWrappedAdapter().getCount());

        //get

        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected void appendCachedData() {
        //To change body of implemented methods use File | Settings | File Templates.
    }



}


//            updateView.text = (TextView) rowView.findViewById(R.id.text);
//            updateView.author = (TextView) rowView.findViewById(R.id.author);
//            updateView.when = (TextView) rowView.findViewById(R.id.when);
//            updateView.attachment = (ImageView) rowView.findViewById(R.id.attachment);


//
//
//
//        public EndlessUpdateAdapter(Context context, List<Update> objects,
//                              LayoutInflater inf) {
//            // NOTE: I pass an arbitrary textViewResourceID to the super
//            // constructor-- Below I override
//            // getView(...), which causes the underlying adapter to ignore this
//            // field anyways, it is just needed in the constructor.
//            super(context, R.id.text, objects);
//            this.mInflater = inf;
//
//        }
//
//    @Override
//    protected boolean cacheInBackground() throws Exception {
//        return false;  //To change body of implemented methods use File | Settings | File Templates.
//    }
//
//    @Override
//    protected void appendCachedData() {
//        //To change body of implemented methods use File | Settings | File Templates.
//    }
//
//    @Override
//        public View getView(int position, View convertView, ViewGroup parent) {
//            FeatureViewHolder holder = null;
//
//            TextView name = null;
//            TextView blurb = null;
//
//            Loader.Feature rowData = getItem(position);
//
//            if (null == convertView) {
//                convertView = mInflater.inflate(R.layout.update_list_item, null);
//                holder = new FeatureViewHolder(convertView);
//                convertView.setTag(holder);
//            }
//            holder = (FeatureViewHolder) convertView.getTag();
//
//            name = holder.getName();
//            name.setText(rowData.getName());
//            blurb = holder.getBlurb();
//            blurb.setText(rowData.getBlurb());
//
//            return convertView;
//        }
//
//        /**
//         * This pattern is used as an optimization for Android ListViews.
//         *
//         * Since every row uses the same layout, the View object itself can be
//         * recycled, only the data/content of the row has to be updated.
//         *
//         * This allows for Android to only inflate enough Row Views to fit on
//         * screen, and then they are recycled. This allows us to avoid creating
//         * a new view for every single row, which can have a negative effect on
//         * performance (especially with large lists on large screen devices).
//         *
//         */
//        private class FeatureViewHolder {
//            private View mRow;
//
//            private TextView tvName = null;
//            private TextView tvBlurb = null;
//
//            public FeatureViewHolder(View row) {
//                mRow = row;
//            }
//
//            public TextView getName() {
//                if (null == tvName) {
//                    tvName = (TextView) mRow.findViewById(R.id.row_feature_name);
//                }
//                return tvName;
//            }
//
//            public TextView getBlurb() {
//                if (null == tvBlurb) {
//                    tvBlurb = (TextView) mRow.findViewById(R.id.row_feature_blurb);
//                }
//                return tvBlurb;
//            }
//
//
//
//        }
//    }
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//    private final List<Update> updates;
//
//    public EndlessUpdateAdapter(Activity activity, List<Update> objects) {
//        super(activity, R.layout.update_list_item , objects);
////        android.util.Log.d(TAG, "EndlessUpdateAdapter::constructor");
//        this.updates = objects;
//    }
//
//
//
//
//
//
//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        View rowView = convertView;
//        UpdateView updateView = null;
//
//        if (rowView == null) {
//            // Get a new instance of the row layout view
//            LayoutInflater inflater = ((SherlockActivity)getContext().getSystemService(LayoutInflater.);
//            rowView = inflater.inflate(R.layout.update_list_item, null);
//
//            // Hold the view objects in an object,
//            // so they don't need to be re-fetched
//            updateView = new UpdateView();
//            updateView.avatar = (ImageView) rowView.findViewById(R.id.avatar);
//            updateView.text = (TextView) rowView.findViewById(R.id.text);
//            updateView.author = (TextView) rowView.findViewById(R.id.author);
//            updateView.when = (TextView) rowView.findViewById(R.id.when);
//            updateView.attachment = (ImageView) rowView.findViewById(R.id.attachment);
//
//            // Cache the view objects in the tag,
//            // so they can be re-accessed later
//            rowView.setTag(updateView);
//        } else {
//            updateView = (UpdateView) rowView.getTag();
//        }
//
//        // Transfer the test data from the data object
//        // to the view objects
//        Update currentUpdate = (Update) updates.get(position);
//        updateView.avatar.setImageBitmap(currentUpdate.getAvatar());
//        updateView.text.setText(currentUpdate.getText());
//        updateView.author.setText(currentUpdate.getAuthorName());
//        updateView.when.setText(currentUpdate.getSince());
//        updateView.attachment.setImageBitmap(currentUpdate.getThumbnail());
////        android.util.Log.d(TAG, "ua.java" + currentUpdate.getThumbnail().getHeight() + " x " + currentUpdate.getThumbnail().getWidth());
//        return rowView;
//    }
//
//    protected static class UpdateView {
//        protected ImageView avatar;
//        protected TextView text;
//        protected TextView author;
//        protected TextView when;
//        protected ImageView attachment;
//    }
//
//}
