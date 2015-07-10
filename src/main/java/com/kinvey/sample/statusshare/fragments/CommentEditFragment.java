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

import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import com.kinvey.java.core.KinveyClientCallback;
import com.kinvey.java.model.KinveyReference;
import com.kinvey.sample.statusshare.MainActivity;
import com.kinvey.sample.statusshare.R;
import com.kinvey.sample.statusshare.model.CommentEntity;
import com.kinvey.sample.statusshare.model.UpdateEntity;

/**
 * @author edwardf
 * @since 2.0
 */
public class CommentEditFragment extends KinveyFragment {

    private TextView title;
    private EditText comment;

    private UpdateEntity parent;


    public static CommentEditFragment newInstance(UpdateEntity parent){
        CommentEditFragment ret = new CommentEditFragment();
        ret.setHasOptionsMenu(true);
        ret.setParent(parent);
        return ret;
    }

    @Override
    public int getViewID() {
        return R.layout.fragment_edit_comment;
    }

    @Override
    public void bindViews(View v) {
        title = (TextView) v.findViewById(R.id.comment_title);
        comment = (EditText) v.findViewById(R.id.comment_text);

        title.setTypeface(getRoboto());
        comment.setTypeface(getRoboto());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_edit_share, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_send_post:
                saveComment();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void saveComment(){

        if (getActivity() != null && getActivity().getCurrentFocus() != null){
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
        }

        final CommentEntity ent = new CommentEntity(comment.getText().toString());
        ent.getAcl().setGloballyReadable(true);
        ent.setAuthor(getClient().user().getUsername());

        KinveyReference updateAuthor = new KinveyReference(parent.getAuthor().getCollection(), parent.getAuthor().getId());
        parent.setAuthor(updateAuthor);
        parent.resetCommentReferences();



//        getClient().appData(StatusShare.COL_UPDATES, UpdateEntity.class).getEntity(parent.getId(), new KinveyClientCallback<UpdateEntity>() {
//            @Override
//            public void onSuccess(UpdateEntity result) {

                getClient().appData(MainActivity.COL_COMMENTS, CommentEntity.class).save(ent, new KinveyClientCallback<CommentEntity>() {
                    @Override
                    public void onSuccess(CommentEntity commentEntity) {
                        if (getActivity() == null){
                            return;
                        }
                        parent.addComment(commentEntity);

                        getClient().appData(MainActivity.COL_UPDATES, UpdateEntity.class).save(parent, new KinveyClientCallback<UpdateEntity>() {
                            @Override
                            public void onSuccess(UpdateEntity updateEntity) {
                                if (getActivity() == null){
                                    return;
                                }
                                if (getActivity() != null){
                                    ((MainActivity)getActivity()).setShareList(null);
                                    ((MainActivity)getActivity()).replaceFragment(new ShareListFragment(), false);
                                }

                            }

                            @Override
                            public void onFailure(Throwable throwable) {
                                Log.e(MainActivity.TAG, "error adding update entity -> ", throwable);
                            }
                        });
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        Log.e(MainActivity.TAG, "error adding comment -> ", throwable);
                    }
                });           // }

//            @Override
//            public void onFailure(Throwable error) {
//                Log.e(StatusShare.TAG, "error adding update entity -> ", error);
//            }
       // });





    }

    public UpdateEntity getParent() {
        return parent;
    }

    public void setParent(UpdateEntity parent) {
        this.parent = parent;
    }
}
