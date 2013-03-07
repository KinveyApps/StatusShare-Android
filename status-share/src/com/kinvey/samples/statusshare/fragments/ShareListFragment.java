/*
 * Copyright (c) 2013, Kinvey, Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.kinvey.samples.statusshare.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.kinvey.android.Client;
import com.kinvey.android.callback.KinveyListCallback;
import com.kinvey.java.Query;
import com.kinvey.java.User;

import com.kinvey.java.query.AbstractQuery;
import com.kinvey.samples.statusshare.*;
import com.kinvey.samples.statusshare.component.EndlessUpdateAdapter;
import com.kinvey.samples.statusshare.component.PullToRefreshListView;
import com.kinvey.samples.statusshare.component.UpdateAdapter;
import com.kinvey.samples.statusshare.model.Friend;
import com.kinvey.samples.statusshare.model.Update;
import com.kinvey.samples.statusshare.model.UpdateEntity;

import java.util.*;

/**
 * Display a persistent list of shared status updates.
 *
 * @author edwardf
 * @since 2.0
 */
public class ShareListFragment extends KinveyFragment {


    private PullToRefreshListView mListView;
    private ProgressBar mProgress;
    private EndlessUpdateAdapter mAdapter;
    private Map<String, Friend> mFriends;


    private List<UpdateEntity> shareList;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        getSherlockActivity().invalidateOptionsMenu();

    }


    @Override
    public int getViewID() {
        return R.layout.updates;
    }

    @Override
    public void bindViews(View v) {
        mListView = (PullToRefreshListView) v.findViewById(R.id.updateList);
        mProgress = (ProgressBar) v.findViewById(R.id.updateProgress);


        if (mFriends == null) {
            mFriends = new TreeMap<String, Friend>();
        }


        if (getUpdates() == null || getUpdates().size() == 0) {
            loadFriends();
            Query q = getClient().appData("Updates", UpdateEntity.class).query();
            q.setLimit(10);
            q.addSort("_kmd.lmt", AbstractQuery.SortOrder.DESC);
            loadUpdates(q);
        } else {
            setAdapter();
        }

    }


    private void loadFriends() {

        Query q = getClient().query();

        Log.i(Client.TAG, "about to retrieve all users");


        getClient().user().retrieve(q, new KinveyListCallback<User>() {
            @Override
            public void onFailure(Throwable t) {
                Log.w(Client.TAG, "Error fetching user data: " + t.getMessage());
                showListView(true);

            }

            @Override
            public void onSuccess(User[] users) {


                Log.i(Client.TAG, "Count of users found: " + users.length);
                for (User user : users) {
                    Friend friend = new Friend();
                    friend.setId((String) (user.getId()));
                    friend.setName(user.get("username").toString());
                    //android.util.Log.d(TAG, "friend : " + friend.getId() + ", " + friend.getUserName()  + ", " + user);
                    mFriends.put(friend.getId(), friend);

                }
                setAdapter();


            }

        });


    }

    private void loadUpdates(Query q) {


        getClient().appData("Updates", UpdateEntity.class).get(q, new KinveyListCallback<UpdateEntity>() {
            @Override
            public void onSuccess(UpdateEntity[] result) {
                android.util.Log.d(Client.TAG, "Count of updates found: " + result.length);

                for (UpdateEntity e : result){
                    Log.d(Client.TAG, "result -> " + e.toString());
                }


                shareList = new ArrayList<UpdateEntity>();
                shareList.addAll(Arrays.asList(result));
                setAdapter();

            }


            @Override
            public void onFailure(Throwable error) {
                Log.w(Client.TAG, "Error fetching updates data: " + error.getMessage());
                showListView(true);
            }
        });

    }

    private void setAdapter() {


        if (mFriends == null || shareList == null) {
            Log.i(StatusShare.TAG, "not ready to set Adapter");
            return;
        }


        if (mFriends.size() == 0 || shareList.size() == 0) {
            Log.i(StatusShare.TAG, "not ready to set Adapter");
            return;
        }

        if (getUpdates() == null){
            setUpdates(new ArrayList<Update>());
        }

        for (UpdateEntity updateEntity : shareList) {

            Log.i(Client.TAG, "update  -> " + updateEntity.toString());
            Log.i(Client.TAG, "update  -> " + updateEntity.getMeta().getLastModifiedTime());


            Update update = new Update(updateEntity.getText(), updateEntity.getAcl(), updateEntity.getMeta(), mFriends, getCalendar());
            Log.i(StatusShare.TAG, "Adding update: " + update.getText() + " - " + update.getSince() + " - " + update.getAuthorName() + " - " + update.getAuthor());

            getUpdates().add(update);
        }
        shareList = null;


        showListView(true);

        if (mListView.getAdapter() == null) {
            mListView.setAdapter(new UpdateAdapter(getSherlockActivity(), getUpdates(), getSherlockActivity().getLayoutInflater()));

            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


//                Intent newIntent = new Intent(getSherlockActivity(), AuthorViewActivity.class);
//                newIntent.putExtra("authorId", mUpdates.get(position).getAuthor());
//                newIntent.putExtra("authorName", mUpdates.get(position).getAuthorName());
                    //android.util.Log.d(TAG, "Click on: " + position + " - " + id + " - " + mUpdates.get(position).getAuthorName());
                    ((StatusShare) getSherlockActivity()).replaceFragment(UserFragment.newInstance(getUpdates().get(position)), true);
//                HomeActivity.this.startActivity(newIntent);
                }
            });

            mListView.setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {
                @Override
                public void onRefresh(PullToRefreshListView listView) {
                    loadFriends();


                    Query q = getClient().appData("Updates", UpdateEntity.class).query();
                    q.setLimit(10);
                    q.greaterThan("_kmd.lmt", getUpdates().get(0).lmt);


                    loadUpdates(q);
//                    mListView.completeRefreshing();


//                getClient().appData("Updates", UpdateEntity.class).get(q, new KinveyListCallback<UpdateEntity>() {
//                    @Override
//                    public void onSuccess(List<UpdateEntity> result) {
//                        android.util.Log.d(AbstractClient.TAG, "Count of updates found: " + result.size());
//
//
//
//
//                        for (UpdateEntity updateEntity : result) {
//                            Update update = new Update(updateEntity.getText(), updateEntity, mFriends, getCalendar());
//                            update.lmt = updateEntity.getLastModifiedTime();
//                            getUpdates().add(0, update);
//                        }
//
//
//
//                    }
//
//
//                    @Override
//                    public void onFailure(Throwable error) {
//                        Log.w(AbstractClient.TAG, "Error fetching updates data: " + error.getMessage());
//                        showListView(true);
//                        mListView.completeRefreshing();
//
//                    }
//                });


                    //To change body of implemented methods use File | Settings | File Templates.
                }
            });


        }

        mListView.completeRefreshing();


//        mListView.setAdapter(new EndlessUpdateAdapter(getSherlockActivity(), mUpdates, getSherlockActivity().getLayoutInflater()));


    }


    private void showListView(boolean show) {
        mListView.setVisibility(show ? View.VISIBLE : View.GONE);
        mProgress.setVisibility(show ? View.GONE : View.VISIBLE);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.home, menu);
    }

    @Override
    public boolean onOptionsItemSelected(com.actionbarsherlock.view.MenuItem item) {
        switch (item.getItemId()) {

            case R.id.menu_status_post:
                ((StatusShare) getSherlockActivity()).addFragment(ShareFragment.newInstance(null, null));
                return (true);
        }

        return super.onOptionsItemSelected(item);
    }


}
