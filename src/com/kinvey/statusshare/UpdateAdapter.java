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
 * Author: Tom Giesberg
 */

package com.kinvey.statusshare;

import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class UpdateAdapter extends ArrayAdapter<Update> {
    public static final String TAG = UpdateAdapter.class.getSimpleName();

    private final Activity activity;
    private final List<Update> updates;

    public UpdateAdapter(Activity activity, List<Update> objects) {
        super(activity, R.layout.update_list_item , objects);
        //android.util.Log.d(TAG, "UpdateAdapter::constructor");
        this.activity = activity;
        this.updates = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        UpdateView updateView = null;

        if (rowView == null) {
            // Get a new instance of the row layout view
            LayoutInflater inflater = activity.getLayoutInflater();
            rowView = inflater.inflate(R.layout.update_list_item, null);

            // Hold the view objects in an object,
            // so they don't need to be re-fetched
            updateView = new UpdateView();
            updateView.avatar = (ImageView) rowView.findViewById(R.id.avatar);
            updateView.text = (TextView) rowView.findViewById(R.id.text);
            updateView.author = (TextView) rowView.findViewById(R.id.author);
            updateView.when = (TextView) rowView.findViewById(R.id.when);
            updateView.attachment = (ImageView) rowView.findViewById(R.id.attachment);

            // Cache the view objects in the tag,
            // so they can be re-accessed later
            rowView.setTag(updateView);
        } else {
            updateView = (UpdateView) rowView.getTag();
        }

        // Transfer the test data from the data object
        // to the view objects
        Update currentUpdate = (Update) updates.get(position);
        updateView.avatar.setImageBitmap(currentUpdate.getAvatar());
        updateView.text.setText(currentUpdate.getText());
        updateView.author.setText(currentUpdate.getAuthorName());
        updateView.when.setText(currentUpdate.getSince());
        updateView.attachment.setImageBitmap(currentUpdate.getThumbnail());
        //android.util.Log.d(TAG, "ua.java" + currentUpdate.getThumbnail().getHeight() + " x " + currentUpdate.getThumbnail().getWidth());
        return rowView;
    }

    protected static class UpdateView {
        protected ImageView avatar;
        protected TextView text;
        protected TextView author;
        protected TextView when;
        protected ImageView attachment;
    }

}
