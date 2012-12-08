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
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.kinvey.statusshare.R;
import com.kinvey.statusshare.StatusShareApp;
import com.kinvey.statusshare.model.Friend;
import com.kinvey.statusshare.model.Update;
import com.kinvey.statusshare.model.UpdateEntity;

public class UpdatesActivity extends Activity {
    public static final String TAG = UpdatesActivity.class.getSimpleName();

    private KCSClient mSharedClient;
    private Calendar mCalendar;
    private Map<String, Friend> mFriends;
    private List<Update> mUpdates;

	private View mUpdateStatusView;
	private View mUpdateMainListView;

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
        mSharedClient = ((StatusShareApp) getApplication()).getKinveyService();
        mCalendar = ((StatusShareApp) getApplication()).getAppCalendar();
        
        mUpdateStatusView = findViewById(R.id.update_status);
        mUpdateMainListView = findViewById(R.id.updatesMain);
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
                
                showProgress(true);
                
                MappedAppdata updates = mSharedClient.mappeddata(UpdateEntity.class, "Updates");
                SimpleQuery query = new SimpleQuery();
                query.orderByDescending("_kmd.lmt");
                updates.setQuery(query);
				updates.fetch(UpdateEntity.class, new ListCallback<UpdateEntity>() {
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
                                    mSharedClient.resource(attachment.getString("_loc")).getUriForResource(new KinveyCallback<String>() {
										
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

                        showProgress(false);
                    }

                });

            }

        });

    }

    public void tryToLogout(View view) {
        mSharedClient.getActiveUser().logout();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra(LoginActivity.LOGGED_OUT, true);
        startActivity(intent);
        finish();
    }

    public void tryToWriteUpdate(View view) {
        startActivity(new Intent(this, WriteUpdateActivity.class));
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_sign_out:
	        mSharedClient.getActiveUser().logout();
	        Intent intent = new Intent(this, LoginActivity.class);
	        intent.putExtra(LoginActivity.LOGGED_OUT, true);
	        startActivity(intent);
	        finish();
			break;
		}
				
		return super.onOptionsItemSelected(item);
	}
	
    /**
	 * Shows the progress UI and hides the login form.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void showProgress(final boolean show) {
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(
					android.R.integer.config_shortAnimTime);

			mUpdateStatusView.setVisibility(View.VISIBLE);
			mUpdateStatusView.animate().setDuration(shortAnimTime)
					.alpha(show ? 1 : 0)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mUpdateStatusView.setVisibility(show ? View.VISIBLE
									: View.GONE);
						}
					});

			mUpdateMainListView.setVisibility(View.VISIBLE);
			mUpdateMainListView.animate().setDuration(shortAnimTime)
					.alpha(show ? 0 : 1)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mUpdateMainListView.setVisibility(show ? View.GONE
									: View.VISIBLE);
						}
					});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			mUpdateStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
			mUpdateMainListView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}
    
}
