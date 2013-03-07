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

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;

import com.kinvey.android.Client;
import com.kinvey.java.core.KinveyClientCallback;
import com.kinvey.samples.statusshare.R;
import com.kinvey.samples.statusshare.StatusShare;
import com.kinvey.samples.statusshare.model.UpdateEntity;

/**
 * @author edwardf
 * @since 2.0
 */
public class ShareFragment extends KinveyFragment {


    private Boolean mLocked;

    private String mPath;
    private ImageView mImageView;
    private AlertDialog mDialog;

    private static final int PICK_FROM_CAMERA = 1;
    private static final int PICK_FROM_FILE = 2;

    private EditText updateText;
    private Bitmap image;

    private ShareFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        getSherlockActivity().invalidateOptionsMenu();


    }

    public static ShareFragment newInstance(Bitmap bitmap, String path) {
        ShareFragment ret = new ShareFragment();
        ret.mPath = path;
        ret.image = bitmap;
        return ret;
    }


    @Override
    public int getViewID() {
        return R.layout.write_update;
    }

    @Override
    public void bindViews(View v) {

        mImageView = (ImageView) v.findViewById(R.id.preview);
        updateText = (EditText) v.findViewById(R.id.update);


        mLocked = false;
        mPath = null;


        final String[] items = new String[]{"From Camera", "From SD Card"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getSherlockActivity(), android.R.layout.select_dialog_item, items);
        AlertDialog.Builder builder = new AlertDialog.Builder(getSherlockActivity());

        builder.setTitle("Select Image");
        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                if (item == 0) {
                    ((StatusShare) getSherlockActivity()).startCamera();


                    dialog.cancel();
                } else {

                    ((StatusShare) getSherlockActivity()).startFilePicker();
                    dialog.cancel();


                }
            }
        });

        mDialog = builder.create();


    }


    public void toggleLock(View view) {
        mLocked = !mLocked;

        if (mLocked) {
            ((ImageButton) view).setImageResource(R.drawable.lock);
        } else {
            ((ImageButton) view).setImageResource(R.drawable.unlock);
        }
    }

    public void doAttachement() {
        mDialog.show();
    }

    public void doUpdate() {
        final ProgressDialog progressDialog = ProgressDialog.show(getSherlockActivity(), "",
                "Posting. Please wait...", true);

            saveUpdateText(progressDialog);
    }



    public void saveUpdateText(ProgressDialog progressDialog) {
        saveUpdateAttachment(progressDialog, null, null);
    }

    public void saveUpdateAttachment(final ProgressDialog progressDialog, byte[] bytes, String filename) {
        UpdateEntity updateEntity = new UpdateEntity();
        updateEntity.setText(updateText.getText().toString());
        updateEntity.getAcl().setGloballyReadable(!mLocked);

        android.util.Log.d(Client.TAG, "updateEntity.getMeta().isGloballyReadable() = " + updateEntity.getAcl().isGloballyReadable());
        getClient().appData("Updates", UpdateEntity.class).save(updateEntity, new KinveyClientCallback<UpdateEntity>() {

            @Override
            public void onFailure(Throwable e) {
                e.printStackTrace();
                progressDialog.dismiss();
                ((StatusShare) getSherlockActivity()).replaceFragment(new ShareListFragment(), false);
            }

            @Override
            public void onSuccess(UpdateEntity updateEntity) {
                android.util.Log.d(Client.TAG, "postUpdate: SUCCESS _id = " + updateEntity.getId() + ", gr = " + updateEntity.getAcl().isGloballyReadable());
                progressDialog.dismiss();
                ((StatusShare) getSherlockActivity()).replaceFragment(new ShareListFragment(), false);
            }

        });
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.update, menu);
    }

    @Override
    public boolean onOptionsItemSelected(com.actionbarsherlock.view.MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_send_post:
                doUpdate();
                break;
        }

        return super.onOptionsItemSelected(item);
    }


}
