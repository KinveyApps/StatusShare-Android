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

import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.kinvey.android.Client;
import com.kinvey.android.callback.KinveyListCallback;
import com.kinvey.java.Query;
import com.kinvey.samples.statusshare.R;
import com.kinvey.samples.statusshare.StatusShare;
import com.kinvey.samples.statusshare.component.UpdateAdapter;
import com.kinvey.samples.statusshare.model.Friend;
import com.kinvey.samples.statusshare.model.Update;
import com.kinvey.samples.statusshare.model.UpdateEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * @author edwardf
 * @since 2.0
 */
public class UserFragment extends KinveyFragment {

    public static final Integer UPDATES_LIST_SIZE = 5;


    private List<Update> mUpdates;
    private Friend mFriend;

    private ImageView avatar;
    private TextView author;
    private ListView lv;


    public static UserFragment newInstance(Update update){
        UserFragment ret = new UserFragment();
        ret.mFriend = new Friend(update.getAuthor(), update.getAuthorName());
        return ret;
    }


    private UserFragment(){}

    @Override
    public int getViewID() {
        return R.layout.author_view;
    }

    @Override
    public void bindViews(View v) {
        avatar = (ImageView) v.findViewById(R.id.avatar);
        author = (TextView) v.findViewById(R.id.author);

        lv = (ListView) v.findViewById(R.id.updateList);

        if (mFriend == null){
            ((StatusShare) getSherlockActivity()).replaceFragment(new ShareListFragment(), false);

        }

        avatar.setImageBitmap(mFriend.getAvatar());
        author.setText(mFriend.getName());

        updateList();



    }

//    /** Called when the activity is first created. */
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        mSharedClient = ((StatusShareApplication) getApplication()).getClient();
//        mCalendar = ((StatusShareApplication) getApplication()).getCalendar();
//
//        setContentView(R.layout.author_view);
//        Intent myIntent = getIntent();
//        mFriend = new Friend(myIntent.getStringExtra("authorId"), myIntent.getStringExtra("authorName"));
//
//        ImageView avatar = (ImageView) findViewById(R.id.avatar);
//
//        TextView author = (TextView) findViewById(R.id.author);
//
//    }

    public void updateList() {
        if (lv.getAdapter() != null) {
            ((UpdateAdapter) lv.getAdapter()).clear();
        }
        mUpdates = new ArrayList<Update>();

        Query q = getClient().appData("Updates", UpdateEntity.class).query();




        q.equals("_acl.creator", mFriend.getId());
//        q.orderByDescending("_kmd.lmt");  //TODO
        q.setLimit(UPDATES_LIST_SIZE);
        android.util.Log.v(Client.TAG, q.toString());

        getClient().appData("Updates", UpdateEntity.class).get(q, new KinveyListCallback<UpdateEntity>() {
            @Override
            public void onSuccess(UpdateEntity[] result) {

                for (UpdateEntity updateEntity : result) {
                    final Update update = new Update(updateEntity.getText(), updateEntity.getAcl(), updateEntity.getMeta(), null, getCalendar());
                    update.setAuthorName(mFriend.getName());
                    update.setAvatar(mFriend.getAvatar());

//                    if (updateEntity.getFile("attachment2") != null && updateEntity.getFile("attachment2").getFileData() != null){
//
//                    byte[] bytes = updateEntity.getFile("attachment2").getFileData();
//                    Bitmap bMap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
//
//                    update.setThumbnail(bMap);
//                    }


                    //-------
//                    GenericJson attachment = updateEntity.getAttachment();
//                    if (attachment != null && attachment.get("_loc") != null) {
//                        //android.util.Log.d(TAG, "_loc: " + attachment.getString("_loc"));
//
//                        getClient().file().getDownloadUrl(attachment.get("_loc").toString(), new KinveyUriCallback() {
//
//
//                            @Override
//                            public void onSuccess(UriLocResponse result) {
//                                //String uri =  mSharedClient.resource(attachment.getString("_loc")).getUriForResource();
//
//                                update.setThumbnail(result.getBlobTemporaryUri());
//                            }
//
//                            @Override
//                            public void onFailure(Throwable error) {
//                                //To change body of implemented methods use File | Settings | File Templates.
//                            }
//                        });
////                            String uri =  mSharedClient.resource(attachment.getString("_loc")).getUriForResource();
//                        //android.util.Log.d(TAG, "uri: " + uri);
////                            update.setThumbnail(uri);
//                    }
////                    } catch (JSONException e) {
////                        e.printStackTrace();
////                    } catch (Exception e) {
////                        e.printStackTrace();
////                    }

                    mUpdates.add(update);
                }

                lv.setAdapter(new UpdateAdapter(getSherlockActivity(), mUpdates, getSherlockActivity().getLayoutInflater()));
            }

            @Override
            public void onFailure(Throwable error) {
                android.util.Log.w(Client.TAG, "Error fetching updates data: " + error.getMessage());
            }
        });


//        MappedAppdata mappedAppdata = mSharedClient.mappeddata(UpdateEntity.class,"Updates");
//        mappedAppdata.setQuery(q);
//        mappedAppdata.fetch(new ListCallback<UpdateEntity>() {
//            @Override
//            public void onFailure(Throwable t) {
//            }
//
//            @Override
//            public void onSuccess(List<UpdateEntity> updateEntities) {
//                //android.util.Log.v(TAG, "Count of Author updates found: " + updateEntities.size());
//
//                for (UpdateEntity updateEntity : updateEntities) {
//                    Update update = new Update(updateEntity.getText(), updateEntity.getMeta(), null, mCalendar);
//                    update.setAuthorName(mFriend.getName());
//                    update.setAvatar(mFriend.getAvatar());
//
//                    try {
//                        JSONObject attachment = updateEntity.getAttachment();
//                        if (attachment != null && attachment.getString("_loc") != null) {
//                            //android.util.Log.d(TAG, "_loc: " + attachment.getString("_loc"));
//                            String uri =  mSharedClient.resource(attachment.getString("_loc")).getUriForResource();
//                            //android.util.Log.d(TAG, "uri: " + uri);
//                            update.setThumbnail(uri);
//                        }
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    } catch (KinveyException e) {
//                        e.printStackTrace();
//                    }
//
//                    mUpdates.add(update);
//                }
//
//                lv.setAdapter(new EndlessUpdateAdapter(AuthorViewActivity.this, mUpdates));
//
//            }
//
//        });

    }



}
