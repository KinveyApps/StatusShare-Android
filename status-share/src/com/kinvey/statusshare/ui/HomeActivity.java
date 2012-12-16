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

package com.kinvey.statusshare.ui;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.kinvey.KinveyUser;
import com.kinvey.MappedAppdata;
import com.kinvey.persistence.query.SimpleQuery;
import com.kinvey.statusshare.R;
import com.kinvey.statusshare.StatusShareApp;
import com.kinvey.statusshare.model.Friend;
import com.kinvey.statusshare.model.Update;
import com.kinvey.statusshare.model.UpdateEntity;
import com.kinvey.util.KinveyCallback;
import com.kinvey.util.ListCallback;

public class HomeActivity extends BaseActivity {
    public static final String TAG = HomeActivity.class.getSimpleName();

    private Calendar mCalendar;
    private Map<String, Friend> mFriends;
    private List<Update> mUpdates;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.updates);
        mCalendar = ((StatusShareApp) getApplication()).getAppCalendar();
        
        
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
        updateList();
    }
    
    @Override
    protected void onPause() {
    	super.onPause();
    	showProgress(false);
    	//TODO: consider canceling http request to kinvey here via our own async task
    }

    public void updateList() {
    	
    	//TODO: add count on update collection to limit unnecessary refreshes

    	showProgress(true);
    	final ListView lv = (ListView) findViewById(R.id.updateList);
        if (lv.getAdapter() != null) {
            ((UpdateAdapter) lv.getAdapter()).clear();
        }

        mFriends = new TreeMap<String, Friend>();
        mUpdates = new ArrayList<Update>();

        mKinveyClient.userCollection().allUsers(new ListCallback<KinveyUser>() {
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

                MappedAppdata updates = mKinveyClient.mappeddata(UpdateEntity.class, "Updates");
                SimpleQuery query = new SimpleQuery();
                query.orderByDescending("_kmd.lmt");
                updates.setQuery(query);
				updates.fetch(new ListCallback<UpdateEntity>() {
                    @Override
                    public void onFailure(Throwable t) {
                        android.util.Log.w(TAG, "Error fetching updates data: " + t.getMessage());
                        showProgress(false);
                    }

                    @Override
                    public void onSuccess(List<UpdateEntity> updateEntities) {
                        //android.util.Log.d(TAG, "Count of updates found: " + updateEntities.size());

                        for (UpdateEntity updateEntity : updateEntities) {
                            final Update update = new Update(updateEntity.getText(), updateEntity.getMeta(), mFriends, mCalendar);
                            try {
                                JSONObject attachment = updateEntity.getAttachment();
                                if (attachment != null && attachment.getString("_loc") != null) {
                                    //android.util.Log.d(TAG, "_loc: " + attachment.getString("_loc"));
                                    mKinveyClient.resource(attachment.getString("_loc")).getUriForResource(new KinveyCallback<String>() {
										
										@Override
										public void onSuccess(String uri) {
											//android.util.Log.d(TAG, "uri: " + uri);
		                                    update.setThumbnail(uri);
										}
									});
                                    
                                }
                            } catch (JSONException e) {
                                Log.e(TAG,"downloading resources for updates", e);
                            }
                            mUpdates.add(update);
                        }

                        lv.setAdapter(new UpdateAdapter(HomeActivity.this, mUpdates));
                        lv.setOnItemClickListener(new OnItemClickListener() {
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Intent newIntent = new Intent(HomeActivity.this, AuthorViewActivity.class);
                                newIntent.putExtra("authorId", mUpdates.get(position).getAuthor());
                                newIntent.putExtra("authorName", mUpdates.get(position).getAuthorName());
                                //android.util.Log.d(TAG, "Click on: " + position + " - " + id + " - " + mUpdates.get(position).getAuthorName());
                                HomeActivity.this.startActivity(newIntent);
                            }
                        });

                        showProgress(false);
                    }


                });

            }

        });

    }

    
    private void showProgress(boolean show) {
    	findViewById(R.id.listProgress).setVisibility(show ? View.VISIBLE : View.GONE);
    	findViewById(R.id.updateList).setVisibility(show ? View.GONE : View.VISIBLE);
    }

    public void handleLogout(View view) {
        mKinveyClient.getActiveUser().logout();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra(LoginActivity.LOGGED_OUT, true);
        startActivity(intent);
        finish();
    }

    public void handlePostUpdate(View view) {
    	Log.d(TAG, "send click");
        startActivity(new Intent(this, WriteUpdateActivity.class));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        new MenuInflater(this).inflate(R.menu.home, menu);
        return super.onCreateOptionsMenu(menu);
    }
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menu_status_post:
	        	startActivity(new Intent(this, WriteUpdateActivity.class));
	        	return(true);
	        	
			case R.id.menu_about:
				showAboutDialog();
				return(true);
				
			case R.id.menu_refresh:
				updateList();
				return(true);
				
			case R.id.menu_sign_out:
		        mKinveyClient.getActiveUser().logout();
		        Intent intent = new Intent(this, LoginActivity.class);
		        intent.putExtra(LoginActivity.LOGGED_OUT, true);
		        startActivity(intent);
		        finish();
				return(true);
		}
				
		return super.onOptionsItemSelected(item);
	}
	
    
}
