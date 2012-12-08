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

import java.io.File;
import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.kinvey.statusshare.R;
import com.kinvey.statusshare.StatusShareApp;
import com.kinvey.statusshare.model.UpdateEntity;

public class WriteUpdateActivity extends Activity {
    public static final String TAG = WriteUpdateActivity.class.getSimpleName();

    protected KCSClient mSharedClient;
    private Boolean mLocked;

    private String mPath;
    private Uri mImageCaptureUri;
    private ImageView mImageView;
    private AlertDialog mDialog;

    private static final int PICK_FROM_CAMERA = 1;
    private static final int PICK_FROM_FILE = 2;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSharedClient = ((StatusShareApp) getApplication()).getKinveyService();
        mLocked = false;
        mPath = null;

        setContentView(R.layout.write_update);

        final String [] items = new String [] {"From Camera", "From SD Card"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.select_dialog_item, items);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Select Image");
        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                if (item == 0) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File file  = new File(Environment.getExternalStorageDirectory(),
                                        "tmp_avatar_" + String.valueOf(System.currentTimeMillis()) + ".jpg");
                    mImageCaptureUri = Uri.fromFile(file);

                    try {
                        intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
                        intent.putExtra("return-data", true);

                        startActivityForResult(intent, PICK_FROM_CAMERA);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    dialog.cancel();
                } else {
                    Intent intent = new Intent();

                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);

                    startActivityForResult(Intent.createChooser(intent, "Complete action using"), PICK_FROM_FILE);
                }
            }
        });

        mDialog = builder.create();

        mImageView = (ImageView) findViewById(R.id.preview);

    }

    public void toggleLock(View view) {
        mLocked = !mLocked;

        if (mLocked) {
          ((ImageButton) view).setImageResource(R.drawable.lock);
        } else {
          ((ImageButton) view).setImageResource(R.drawable.unlock);
        }
    }

    public void createAttachment(View view) {
        mDialog.show();
    }

    public void cancelUpdate(View view) {
        finish();
    }

    public void postUpdate(View view) {
        final ProgressDialog progressDialog = ProgressDialog.show(WriteUpdateActivity.this, "",
                        "Posting. Please wait...", true);
        if (mPath != null) {
            final File file = new File(mPath);
            final String ext = mPath.substring(mPath.lastIndexOf('.') + 1);
            final String type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext);
            if (ext != null && type != null) {
                final String assetname = "Updates-" + UUID.randomUUID().toString() + "-attachment." + ext;
                final KinveyResource resource = mSharedClient.resource(assetname);
                resource.upload(file, new KinveyCallback<Void>() {
                    @Override
                    public void onFailure(Throwable e) {
                        e.printStackTrace();
                        saveUpdate(progressDialog);
                    }

                    @Override
                    public void onSuccess(Void r) {
                        try {
                            //android.util.Log.d(TAG, "upload: SUCCESS, resource = " + resource.getUriForResource());

                            JSONObject attachment = new JSONObject();
                            attachment.put("_mime-type", type);
                            attachment.put("_loc", assetname);
                            attachment.put("_type", "resource");
                            //android.util.Log.d(TAG, "saveUpdate: " + attachment);
                            saveUpdate(progressDialog, attachment);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            } else {
                android.util.Log.w(TAG, "Skipping attachment because of indeterminate MIME type for file = " + mPath);
                saveUpdate(progressDialog);
            }
        } else {
            saveUpdate(progressDialog);
        }
    }

    public void saveUpdate(ProgressDialog progressDialog) {
        saveUpdate(progressDialog, null);
    }

    public void saveUpdate(final ProgressDialog progressDialog, JSONObject attachment) {
        UpdateEntity updateEntity = new UpdateEntity();

        updateEntity.setText(((EditText) findViewById(R.id.update)).getText().toString());
        updateEntity.setAttachment(attachment);
        KinveyMetadata md = new KinveyMetadata(null, null, !mLocked, null, mSharedClient);
        updateEntity.setMeta(md);

        android.util.Log.d(TAG, "updateEntity.getMeta().isGloballyReadable() = " + updateEntity.getMeta().isGloballyReadable());
        mSharedClient.mappeddata(UpdateEntity.class, "Updates").save(updateEntity, new ScalarCallback<UpdateEntity>() {

            @Override
            public void onFailure(Throwable e) {
                e.printStackTrace();
                progressDialog.dismiss();
                finish();
            }

            @Override
            public void onSuccess(UpdateEntity updateEntity) {
                android.util.Log.d(TAG, "postUpdate: SUCCESS _id = " + updateEntity.getId() +  ", gr = " + updateEntity.getMeta().isGloballyReadable());
                progressDialog.dismiss();
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }

        Bitmap bitmap = null;

        if (requestCode == PICK_FROM_FILE) {
            mImageCaptureUri = data.getData();
            mPath = getRealPathFromURI(mImageCaptureUri); //from Gallery

            if (mPath == null) {
                mPath = mImageCaptureUri.getPath(); //from File Manager
            }

            if (mPath != null) {
                bitmap = BitmapFactory.decodeFile(mPath);
            }
        } else {
            mPath = mImageCaptureUri.getPath();
            bitmap = BitmapFactory.decodeFile(mPath);
        }

        mImageView.setImageBitmap(bitmap);
    }

    public String getRealPathFromURI(Uri contentUri) {
        String [] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor  = managedQuery(contentUri, proj, null, null, null);

        if (cursor == null) {
            return null;
        }

        int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

        cursor.moveToFirst();

        return cursor.getString(columnIndex);
    }

}
