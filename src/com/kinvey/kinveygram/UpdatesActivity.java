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
import java.util.Map;
import java.util.TreeMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.kinvey.KCSClient;
import com.kinvey.KinveyUser;
import com.kinvey.exception.KinveyException;
import com.kinvey.util.ListCallback;

public class UpdatesActivity extends Activity {
    public static final String TAG = UpdatesActivity.class.getSimpleName();

    private KCSClient mSharedClient;
    private Calendar mCalendar;
    private Map<String, Friend> mFriends;
    private List<Update> mUpdates;

    static final Comparator<Update> LATEST_FIRST_ORDER = new Comparator<Update>() {
        public int compare(Update u1, Update u2) {
            return u2.getDate().compareTo(u1.getDate());
        }
    };

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.updates);
        mSharedClient = ((KinveyGramApp) getApplication()).getKinveyService();
        mCalendar = ((KinveyGramApp) getApplication()).getAppCalendar();
    }

    @Override
    public void onStart() {
        super.onStart();
        updateList();
    }

    public void updateList() {
        final ListView lv = (ListView) findViewById(R.id.updateList);
        if (lv.getAdapter() != null) {
            ((UpdateAdapter) lv.getAdapter()).clear();
        }

        mFriends = new TreeMap<String, Friend>();
        mUpdates = new ArrayList<Update>();

        mSharedClient.userCollection().allUsers(new ListCallback<KinveyUser>() {
            @Override
            public void onFailure(Throwable t) {
                android.util.Log.w(TAG, "Error fetching user data: " + t.getMessage());
            }

            @Override
            public void onSuccess(List<KinveyUser> users) {
                //android.util.Log.d(TAG, "Count of users found: " + users.size());
                for (KinveyUser user : users) {
                    Friend friend = new Friend();
                    friend.setId((String) (user.getAttribute("_id")));
                    friend.setName(user.getUsername());
                    //android.util.Log.d(TAG, "friend : " + friend.getId() + ", " + friend.getUserName()  + ", " + user);
                    mFriends.put(friend.getId(), friend);
                }

                mSharedClient.mappeddata("Updates").fetch(UpdateEntity.class, new ListCallback<UpdateEntity>() {
                    @Override
                    public void onFailure(Throwable t) {
                        android.util.Log.w(TAG, "Error fetching updates data: " + t.getMessage());
                    }

                    @Override
                    public void onSuccess(List<UpdateEntity> updateEntities) {
                        //android.util.Log.d(TAG, "Count of updates found: " + updateEntities.size());

                        for (UpdateEntity updateEntity : updateEntities) {
                            Update update = new Update(updateEntity.getText(), updateEntity.getMeta(), mFriends, mCalendar);
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

                        Collections.sort(mUpdates, LATEST_FIRST_ORDER);

                        lv.setAdapter(new UpdateAdapter(UpdatesActivity.this, mUpdates));
                        lv.setOnItemClickListener(new OnItemClickListener() {
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Intent newIntent = new Intent(UpdatesActivity.this, AuthorViewActivity.class);
                                newIntent.putExtra("authorId", mUpdates.get(position).getAuthor());
                                newIntent.putExtra("authorName", mUpdates.get(position).getAuthorName());
                                //android.util.Log.d(TAG, "Click on: " + position + " - " + id + " - " + mUpdates.get(position).getAuthorName());
                                UpdatesActivity.this.startActivity(newIntent);
                            }
                        });

                    }

                });

            }

        });

    }

    public void tryToLogout(View view) {
        mSharedClient.getCurrentUser().logout();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    public void tryToWriteUpdate(View view) {
        startActivity(new Intent(this, WriteUpdateActivity.class));
    }

}
