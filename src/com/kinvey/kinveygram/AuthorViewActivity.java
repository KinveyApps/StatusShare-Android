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

package com.kinvey.kinveygram;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.kinvey.KCSClient;
import com.kinvey.MappedAppdata;
import com.kinvey.exception.KinveyException;
import com.kinvey.persistence.query.SimpleQuery;
import com.kinvey.persistence.query.LimitQueryModifier;
import com.kinvey.persistence.query.SortQueryModifier;
import com.kinvey.util.ListCallback;

public class AuthorViewActivity extends Activity {
    public static final String TAG = AuthorViewActivity.class.getSimpleName();
    public static final Integer UPDATES_LIST_SIZE = 5;

    protected KCSClient mSharedClient;
    private Calendar mCalendar;
    private List<Update> mUpdates;
    private Friend mFriend;

    static final Comparator<Update> LATEST_FIRST_ORDER = new Comparator<Update>() {
        public int compare(Update u1, Update u2) {
            return u2.getDate().compareTo(u1.getDate());
        }
    };

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSharedClient = ((KinveyGramApp) getApplication()).getKinveyService();
        mCalendar = ((KinveyGramApp) getApplication()).getAppCalendar();

        setContentView(R.layout.author_view);
        Intent myIntent = getIntent();
        mFriend = new Friend(myIntent.getStringExtra("authorId"), myIntent.getStringExtra("authorName"));

        ImageView avatar = (ImageView) findViewById(R.id.avatar);
        avatar.setImageBitmap(mFriend.getAvatar());

        TextView author = (TextView) findViewById(R.id.author);
        author.setText(mFriend.getName());

        updateList();
    }

    public void updateList() {
        final ListView lv = (ListView) findViewById(R.id.updateList);
        if (lv.getAdapter() != null) {
            ((UpdateAdapter) lv.getAdapter()).clear();
        }
        mUpdates = new ArrayList<Update>();

        SimpleQuery q = new SimpleQuery();
        q.addCriteria("_acl.creator", "==", mFriend.getId());
        //FIXME
        q.setSortQueryModifier(new SortQueryModifier ("_acl.lmt", true));
        q.setLimitQueryModifier(new LimitQueryModifier (UPDATES_LIST_SIZE));
        android.util.Log.v(TAG, q.toString());

        MappedAppdata mappedAppdata = mSharedClient.mappeddata("Updates");
        mappedAppdata.setQuery(q);
        mappedAppdata.fetch(UpdateEntity.class, new ListCallback<UpdateEntity>() {
            @Override
            public void onFailure(Throwable t) {
                android.util.Log.w(TAG, "Error fetching updates data: " + t.getMessage());
            }

            @Override
            public void onSuccess(List<UpdateEntity> updateEntities) {
                //android.util.Log.v(TAG, "Count of Author updates found: " + updateEntities.size());

                for (UpdateEntity updateEntity : updateEntities) {
                    Update update = new Update(updateEntity.getText(), updateEntity.getMeta(), null, mCalendar);
                    update.setAuthorName(mFriend.getName());
                    update.setAvatar(mFriend.getAvatar());

                    try {
                        JSONObject attachment = updateEntity.getAttachment();
                        if (attachment != null && attachment.getString("_loc") != null) {
                            //android.util.Log.d(TAG, "_loc: " + attachment.getString("_loc"));
                            String uri =  mSharedClient.resource(attachment.getString("_loc")).getUriForResource();
                            //android.util.Log.d(TAG, "uri: " + uri);
                            update.setThumbnail(uri);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (KinveyException e) {
                        e.printStackTrace();
                    }

                    mUpdates.add(update);
                }

                //Collections.sort(mUpdates, LATEST_FIRST_ORDER);

                lv.setAdapter(new UpdateAdapter(AuthorViewActivity.this, mUpdates));

            }

        });

    }

    public void goBack(View view) {
        finish();
    }
}
