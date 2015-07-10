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

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import com.kinvey.android.Client;
import com.kinvey.java.LinkedResources.LinkedFile;
import com.kinvey.java.core.KinveyClientCallback;
import com.kinvey.java.model.KinveyMetaData;
import com.kinvey.sample.statusshare.MainActivity;
import com.kinvey.sample.statusshare.R;
import com.kinvey.sample.statusshare.model.UpdateEntity;

import java.io.*;

/**
 * @author edwardf
 * @since 2.0
 */
public class UpdateEditFragment extends KinveyFragment implements View.OnClickListener {


    private ImageView attachmentImage;
    private AlertDialog mDialog;

    private EditText updateText;
    private Bitmap image;
    private TextView title;
    private TextView attachmentTitle;

    public UpdateEditFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);


        final String[] items = new String[]{"From Camera", "From SD Card"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.select_dialog_item, items);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle("Select Image");
        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                if (item == 0) {
                    ((MainActivity) getActivity()).startCamera();


                    dialog.cancel();
                } else {

                    ((MainActivity) getActivity()).startFilePicker();
                    dialog.cancel();


                }
            }
        });

        mDialog = builder.create();

    }

    @Override
    public void onResume() {
        super.onResume();
        this.image = ((MainActivity) getActivity()).bitmap;


        if (this.image != null) {
            Log.i(Client.TAG, "setting imageview");
            attachmentImage.setBackgroundDrawable(null);
            attachmentImage.setImageBitmap(this.image);
        } else {

            Log.i(Client.TAG, "not setting imageview");
        }
    }

    @Override
    public int getViewID() {
        return R.layout.fragment_write_update;
    }

    @Override
    public void bindViews(View v) {

        attachmentImage = (ImageView) v.findViewById(R.id.preview);
        updateText = (EditText) v.findViewById(R.id.update);
        title = (TextView) v.findViewById(R.id.share_title);
        attachmentTitle = (TextView) v.findViewById(R.id.share_attach_title);
        title.setTypeface(getRoboto());
        attachmentTitle.setTypeface(getRoboto());
        updateText.setTypeface(getRoboto());

        attachmentImage.setOnClickListener(this);
    }


    public void doUpdate() {
        final ProgressDialog progressDialog = ProgressDialog.show(getActivity(), "",
                "Posting. Please wait...", true);

        byte[] byteArray = null;
        if (image != null) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byteArray = stream.toByteArray();
            try {
                stream.close();
            } catch (IOException e) {

            }
        }


        saveUpdateAttachment(progressDialog, byteArray, getClient().user().getId() + "_attachment_" + System.currentTimeMillis() + ".png");
    }


    public void saveUpdateAttachment(final ProgressDialog progressDialog, byte[] bytes, String filename) {
        UpdateEntity updateEntity = new UpdateEntity(getClient().user().getId());
        updateEntity.setText(updateText.getText().toString());
        updateEntity.getAcl().setGloballyReadable(true);

        android.util.Log.d(Client.TAG, "updateEntity.getMeta().isGloballyReadable() = " + updateEntity.getAcl().isGloballyReadable());

        if (bytes != null && filename != null) {
            Log.i(Client.TAG, "there is an attachment!");
            LinkedFile lf = new LinkedFile(filename);
            lf.addExtra("_public", true);
            KinveyMetaData.AccessControlList acl = new KinveyMetaData.AccessControlList();
            acl.setGloballyReadable(true);
            lf.addExtra("_acl", acl);
            updateEntity.putFile("attachment", lf);
        }
        final ByteArrayInputStream bais = ((bytes == null) ? null : new ByteArrayInputStream(bytes));
        if (bais != null){
            updateEntity.getFile("attachment").setInput(bais);
        }

        getClient().linkedData(MainActivity.COL_UPDATES, UpdateEntity.class).save(updateEntity, new KinveyClientCallback<UpdateEntity>() {

            @Override
            public void onSuccess(UpdateEntity result) {
                if (getActivity() == null){
                    return;
                }
                android.util.Log.d(Client.TAG, "postUpdate: SUCCESS _id = " + result.getId() + ", gr = " + result.getAcl().isGloballyReadable());
                progressDialog.dismiss();

                try {
                    bais.close();
                } catch (Exception e) {
                }

                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(updateText.getWindowToken(), 0);

                ((MainActivity) getActivity()).bitmap = null;

                if (getActivity() != null) {
                    ((MainActivity)getActivity()).setShareList(null);

                    ((MainActivity)getActivity()).replaceFragment(new ShareListFragment(), false);

//                    ((StatusShare)getSherlockActivity()).removeFragment(UpdateEditFragment.this);
//                    ((StatusShare)((StatusShare) getSherlockActivity()).removeFragment(getSherlockActivity().getSupportFragmentManager().);)
                }
            }

            @Override
            public void onFailure(Throwable e) {
                if (getActivity() == null){
                    return;
                }
                Log.d(Client.TAG, "failed to upload linked app data");
                e.printStackTrace();
                progressDialog.dismiss();
            }

        }, null
        );
//        } else {
//            Log.i(Client.TAG, "there is no attachment");
//        }
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
                doUpdate();
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onClick(View v) {
        if (v == attachmentImage) {
            mDialog.show();

        }
    }
}
