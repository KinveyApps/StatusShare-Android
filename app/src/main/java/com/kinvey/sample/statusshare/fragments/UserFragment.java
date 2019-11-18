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

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.kinvey.android.Client;
import com.kinvey.android.callback.KinveyReadCallback;
import com.kinvey.android.store.DataStore;
import com.kinvey.java.Query;
import com.kinvey.java.model.KinveyReadResponse;
import com.kinvey.java.query.AbstractQuery;
import com.kinvey.java.store.StoreType;
import com.kinvey.sample.statusshare.Constants;
import com.kinvey.sample.statusshare.R;
import com.kinvey.sample.statusshare.component.UpdateAdapter;
import com.kinvey.sample.statusshare.model.UpdateEntity;

import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import timber.log.Timber;

/**
 * @author edwardf
 * @since 2.0
 */
public class UserFragment extends KinveyFragment {

    public static final Integer UPDATES_LIST_SIZE = 5;

    private DataStore<UpdateEntity> dataStore = null;

    private List<UpdateEntity> updates;

    private ImageView avatar;
    private TextView author;
    private ListView lv;
    private TextView history_title;
    private ProgressBar loading;

    private UpdateEntity source;
    private Bitmap gravatar;

    public static UserFragment newInstance(UpdateEntity update) {
        UserFragment ret = new UserFragment();
        ret.setSource(update);
        ret.setHasOptionsMenu(true);
        return ret;
    }

    private UserFragment() {
    }

    @Override
    public int getViewID() {
        return R.layout.fragment_view_author;
    }

    @Override
    public void bindViews(View v) {
        avatar = (ImageView) v.findViewById(R.id.avatar);
        author = (TextView) v.findViewById(R.id.author_name);
        history_title = (TextView) v.findViewById(R.id.auther_updates_title);
        lv = (ListView) v.findViewById(R.id.author_updateList);
        loading = (ProgressBar) v.findViewById(R.id.author_list_loading);
        author.setTypeface(getRoboto());
        history_title.setTypeface(getRoboto());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        dataStore = DataStore.collection(Constants.COL_UPDATES, UpdateEntity.class, StoreType.AUTO, getClient());
    }

    public void populateViews() {
        if (this.source == null) {
            return;
        }
        author.setText(this.source.getAuthorName());
        setAvatar(this.source.getAuthorName());
        if (lv.getAdapter() == null || lv.getAdapter().getCount() == 0) {
            updateList();

        }
    }

    public void updateList() {
        loading.setVisibility(View.VISIBLE);
        lv.setVisibility(View.GONE);
        Query q = new Query();
        q.equals("_acl.creator", source.getAuthorID());
        q.addSort("_kmd.lmt", AbstractQuery.SortOrder.DESC);
        q.setLimit(UPDATES_LIST_SIZE);
        dataStore.find(q, new KinveyReadCallback<UpdateEntity>() {
            @Override
            public void onSuccess(@Nullable KinveyReadResponse<UpdateEntity> result) {
                List<UpdateEntity> list = result.getResult();
                Timber.d("Count of updates found: " + list.size());
                if (getActivity() == null) {
                    return;
                }
                for (UpdateEntity e : list) {
                    Timber.d("result -> " + e.toString());
                    Timber.d("attachment? -> " + (e.getFile(UpdateEntity.attachmentName) == null));
                    if (e.getFile(UpdateEntity.attachmentName) != null) {
                        Timber.d("outputstream ? -> " + (e.getFile(UpdateEntity.attachmentName).getOutput() == null));
                    }
                }
                updates = new ArrayList<UpdateEntity>();
                updates.addAll(list);
                lv.setAdapter(new UpdateAdapter(getActivity(), updates, getActivity().getLayoutInflater()));
                loading.setVisibility(View.GONE);
                lv.setVisibility(View.VISIBLE);
            }
            @Override
            public void onFailure(Throwable error) {
                Timber.w("Error fetching updates data: " + error.getMessage());
            }
        }, null);
        // new String[]{"author"}, 1, true
    }

    public void setSource(UpdateEntity ent) {
        this.source = ent;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
    }

    public void setAvatar(String gravatarID) {
        try {
            MessageDigest digester = MessageDigest.getInstance("MD5");
            byte[] digest = digester.digest(gravatarID.getBytes());

            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < digest.length; i++) {
                sb.append(Integer.toString((digest[i] & 0xff) + 0x100, 16).substring(1));
            }
            String url = new String("http://www.gravatar.com/avatar/" + sb.toString() + ".jpg?d=identicon");
            //android.util.Log.d(TAG, gravatarID + " = " + url);
            new DownloadAvatarTask().execute(url);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class DownloadAvatarTask extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... params) {
            try {
                gravatar = BitmapFactory.decodeStream((InputStream) new URL(
                        params[0]).getContent());
            } catch (MalformedURLException e) {
                Log.e(Client.TAG, "url for avatar download is bad", e);
            } catch (IOException e) {
                Log.e(Client.TAG, "failed to download avatar", e);
            }
            return gravatar;
        }

        @Override
        protected void onPostExecute(Bitmap grav) {

            if (gravatar != null) {
                avatar.setImageBitmap(gravatar);
            }
        }
    }
}
