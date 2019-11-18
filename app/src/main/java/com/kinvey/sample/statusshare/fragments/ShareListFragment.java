/** 
 * Copyright (c) 2014 Kinvey Inc.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 * 
 */
package com.kinvey.sample.statusshare.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.kinvey.android.Client;
import com.kinvey.android.callback.KinveyListCallback;
import com.kinvey.android.callback.KinveyReadCallback;
import com.kinvey.android.store.DataStore;
import com.kinvey.android.store.UserStore;
import com.kinvey.java.Query;
import com.kinvey.java.core.KinveyClientCallback;
import com.kinvey.java.model.KinveyReadResponse;
import com.kinvey.java.query.AbstractQuery;
import com.kinvey.java.store.StoreType;
import com.kinvey.sample.statusshare.Constants;
import com.kinvey.sample.statusshare.MainActivity;
import com.kinvey.sample.statusshare.R;
import com.kinvey.sample.statusshare.component.UpdateAdapter;
import com.kinvey.sample.statusshare.model.UpdateEntity;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import timber.log.Timber;

/**
 * Display a persistent list of shared status updates.
 *
 * @author edwardf
 * @since 2.0
 */
public class ShareListFragment extends KinveyFragment {

    private DataStore<UpdateEntity> dataStore = null;

    private ListView mListView;
    private ProgressBar loading;
    private UpdateAdapter mAdapter;
    private TextView empty;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        dataStore = DataStore.collection(Constants.COL_UPDATES, UpdateEntity.class, StoreType.AUTO, getClient());
        getActivity().invalidateOptionsMenu();
    }

    @Override
    public int getViewID() {
        return R.layout.fragment_updates_list;
    }

    @Override
    public void bindViews(View v) {
        mListView = (ListView) v.findViewById(R.id.updateList);
        loading = (ProgressBar) v.findViewById(R.id.updateProgress);
        empty = (TextView) v.findViewById(R.id.empty_list);
        empty.setVisibility(View.GONE);
        loadUpdates();
    }

    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
        if (mListView == null){
            return;
        }
        if (((MainActivity) activity).getShareList() == null){
            loadUpdates();
        }
    }

    private void loadUpdates() {
        showListView(false);
        Query q = dataStore.query();
        q.setLimit(10);
        q.addSort("_kmd.lmt", AbstractQuery.SortOrder.DESC);
        dataStore.find(q, new KinveyReadCallback<UpdateEntity>() {
            @Override
            public void onSuccess(@Nullable KinveyReadResponse<UpdateEntity> result) {
                List<UpdateEntity> list = result.getResult();
                Timber.d("Count of updates found: " + list.size());
                for (UpdateEntity e : list) {
                    Timber.d("result -> " + e.toString());
                }
                if (getActivity() == null){ return; }
                ((MainActivity)getActivity()).setShareList(new ArrayList<UpdateEntity>());
                ((MainActivity)getActivity()).getShareList().addAll(list);
                if ( ((MainActivity)getActivity()).getShareList().size() == 0){
                    empty.setVisibility(View.VISIBLE);
                    loading.setVisibility(View.GONE);
                } else{
                    empty.setVisibility(View.GONE);
                    setAdapter();
                }
            }
            @Override
            public void onFailure(Throwable error) {
                Timber.w("Error fetching updates data: " + error.getMessage());
                showListView(true);
            }
        }, null);
        // new String[]{"author", "comments", "author"}, 3, true);
    }

    private void setAdapter() {
        if ( ((MainActivity)getActivity()).getShareList() == null) {
            Log.i(MainActivity.TAG, "not ready to set Adapter");
            return;
        }
        showListView(true);
        mAdapter = new UpdateAdapter(getActivity(),  ((MainActivity)getActivity()).getShareList(), getActivity().getLayoutInflater());
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                ((MainActivity) getActivity()).replaceFragment(UpdateDetailsFragment.newInstance(((MainActivity) getActivity()).getShareList().get(position)), true);

            }
        });
    }

    private void showListView(boolean show) {
        mListView.setVisibility(show ? View.VISIBLE : View.GONE);
        loading.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_share_list, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_status_post:
                ((MainActivity) getActivity()).replaceFragment(new UpdateEditFragment(), true);
                return (true);
            case R.id.menu_refresh:
                mAdapter = null;
                loadUpdates();
                return true;
            case R.id.menu_sign_out:
                UserStore.logout(getClient(), new KinveyClientCallback<Void>() {
                    @Override
                    public void onSuccess(Void result) {
                        openLoginScreen();
                    }
                    @Override
                    public void onFailure(@NotNull Throwable throwable) {
                        openLoginScreen();
                    }
                });
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void openLoginScreen() {
        ((MainActivity) getActivity()).replaceFragment(new LoginFragment(), true);
    }
}
