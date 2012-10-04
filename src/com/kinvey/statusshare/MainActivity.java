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

package com.kinvey.statusshare;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.kinvey.KCSClient;
import com.kinvey.util.KinveyCallback;

public class MainActivity extends Activity {
    public static final String TAG = MainActivity.class.getSimpleName();

    private KCSClient mSharedClient;
    private TextView mStatus;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mStatus = (TextView) findViewById(R.id.status);
        mSharedClient = ((StatusShareApp) getApplication()).getKinveyService();

        mStatus.setText("Checking connection..");
        mSharedClient.isKinveyReachable(new KinveyCallback<Boolean>() {
            public void onFailure(Throwable t) {
                android.util.Log.w(TAG, "isKinveyReachable error: " + t.getMessage());
                mStatus.setText("Connection error");
             }

            public void onSuccess(Boolean b) {
                mStatus.setText("Connection good");
                MainActivity.this.startActivity(new Intent(MainActivity.this, LoginActivity.class));
                MainActivity.this.finish();
            }

        });
    }
}
